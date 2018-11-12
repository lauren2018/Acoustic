package parsley.acoustic.digital;

import java.util.ArrayList;

import parsley.acoustic.acmath.Complex;
public class CyclicPrefix {
    private double cp_ratio;

    public CyclicPrefix(Double ratio){
        cp_ratio = ratio;
    }

    public float [] add_cp(float [] in){
        int num = in.length;
        int n = (int)((1+cp_ratio)*in.length);
        int k = n-num;
        float [] out = new float[n];
        for(int i = 0; i < n; i++){
            if(i<k){
                out[i] = in[i-k+num];
            }
            else{
                out[i] = in[i-k];
            }
        }
        return out;
    }
}
