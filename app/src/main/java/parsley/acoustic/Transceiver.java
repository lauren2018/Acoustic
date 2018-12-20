package parsley.acoustic;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import parsley.acoustic.acmath.Complex;
import parsley.acoustic.acmath.IFFT;
import parsley.acoustic.code.HammingEncoding;
import parsley.acoustic.code.Interleave;
import parsley.acoustic.code.LoRaMod;
import parsley.acoustic.digital.CyclicPrefix;
import parsley.acoustic.digital.FSK;
import parsley.acoustic.digital.QAM;
import parsley.acoustic.exp_signal.simpleChirp;
import parsley.acoustic.exp_signal.simpleCos;
import parsley.acoustic.exp_signal.simplePSK;
import parsley.acoustic.packet.PacketGen;
import parsley.acoustic.signal.chirp;
//import parsley.acoustic.signal.chirpView;
import parsley.acoustic.tools.Array;
import parsley.acoustic.view.blocks.Port;

import static android.media.AudioTrack.MODE_STREAM;

public class Transceiver implements Runnable{
    private int sample_rate = 44100;
    //private int minBufferSize = getMinBufferSize(sample_rate,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
    private int bufferSize = 32000;
    private AudioTrack audio = new AudioTrack(AudioManager.STREAM_MUSIC,sample_rate,
            AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize,MODE_STREAM);
    private int pream_sample_num = 256;
    private float fmin = (float)6e3, fmax = (float)16e3;
    private Boolean isOn;
    //data to be sent
    private float [] preamble_data;
    private int [] bit_data;
    private float [] payload_data;
    private short [] audio_data;
    private double cp_ratio = .25;

    //debug
    private FileWriter fw;
    private BufferedWriter bw;
    private int file_flag = 0; //0: audio_data  1: bit_data

    public Transceiver(){tx_config();}

    private void general_signal_config(){
//        simpleCos sc = new simpleCos();
//        float f = 4000;
//        int num = 256;
//        sc.setParams(f, num, (float)(1.0/sample_rate));
//        preamble_data = sc.genSignalSamples();
/**Chirp*/
        //        int f0 = 8000, f1 = 16000, num = 256;
//        float [] t = new float[num];
//        for(int i = 0; i < num; i++){
//            t[i] = (float)i/sample_rate;
//        }
//        simpleChirp sc = new simpleChirp();
//        preamble_data = simpleChirp.get_chirp(t, f0, (float)num / sample_rate, f1,0);
/**PSK*/
        simplePSK sc = new simplePSK();
        float f = 4410;
        sc.setParams(f, (float)(1.0/sample_rate));
        preamble_data = sc.genSignalSamples();
        int zeros = 2048;
        audio_data = new short[preamble_data.length + zeros];
        for(int i = 0; i < preamble_data.length+zeros;i++){
            if(i < preamble_data.length){
                audio_data[i] = (short)(preamble_data[i]*32767);
            }else{
                audio_data[i] = 0;
            }
        }
    }

    private void tx_config(){
        //test_cos_config();
        ofdm_config();
        //chirp_config();
        //fsk_config();
        //general_signal_config();
        //write_to_file();
        //lora_chirp_config_gnuradio();
    }

