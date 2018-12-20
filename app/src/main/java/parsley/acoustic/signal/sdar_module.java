package parsley.acoustic.signal;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.view.basic.DataType;
import parsley.acoustic.view.basic.Param;
import parsley.acoustic.view.blocks.BlockView;
import parsley.acoustic.view.blocks.Port;

public class sdar_module {
    protected ArrayList<String> d_keys = new ArrayList<>();
    protected Map<String, Param> d_params = new HashMap<>();
    protected ArrayList<Port> d_inPorts = new ArrayList<>();
    protected ArrayList<Port> d_outPorts = new ArrayList<>();
    protected DataType in_type;
    protected DataType out_type;
    protected int in_num;
    protected int out_num;
    /**View*/

    public sdar_module(){

    }

    protected void buildBlock(Context context){
        //bv = new BlockView(context,d_keys, d_params, d_inPorts,d_outPorts);
    }

    public ArrayList<String> getKeys(){return d_keys;}

    public Map<String, Param> getParams(){return d_params;}

    public DataType getInType(){return in_type;}

    public DataType getOutType(){return out_type;}

    public int getInNum(){return in_num;}

    public int getOutNum(){return out_num;}
}
