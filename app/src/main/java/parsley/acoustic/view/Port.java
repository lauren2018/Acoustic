package parsley.acoustic.view;

import java.util.ArrayList;

import parsley.acoustic.view.basic.DataType;

/**
 * Created by tomsp on 2017/12/20.
 */

public class Port {
    private String name;
    private DataType type;
    private boolean isConnected = false;
    private ArrayList<BasicBlock> connectingBlocks;

    public String getName(){return name;}

    public String setName(String name){return this.name = name;}

    public String getType(){return type.getName();}

    public DataType setType(DataType type){return this.type = type;}

    public Boolean connect(BasicBlock b){
        connectingBlocks.add(b);
        isConnected = true;
        return true;
    }

}