    private void ofdm_config(){
        //preamble
        float [] x = new float[pream_sample_num];
        for(int i = 0; i <pream_sample_num;i++){
            x[i] = (float)i/sample_rate;
        }
        chirp ch1 = new chirp(),ch2 = new chirp();
        float [] pre_first = ch1.get_chirp(x,fmin,(float)pream_sample_num/sample_rate,fmax,0);
        float phase = ch1.get_phase();
        float [] pre_last = ch2.get_chirp(x,fmax,(float)pream_sample_num/sample_rate,fmin,phase);
//        chirp ch3 = new chirp();
//        float [] test = ch3.get_chirp(x,fmax,(float)pream_sample_num/sample_rate,fmin,phase);
        preamble_data = new float[2*pream_sample_num];
        for(int i = 0; i < 2*pream_sample_num; i++){
            if(i < pream_sample_num){
                preamble_data[i] = pre_first[i];
            }else{
                preamble_data[i] = pre_last[i-pream_sample_num];
            }
        }

        //payload+pilot
        /*
        QAM qam = new QAM(16);
        Byte [] raw_data = {
                0x5d,0x71,0x62,0x57,0x21,0x79,0x32,0x1f,0xe,0x22,0x57,0x15,0x34,0x50,0x6c,0x4,0x18,0x11,0x45,
                0x13,0x35,0x7e,0x1,0x11,0x48,0x8,0x48,0x16,0x7b,0x63,0x5,0x7b,0x77,0x45,0x15,0x22,0x2b,0x43,
                0x10,0x5e,0x7e,0x4b,0x62,0x4c,0x5e,0x10,0x3f,0x63,0x24,0x55,0x21,0x63,0x2e,0x58,0x47,0x52,
                0x67,0x62,0x12,0xc,0x5,0x2a,0x33,0x22,0x1f,0xe,0x22,0x57,0x15,0x34,0x50,0x6c,0x4,0x18,0x11,0x45,
                0x13,0x35,0x7e,0x1,0x11,0x48,0x8,0x48,0x16,0x7b,0x63,0x5,0x7b,0x77,0x45,0x15,0x22,0x2b,0x43,
                0x10,0x5e,0x7e,0x4b,0x62,0x4c,0x5e,0x10,0x3f,0x63,0x24,0x55,0x21,0x63,0x2e,0x58,0x47,0x52,
                0x67,0x62,0x12,0xc,0x5,0x2a,0x33,0x22,0,0x3f,0x63,0x24,0x55,0x21,0x63,0x2e,0x58,0x47,0x52,
                0x67,0x62,0x12,0xc,0x5,0x2a,0x33,0x22,0x1f,0xe,0x22,0x67,0x62
        };
        int N = subcarrier_num, K = 2*N;
        ArrayList<Byte> data = new ArrayList<Byte>(Arrays.asList(raw_data));
        ArrayList<Complex> qam_symbols_payload = qam.work(data);

        Complex [] pilot = {new Complex(1,1), new Complex(1,-1),
                            new Complex(-1,1), new Complex(-1,-1)};
        int [] pilot_carriers = {-50,-15,15,50};
        for(int i = 0; i < pilot_carriers.length;i++){
            pilot_carriers[i] += subcarrier_num / 2;
        }
        int [] payload_carriers = new int [subcarrier_num-pilot_carriers.length];
        int j = 0;
        for(int i = 0; i < subcarrier_num;i++){
            if(j >= pilot.length || i != pilot_carriers[j]) {
                payload_carriers[i - j] = i;
            }else{
                j++;
            }
        }
*/
        int [] bits = read_from("QPSK.txt", 2048);
        Complex [] complex_sig = get_complex_from_bits(bits);
        int selected_complex_sig_num = 128;
        Complex [] selected_complex_sig = select_complex_sig(complex_sig, selected_complex_sig_num);
        int subcarrier_num = 128;
        double fmin = 6000;
        double fmax = 7000;
        double df = 22050 / subcarrier_num;
        int freqIdx_b = 0, freqIdx_e = 0;
        double f = 0;
        for(int i = 0; i < subcarrier_num; i++){
            if(f >= fmin && f -df < fmin){
                freqIdx_b = i;
            }
            if(f <= fmax && f +df > fmax){
                freqIdx_e = i;
            }
            f += df;
        }
        int occupy_subcarrier_num = freqIdx_e-freqIdx_b+1;
        int ofdm_symbols_num = selected_complex_sig_num / occupy_subcarrier_num + 1;
        Complex [] ofdm_samples_1 = new Complex[ofdm_symbols_num*subcarrier_num];
        int i = 0, j = 0;
        while(i < ofdm_symbols_num * subcarrier_num){
            if(j < selected_complex_sig_num && i % 128 >= freqIdx_b && i % 128 <= freqIdx_e){
                ofdm_samples_1[i] = selected_complex_sig[j];
                j++;
            }else{
                ofdm_samples_1[i] = new Complex(0,0);
            }
            i++;
        }
        /**conj reverse*/
        Complex [] ofdm_samples_2 = new Complex[2*ofdm_samples_1.length];
        int samples_each_symbol = 2*subcarrier_num;
        for(i = 0,j=0; i <= ofdm_samples_2.length-samples_each_symbol;i+=samples_each_symbol,j+=subcarrier_num){
            for(int i1 = 0; i1 < samples_each_symbol;i1++){
                if(i1 == 0){
                    ofdm_samples_2[i+i1] = new Complex(ofdm_samples_1[j].getReal(),0);
                }else if(i1 == subcarrier_num){
                    ofdm_samples_2[i+i1] = new Complex(ofdm_samples_1[j].getImag(),0);
                }else if(i1 > 0 && i1 < subcarrier_num){
                    ofdm_samples_2[i+i1] = ofdm_samples_1[j+i1];
                }else{
                    ofdm_samples_2[i+i1] = ofdm_samples_1[j+samples_each_symbol-i1].conjugate();
                }
            }
        }

        /**ifft*/
        int fft_size = samples_each_symbol;
        Complex [] ifft_samples = new Complex[ofdm_samples_2.length];
        for(i = 0; i <= ifft_samples.length-fft_size;i += fft_size){
            Complex [] ifft_samples_chunk = subarray(ofdm_samples_2, i,i+fft_size);
            Complex [] tmp = IFFT.ifft(ifft_samples_chunk);
            j = 0;
            for(Complex c: tmp){
                ifft_samples[i+j] = c;
                j += 1;
            }
        }

        /**get real and add cyclic prefix*/
        int zero_num_per_symbol = fft_size;
        int payload_data_length = ifft_samples.length *(fft_size+zero_num_per_symbol) / fft_size;
        double [] payload_data = new double[payload_data_length];
        j = 0;
        for(i = 0; i < payload_data.length;i++){
            if(i % (zero_num_per_symbol+fft_size) < zero_num_per_symbol){
                payload_data[i] = 0;
            }else{
                payload_data[i] = ifft_samples[j].getReal();
                j += 1;
            }
        }
        double maxV = 0;
        int maxI = 0;
        for(i = 0; i < payload_data_length;i++){
            if (Math.abs(payload_data[i]) > maxV){
                maxV = Math.abs(payload_data[i]);
                maxI = i;
            }
        }
        for(i = 0; i < payload_data_length;i++){
            payload_data[i] = payload_data[i] * 1 / maxV;
        }

        int zeronum = 1024;
        int data_len = preamble_data.length + zeronum + payload_data.length;


        audio_data = new short[data_len];
        short MAX_SHORT = 32767;
        //short MAX_SHORT_FORDATA=23173;
        for(i = 0; i < data_len;i++){
            if(i < preamble_data.length)
                audio_data[i] = (short)(preamble_data[i]*MAX_SHORT);
            else if(i < preamble_data.length+zeronum)
                audio_data[i] = 0;
            else
                audio_data[i] = (short)(payload_data[i-preamble_data.length-zeronum] *MAX_SHORT);
        }
        int cc = 1;
    }

