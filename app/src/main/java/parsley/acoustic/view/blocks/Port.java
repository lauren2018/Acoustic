package parsley.acoustic.view.blocks;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;

import parsley.acoustic.tools.Array;
import parsley.acoustic.view.basic.DataType;


/**
 * Created by tomsp on 2017/12/20.
 */


public class Port{
    private PortView mPortView;
    private DataType mDataType;
    private int mPortType;
    private boolean isConnected = false;
    private ArrayList<Port> connectedPorts = new ArrayList<>();
    private ArrayList<LineGroup> lineGroups = new ArrayList<>();
    private int pid;
    private static int ct = 0;

    public Port(PortView v, DataType d, int portType){
        mPortView = v;
        mDataType = d;
        mPortType = portType;
        pid = ct;
        ct += 1;
    }

    public Port(DataType d, int portType){
        mDataType = d;
        mPortType = portType;
        pid = ct;
        ct += 1;
    }

    public DataType getDataType(){
        return mDataType;
    }

    public int getPortType(){return mPortType;}

    public boolean setDataType(DataType d){
        try{
            mDataType = d;
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean setPortType(int p){
        try{
            mPortType = p;
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void initPort(Context context, Rect rect){
        mPortView = new PortView(context, mDataType, this);
    }

    public void initPort(Context context, AttributeSet attrs, Rect rect){
        mPortView = new PortView(context, attrs,mDataType, this);
    }

    public PortView getPortView(){return mPortView;}

    public void setConnected(Port p){
        isConnected = true;
        connectedPorts.add(p);
    }
    /*here reserve is a boolean value indicates that if these two ports are disconnected temporarily or permanently:
       true: temporarily
       false: permanently
   */
    public void dismissConnected(Port portToRemove){
        isConnected = false;
        for(Port p:connectedPorts){
            if(portToRemove == p){
                connectedPorts.remove(p);
            }
        }
    }

    public boolean isConnected(){
        return isConnected;
    }

    public ArrayList<Port> getConnectedPorts(){return connectedPorts;}

    public boolean isInConnectedPorts(Port p){
        for(int i = 0; i < connectedPorts.size();i++){
            if(connectedPorts.get(i) == p){return true;}
        }
        return false;
    }

    public PortView initPortView(Context context){
        mPortView = new PortView(context, mDataType, this);
        return mPortView;
    }
    //public BlockView getParentBlockView(){return this.parentBlockView;}


    public ArrayList<LineGroup> getLineGroups() {
        return lineGroups;
    }

    public void addLineGroup(LineGroup lg){
        lineGroups.add(lg);
    }

    public void removeLineGroup(LineGroup lg){
        lineGroups.remove(lg);
    }

    public int getPid(){return pid;}


}
