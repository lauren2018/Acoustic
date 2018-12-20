package parsley.acoustic.digital;


import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.R;
import parsley.acoustic.acmath.Complex;
import parsley.acoustic.signal.sdar_module;
import parsley.acoustic.view.basic.DataType;
import parsley.acoustic.view.basic.Param;
import parsley.acoustic.view.blocks.BlockView;
import parsley.acoustic.view.blocks.Port;

/**All of formulas of this OFDM module are built on Complex Sig */


public class OFDM extends sdar_module{
    /**params*/

    private int d_subcarrierNum;/**The subcarrier index should be [0,subcarrierNum-1]*/
    private ArrayList<Integer> d_occupiedCarrierIdx = new ArrayList<>();
    private ArrayList<Integer> d_pilotCarrierIdx = new ArrayList<>();

    /**in-out*/
    private Map<String, Integer> tag;
    private Complex [] in;
    private Complex [] out;

    public OFDM(Context context){
        setParamInOut();
    }


    /**if the subcarrier num is n, actually it needs to generate 2*n samples for real signal transmission,
     * the second n samples should be the conj sym of the first n samples
     * */
    public ArrayList<Complex> work(ArrayList<Complex> in){
        ArrayList<Complex> out = new ArrayList<>(2*in.size());
        Boolean [] carrier_state = get_carrier_state();
        int ofdm_symbol_num = in.size() / d_occupiedCarrierIdx.size() + 1;
        int samples_each_symbol = 2 * d_subcarrierNum;
        int ct = 0;
        for(int i = 0; i < ofdm_symbol_num; i++){
            for(int j = 0; j < d_subcarrierNum; j++){
                if (j == 0){
                    if (carrier_state[j] && ct < in.size()){
                        out.set(i*samples_each_symbol + j, new Complex(in.get(ct).getReal(),0));
                        out.set(i*samples_each_symbol + d_subcarrierNum, new Complex(in.get(ct).getImag(),0));
                        ct += 1;
                    }else{
                        out.set(i*samples_each_symbol + j, new Complex(0,0));
                        out.set(i*samples_each_symbol + d_subcarrierNum, new Complex(0,0));
                    }
                }else{
                    if (carrier_state[j] && ct < in.size()){
                        out.set(i*samples_each_symbol + j, new Complex(in.get(ct)));
                        out.set(i*samples_each_symbol + samples_each_symbol - j, (new Complex(in.get(ct))).conjugate());
                        ct += 1;
                    }else{
                        out.set(i*samples_each_symbol + j, new Complex(0,0));
                        out.set(i*samples_each_symbol + d_subcarrierNum, new Complex(0,0));
                    }
                }
            }
        }
        return out;
    }

    protected void setParamInOut(){
        /**param**/
        d_keys.add("subcarrier num");
        d_keys.add("occupied carriers");
        d_keys.add("pilot carriers");
        d_keys.add("pilot symbols");
        d_params.put(d_keys.get(0),new Param(d_keys.get(0), DataType.INTEGER,128,false));
        d_params.put(d_keys.get(1),new Param(d_keys.get(1), DataType.INTEGER,"()",true));
        d_params.put(d_keys.get(2),new Param(d_keys.get(2), DataType.INTEGER,"()", true));
        d_params.put(d_keys.get(3),new Param(d_keys.get(3), DataType.COMPLEX,null, true));
        /**in-out*/
        in_type = DataType.COMPLEX;
        out_type = DataType.COMPLEX;
        in_num = 1;
        out_num = 1;
    }

    private Boolean[] get_carrier_state(){
        Boolean [] b = new Boolean[d_subcarrierNum];
        for(int i = 0; i < d_occupiedCarrierIdx.size();i++){
            int idx = d_occupiedCarrierIdx.get(i);
            b[idx] = true;
        }
        return b;
    }


}
