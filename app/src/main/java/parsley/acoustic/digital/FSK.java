package parsley.acoustic.digital;

public class FSK {
    static public int [] byte_to_bit_1(byte [] byte_data){
        int [] res = new int [8*byte_data.length];
        int i = 0;
        for(byte x:byte_data){
            int tmpx = x;
            for(int j = 0; j < 8; j++) {
                int tmp = tmpx & 0x1;
                res[i] = tmp;
                i++;
                tmpx = tmpx >> 1;
            }
        }
        return res;
    }

    static public float [] byte_to_bit_4(byte [] byte_data){
        int [] res = new int [2*byte_data.length];
        float freq_interval = 44100/48;
        float [] freqList = new float[16];
        for(int i = 0; i < 16;i++){
            freqList[i] = (i+2)*freq_interval;
        }
        int i = 0;
        for(byte x:byte_data){
            int tmpx = x;
            res[i] = tmpx & 0x0F;
            res[i+1] = (tmpx & 0xF0)>>4;
            i += 2;
        }
        float [] freq = new float[res.length];
        for(i = 0; i < freq.length;i++){
            int tmp = res[i];
            freq[i] = freqList[tmp];
        }
        return freq;
    }

    static public float [] freq_to_samples(float [] freq, int samples_per_symbol, float sample_rate){
        float [] t = new float[samples_per_symbol];
        for(int i = 0; i < samples_per_symbol; i++){
            t[i] = i/sample_rate;
        }
        float [] res = new float [samples_per_symbol * freq.length];
        for(int i = 0; i < freq.length; i++){
            for(int j = 0; j < samples_per_symbol; j++){
                res[i*samples_per_symbol+j] = (float)Math.cos(2*Math.PI*freq[i]*t[j]);
            }
        }
        return  res;
    }
}