    private Complex [] get_complex_from_bits(int [] bits){
        Complex [] complex_sig = new Complex[bits.length / 2];
        for(int i = 0; i < complex_sig.length;i++){
            complex_sig[i] = new Complex(bits[2*i], bits[2*i+1]);
        }
        return complex_sig;
    }

    private Complex [] select_complex_sig(Complex [] sig, int size){
        Complex [] ssig= new Complex[size];
        for(int i = 0; i < size;i++){
            ssig[i] = sig[i];
        }
        return ssig;
    }

    private Complex [] subarray(Complex[] data, int begin, int end){
        Complex [] res = new Complex[end-begin];
        int j = 0;
        for(int i = begin;i < end;i++){
            res[j] = data[i];
            j+=1;
        }
        return res;
    }

    private void test_cos_config(){
        //preamble
        float [] x = new float[pream_sample_num];
        for(int i = 0; i <pream_sample_num;i++){
            x[i] = (float)i/sample_rate;
        }
        chirp ch1 = new chirp(),ch2 = new chirp();
        float [] pre_first = ch1.get_chirp(x,fmin,(float)pream_sample_num/sample_rate,fmax,0);
        float phase = ch1.get_phase();
        float [] pre_last = ch2.get_chirp(x,fmax,(float)pream_sample_num/sample_rate,fmin,phase);
        chirp ch3 = new chirp();
        float [] test = ch3.get_chirp(x,fmax,(float)pream_sample_num/sample_rate,fmin,phase);
        preamble_data = new float[2*pream_sample_num];
        for(int i = 0; i < 2*pream_sample_num; i++){
            if(i < pream_sample_num){
                preamble_data[i] = pre_first[i];
            }else{
                preamble_data[i] = pre_last[i-pream_sample_num];
            }
        }
        //preamble_data = chirp.get_chirp(x,fmin,(float)pream_sample_num/sample_rate,fmax);

        int cos_sum = 16, samples_per_cos = 32;

        float [] t = new float[cos_sum*samples_per_cos];
        for(int j = 0; j < cos_sum; j++) {
            for (int i = 0; i < samples_per_cos; i++) {
                int idx = j * samples_per_cos + i;
                t[idx] = (float) Math.sin(Math.PI / 32 * i);
            }
        }
        //preamble


        int data_len = cos_sum * samples_per_cos + preamble_data.length;


        audio_data = new short[data_len];
        short MAX_SHORT = 32767;
        short MAX_SHORT_FORDATA=16000;
        for(int i = 0; i < data_len;i++){
            if(i < preamble_data.length)
                audio_data[i] = (short)(preamble_data[i]*MAX_SHORT);
            else
                audio_data[i] = (short)(t[i-preamble_data.length] * MAX_SHORT_FORDATA);

        }
        int xx = 1;


    }

