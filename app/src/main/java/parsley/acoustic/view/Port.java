package parsley.acoustic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.ScriptGroup;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import parsley.acoustic.R;
import parsley.acoustic.view.basic.DataType;
import parsley.acoustic.view.blocks.BlockView;


/**
 * Created by tomsp on 2017/12/20.
 */


public class Port{
    private PortView mPortView;
    private DataType mDataType;
    private int mPortType;

    Port(PortView v, DataType d, int portType){
        mPortView = v;
        mDataType = d;
        mPortType = portType;
    }

    Port(DataType d, int portType){
        mDataType = d;
        mPortType = portType;
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

    public void drawPort(Context context, Rect rect){
        mPortView = new PortView(context, rect);
    }

    public void drawPort(Context context, AttributeSet attrs, Rect rect){
        mPortView = new PortView(context, attrs, rect);
    }

    public PortView getPortView(){return mPortView;}
}
