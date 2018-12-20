package parsley.acoustic.signal;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import parsley.acoustic.R;
import parsley.acoustic.view.blocks.Port;
import parsley.acoustic.view.basic.DataType;
import parsley.acoustic.view.basic.Param;
import parsley.acoustic.view.blocks.BlockView;

public class chirpView /*extends BlockView */{
    static float [] y;
    static float phase;
//    private ArrayList<Port> mInPorts = new ArrayList<>();
//    private ArrayList<Port> mOutPorts = new ArrayList<>();

    public chirpView(Context context, ArrayList<String> keys, Map<String, Param> params){
       // super(context,keys,params);
//        mInPorts.add(new Port(DataType.COMPLEX, R.integer.IN_PORT));
//        mInPorts.add(new Port(DataType.INTEGER,R.integer.IN_PORT));
//        mInPorts.add(new Port(DataType.INTEGER,R.integer.IN_PORT));
//        mOutPorts.add(new Port(DataType.FLOAT,R.integer.OUT_PORT));
//        _initViews(context,keys,params, id);
//;//        inports.add(new Port(context,));
//        _initViews();
//        //this.mBlockViewId = id;
//        setParams(context);
    }
//    public chirp (){
//
//    }

    public static float[] get_chirp(float [] t,float f0, float t1, float f1, float initPhase ){
        float [] f = new float[t.length];
        y = new float[t.length];
        float integral = 0;
        int i = 0;
        for(float x: t){
            float tmpf = f0 + (f1 - f0) * x / t1;
            integral = f0 * x + x*x*(f1-f0) / (2*t1);
            f[i] = tmpf;
            y[i] = (float)Math.cos(2*Math.PI*integral+initPhase);
            i++;
        }
        phase = (float)(2*Math.PI*integral+initPhase);
        return y;
    }

    public static float get_phase(){
        return phase;
    }
}