    private void fsk_config(){
        int samples_per_symbol = 64;

        //preamble
        float [] x = new float[pream_sample_num];
        for(int i = 0; i <pream_sample_num;i++){
            x[i] = (float)i/sample_rate;
        }
        fmin = 12000;
        fmax = 16000;
        chirp ch1 = new chirp(),ch2 = new chirp();
        float [] pre_first = ch1.get_chirp(x,fmin,(float)pream_sample_num/sample_rate,fmax,0);
        float phase = ch1.get_phase();
        float [] pre_last = ch2.get_chirp(x,fmax,(float)pream_sample_num/sample_rate,fmin,phase);
        chirp ch3 = new chirp();
        float [] test = ch3.get_chirp(x,fmax,(float)pream_sample_num/sample_rate,fmin,phase);
        preamble_data = new float[2*pream_sample_num];
        for(int i = 0; i < 2*pream_sample_num; i++){
            if(i < pream_sample_num){
                preamble_data[i] = pre_first[i];
            }else{
                preamble_data[i] = pre_last[i-pream_sample_num];
            }
        }
        //payload
        byte [] raw_data = {
                0x5d,0x71,0x62,0x57,0x21,0x79,0x32,0x1f,0xe,0x22,0x57,0x15,0x34,0x50,0x6c,0x4,0x18,0x11,0x45,
                0x13,0x35,0x7e,0x1,0x11,0x48,0x8
        };
        float [] freq = FSK.byte_to_bit_4(raw_data);
        payload_data = FSK.freq_to_samples(freq,samples_per_symbol,sample_rate);
        audio_data = new short[preamble_data.length+payload_data.length];
        short MAX_SHORT = 32767;
        int zeronum = 256;
        int data_len = preamble_data.length + zeronum + payload_data.length;
        audio_data = new short[data_len];
        //short MAX_SHORT_FORDATA=23173;
        for(int i = 0; i < data_len;i++){
            if(i < preamble_data.length)
                audio_data[i] = (short)(preamble_data[i]*MAX_SHORT);
            else if(i < preamble_data.length+zeronum)
                audio_data[i] = 0;
            else
                audio_data[i] = (short)(payload_data[i-preamble_data.length-zeronum] *MAX_SHORT);
        }
    }

