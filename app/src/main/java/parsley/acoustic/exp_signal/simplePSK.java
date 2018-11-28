package parsley.acoustic.exp_signal;

public class simplePSK {
    private float [] signalSamples;
    private float freq;
    private int sampleNum;
    private float T;

    public void setParams(float f, float t){
        freq = f;
        T = t;
    }

    public float [] genSignalSamples(){
        int [] x = {0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,1,0,1,1,0,1,0,1,1,0,0};
        float sampleRate = (float)1.0/T;
        int samplesPerCycle = (int)((sampleRate+1)/freq);

        signalSamples = new float[x.length*samplesPerCycle];
        for(int i = 0; i < x.length;i++){
            int symbol = x[i];
            for(int j = 0; j < samplesPerCycle;j++){
                signalSamples[i*samplesPerCycle+j] = (float)Math.cos(2*Math.PI*freq*T*j+(1-symbol)*Math.PI);
            }
        }
        return signalSamples;
    }

    public float [] getSignalSamples(){
        return signalSamples;
    }
}
