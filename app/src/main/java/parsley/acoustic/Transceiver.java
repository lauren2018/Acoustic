package parsley.acoustic;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import parsley.acoustic.acmath.Complex;
import parsley.acoustic.acmath.IFFT;
import parsley.acoustic.digital.CyclicPrefix;
import parsley.acoustic.digital.FSK;
import parsley.acoustic.digital.QAM;
import parsley.acoustic.signal.chirp;
import parsley.acoustic.tools.Array;

import static android.media.AudioTrack.MODE_STREAM;
import static android.media.AudioTrack.getMinBufferSize;

public class Transceiver implements Runnable{
    private int sample_rate = 44100;
    //private int minBufferSize = getMinBufferSize(sample_rate,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
    private int bufferSize = 32000;
    private AudioTrack audio = new AudioTrack(AudioManager.STREAM_MUSIC,sample_rate,
            AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize,MODE_STREAM);
    private int pream_sample_num = 256;
    private float fmin = (float)6e3, fmax = (float)16e3;
    private Boolean isOn;
    private int subcarrier_num = 128;
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

    private void tx_config(){
        //test_cos_config();
        // ofdm_config();
        //chirp_config();
        fsk_config();
        write_to_file();

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

        //payload+pilot
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

        Complex [] qam_symbols_data = new Complex[K];
        //payload setting (we regulate that pilot symbols cannot take the first and the (N // 2)th subchannel)
        j = 1;
        for(int i = 0; i <= payload_carriers.length;i++){
            if(i == 0)
                qam_symbols_data[0] = new Complex(qam_symbols_payload.get(0).getReal(),0);
            else if(i == payload_carriers.length)
                qam_symbols_data[subcarrier_num] = new Complex(qam_symbols_payload.get(0).getImag(),0);
            else {
                int tmpcarrier = payload_carriers[i];
                int tmpcarrier_sym = K - tmpcarrier;
                qam_symbols_data[tmpcarrier] = qam_symbols_payload.get(j);
                qam_symbols_data[tmpcarrier_sym] = qam_symbols_payload.get(j).conjugate();
                j++;
            }
        }
        //pilot setting
        j = 0;
        for(int i = 0; i < pilot.length;i++){
            int tmpcarrier = pilot_carriers[i];
            int tmpcarrier_sym = K - tmpcarrier;
            qam_symbols_data[tmpcarrier] = pilot[j];
            qam_symbols_data[tmpcarrier_sym] =pilot[j].conjugate();
            j++;
        }


//        for(int i = 0; i < K;i++){
//            if(i == 0)
//                qam_symbols_data[i] = new Complex(qam_symbols_payload.get(0).getReal(),0);
//            else if(i == N)
//                qam_symbols_data[i] = new Complex(qam_symbols_payload.get(0).getImag(),0);
//            else if(i < N)
//                qam_symbols_data[i] = qam_symbols_payload.get(i);
//            else
//                qam_symbols_data[i] = qam_symbols_payload.get(K-i).conjugate();
//        }
        Complex [] ofdm_symbols = IFFT.ifft(qam_symbols_data);

        float [] audio_data_nocp = new float[ofdm_symbols.length];
        float max = 0, tmp;
        for(int i = 0; i < ofdm_symbols.length;i++){
            tmp = (float)ofdm_symbols[i].getReal();
            audio_data_nocp[i] = tmp;
            if (Math.abs(tmp) > max){
                max = Math.abs(tmp);
            }
        }
        CyclicPrefix cp = new CyclicPrefix(cp_ratio);
        float [] float_audio_data = cp.add_cp(audio_data_nocp);
        //padding zeros and concatenate
        double time = 0.005;
        int zeronum = 256;
        int data_len = preamble_data.length + zeronum + float_audio_data.length;


        audio_data = new short[data_len];
        short MAX_SHORT = 32767;
        //short MAX_SHORT_FORDATA=23173;
        for(int i = 0; i < data_len;i++){
            if(i < preamble_data.length)
                audio_data[i] = (short)(preamble_data[i]*MAX_SHORT);
            else if(i < preamble_data.length+zeronum)
                audio_data[i] = 0;
            else
                audio_data[i] = (short)(float_audio_data[i-preamble_data.length-zeronum] *MAX_SHORT);
        }
        int cc = 1;
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

    private void chirp_test(){


    }

    private void chirp_config(){
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
        bit_data = FSK.byte_to_bit_1(raw_data);

        int f0 = 3000,fmid=6000, f1=9000;
        int sample_per_bit = 64;
        float [] t = new float[sample_per_bit];
        for(int i = 0; i <sample_per_bit;i++){
            t[i] = (float)i/sample_rate;
        }
        payload_data = new float[sample_per_bit * bit_data.length];
        //gen chirp0
        float [] thalf = new float[sample_per_bit / 2];
        for(int i = 0; i <sample_per_bit/2;i++){
            thalf[i] = (float)i/sample_rate;
        }

        chirp ch = new chirp();
        float [] chirp0_0 = ch.get_chirp(thalf, fmid,(float)sample_per_bit/2 / sample_rate,f0,0);
        phase = ch.get_phase();
        float [] chirp0_1 = ch.get_chirp(thalf, f0,(float)sample_per_bit / 2 / sample_rate,fmid,phase);
        float [] chirp0 = Array.concat(chirp0_0,chirp0_1);
        //gen chirp1
        ch = new chirp();
        float [] chirp1_0 = ch.get_chirp(thalf, fmid,(float)sample_per_bit /2/ sample_rate,f1,0);
        phase = ch.get_phase();
        float [] chirp1_1 = ch.get_chirp(thalf, f1,(float)sample_per_bit /2/ sample_rate,fmid,phase);
        float [] chirp1 = Array.concat(chirp1_0,chirp1_1);

        for(int i = 0; i < bit_data.length;i++){
            int bit = bit_data[i];
            if(bit == 0){
                Array.replace(payload_data, chirp0,sample_per_bit*i, sample_per_bit);
            }
            else{
                Array.replace(payload_data, chirp1,sample_per_bit*i, sample_per_bit);
            }

        }

        int zeronum = 256;
        int data_len = preamble_data.length + zeronum + payload_data.length;
        audio_data = new short[data_len];
        short MAX_SHORT = 32767;
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

    private void write_to_file(){
        try{
            setOutputFile();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(file_flag == 0){
            for(short data : audio_data){
                try{
                    bw.write(Short.toString(data)+" ");
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            for(int data : bit_data){
                try{
                    bw.write(Integer.toString(data)+" ");
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        try{
            bw.flush();
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @TargetApi(23)
    private void send(){
        audio.play();
        //audio.write(preamble_data,0,preamble_data.length,AudioTrack.WRITE_NON_BLOCKING);
        while(isOn){
            //audio.write(preamble_data,0,preamble_data.length,AudioTrack.WRITE_NON_BLOCKING);
            audio.write(audio_data,0,audio_data.length,AudioTrack.WRITE_BLOCKING);
        }
    }

    @Override
    public void run(){
        send();
    }

    public void setStart(){
        isOn = true;
    }

    public void setStop(){
        isOn = false;
        audio.stop();
    }

    private void setOutputFile() throws Exception{
        String path="";
        if(file_flag == 0){
            path = Environment.getExternalStorageDirectory()+"/"+"tx_audio.txt";
        }else{
            path = Environment.getExternalStorageDirectory()+"/"+"tx_audio_bits.txt";
        }
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