    private void lora_chirp_config_gnuradio(){
        Byte [] raw_data = {
                72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 58, 32, 48
        };

        Byte [] nybbles = new Byte[raw_data.length*2];
        for(int i = 0; i < raw_data.length;i++){
            nybbles[2*i] = (byte)((raw_data[i] & 0xF0) >> 4);
            nybbles[2*i+1] = (byte)(raw_data[i] & 0xF);
        }

        //new ArrayList<Element>(Arrays.asList(array));
        HammingEncoding hm1 = new HammingEncoding(new ArrayList<Byte>(Arrays.asList(nybbles)));
        ArrayList<Byte> data1 = hm1.getOutput();
        Byte [] data1_array = (Byte []) data1.toArray(new Byte[data1.size()]);
        String [] str= new String[data1_array.length];
        for(int j = 0; j < data1_array.length;j++){
            byte b = data1_array[j];
            String tmp = "";
            for(int i = 0; i < 8; i++){
                tmp = Integer.toString(0x1 & (b >> i)) + tmp;
            }
            str[j] = tmp;
        }

        write_to_file("tx_symbols_lora_hm",str);

        Interleave il1 = new Interleave(data1);
        ArrayList<Short> data2 = il1.getOutput();
        Short [] data2_array = (Short []) data2.toArray(new Short[data2.size()]);
        String [] str2= new String[data2_array.length];
        for(int j = 0; j < data2_array.length;j++){
            short b = data2_array[j];
            String tmp = "";
            for(int i = 0; i < 16; i++){
                tmp = Integer.toString(0x1 & (b >> i)) + tmp;
            }
            str2[j] = tmp;
        }
        write_to_file("tx_symbols_lora_il",str2);

        LoRaMod lrm = new LoRaMod(data2);
        ArrayList<Float> data3 = lrm.getOutput();
        Float [] data3_array = (Float [])data3.toArray(new Float[data3.size()]);
        write_to_file("tx_samples_lora_float",data3_array);

        audio_data = new short[data3.size()];
        for(int i = 0; i < audio_data.length;i++){
            audio_data[i] = (short)(data3.get(i) * 32767);
        }

        write_to_file("tx_samples_lora_short",audio_data);
    }


    private short[] chirp_config(int ampl){
        //preamble
        int fft_size = 64;
        int cp_len = 16;
        float df = (float) sample_rate / fft_size;
        int comb_per_symbol = 4;
        int [] freq_idx_3 = {13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28};
        int [] freq_idx_2 = {21,22,23,24,25,26,27,28};
        float [] t = new float[fft_size];
        float [][] upchirp = new float[comb_per_symbol][fft_size];
        float [] downchirp = new float[fft_size];
        float chirp_time = (float)(1.0 * fft_size / sample_rate);
        for(int i = 0; i < fft_size;i++){
            t[i] = (float)(i * 1.0 / sample_rate);
        }
        for(int i = 0; i < comb_per_symbol; i++){
            upchirp[i] = chirp.get_chirp(t,freq_idx_2[2*i]*df,chirp_time,freq_idx_2[2*i+1]*df,0);
        }

        /**data assembling**/
        int preamble_chirp_length = 128;
        int preamble_upchirp_num = 4;
        int preamble_length = preamble_chirp_length*preamble_upchirp_num;
        int zero_num = 64;
        int data_length = 2048*(fft_size+cp_len);
        float [] tp = new float[preamble_chirp_length];
        for(int i = 0; i < preamble_chirp_length;i++){
            tp[i] = (float)(i * 1.0 / sample_rate);
        }
        int ct = 0;
        short [] audio_data = new short[preamble_length + zero_num + data_length];
        int fmin = 12000, fmax = 16000;
        float [] preamble_chirp = chirp.get_chirp(tp,fmin,(float) (preamble_length*1.0)/sample_rate, fmax,0);
        for(int i = 0; i < preamble_upchirp_num;i++){
            for(float f:preamble_chirp){
                audio_data[ct] = (short)(f * ampl);
                ct += 1;
            }
        }
        for(int i = 0; i < zero_num;i++,ct++){
            audio_data[ct] = 0;
        }

        int [] bits = read_from("bits_2.txt",2048);

        for(int i = 0; i < bits.length; i++){
            int chirp_idx = bits[i];
            for(int j = 0; j < cp_len; j++, ct++){
                audio_data[ct] = (short)(ampl*upchirp[chirp_idx][j+fft_size-cp_len]);
            }
            for(int j = 0; j < fft_size;j++,ct++){
                audio_data[ct] = (short)(ampl*upchirp[chirp_idx][j]);
            }
        }
        return audio_data;

    }



