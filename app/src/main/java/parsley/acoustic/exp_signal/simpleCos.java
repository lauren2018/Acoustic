package parsley.acoustic.exp_signal;

public class simpleCos {
    private float [] signalSamples;
    private float freq;
    private int sampleNum;
    private float T;

    public void setParams(float f, int sn, float t){
        freq = f;
        sampleNum = sn;
        T = t;
    }

    public float [] genSignalSamples(){
        signalSamples = new float[sampleNum];
        for(int i = 0; i < sampleNum; i++){
            signalSamples[i] = (float)Math.cos(2*Math.PI*freq*T*i);
        }
        return signalSamples;
    }

    public float [] getSignalSamples(){
        return signalSamples;
    }
}