    private void write_to_file(String name, short [] array){
        try{
            setOutputFile(name);
            for(short data : array) {
                try {
                    bw.write(Short.toString(data) +  '\n');
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bw.flush();
            bw.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void write_to_file(String name, Float [] array){
        try{
            setOutputFile(name);
            for(float data : array) {
                try {
                    bw.write(Float.toString(data) +  '\n');
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bw.flush();
            bw.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void write_to_file(String name, Short [] array){
        try{
            setOutputFile(name);
            for(float data : array) {
                try {
                    bw.write(Float.toString(data) +  '\n');
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bw.flush();
            bw.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void write_to_file(String name, Byte [] array){
        try{
            setOutputFile(name);
            for(byte data : array) {
                try {
                    bw.write(Float.toString(data) + '\n');
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bw.flush();
            bw.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void write_to_file(String name, String [] array){
        try{
            setOutputFile(name);
            for(String data : array) {
                try {
                    bw.write(data + '\n');
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bw.flush();
            bw.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private int [] read_from(String name, int size){
        File bitfile = new File(Environment.getExternalStorageDirectory()+"/"+name);
        int [] bits = new int[size];
        try{
            FileInputStream fis= new FileInputStream(bitfile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            String str;
            int i = 0;
            while((str = bufferedReader.readLine()) != null){
                bits[i] = Integer.parseInt(str);
                i += 1;
            }
        }catch (Exception e){

        }
        return bits;
    }

    @TargetApi(23)
    private void load_audio_file(String name){
        File audioFile = new File(Environment.getExternalStorageDirectory()+"/"+name);
        int SIZE = 5300000;
        chirp ch = new chirp();
        int sig_num = 64;
//        int fmin = 16538, fmax = 17916;

        float [] t = new float[sig_num];
        for(int i = 0; i < sig_num;i++){
            t[i] = (float)(i*1.0 / sample_rate);
        }

        short [] sig = chirp_config(100);
        int f = 16538;
//        float [] sig = new float[sig_num];
//        for(int i = 0; i < sig_num;i++){
//            sig[i] = (float)Math.cos(2*Math.PI*f*t[i]);
//        }

        try{
            FileInputStream fis= new FileInputStream(audioFile);
            //fis.skip(182350+0x4c);

            byte [] buf = new byte[SIZE];
            short [] buf_short = new short[SIZE/2];
            fis.read(buf);
            ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buf_short);
            write_to_file("canon_tx_raw.txt",buf_short);
            for(int i = 0; i < buf_short.length;i++){
                buf_short[i] = (short) (buf_short[i] *0.9);
            }
            for(int i = 0; i < buf_short.length;i++){
                buf_short[i] += sig[i%sig.length];
            }

            int slice_idx = 0;
            int slice_size = 4096;
            audio.play();
             while(isOn){
                audio.write(buf_short,slice_idx*slice_size,slice_size,AudioTrack.WRITE_BLOCKING);
                 slice_idx += 1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @TargetApi(23)
    private void send(Boolean with_music){
        if(!with_music){
            audio.play();
            //audio.write(preamble_data,0,preamble_data.length,AudioTrack.WRITE_NON_BLOCKING);
            while(isOn){
                //audio.write(preamble_data,0,preamble_data.length,AudioTrack.WRITE_NON_BLOCKING);
                audio.write(audio_data,0,audio_data.length,AudioTrack.WRITE_BLOCKING);
            }
        }else{
            load_audio_file("canon_1min.wav");
        }

    }

    @Override
    public void run(){
        send(false);
    }

    public void setStart(){
        isOn = true;
    }

    public void setStop(){
        isOn = false;
        audio.stop();
    }

    private void setOutputFile(String name) throws Exception{
        String path="";
        path = Environment.getExternalStorageDirectory()+"/"+name;
        File file = new File(path);
        file.createNewFile();
        fw = new FileWriter(path, false);
        try{
            bw = new BufferedWriter(fw);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
