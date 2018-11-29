package parsley.acoustic.view.blocks;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import parsley.acoustic.R;

public class LineGroup{
    private Line [] lineViews;
    private int [] x;
    private int [] y;
    /**Indicating which side the lineGroup connected to the block (left or right)*/
    private int beginPortSide;
    private int endPortSide;
    private boolean withExtensionLine = false;
    private int extensionLineLength = 10;
    private int inPid;
    private int outPid;
    private final int maxLineNum = 3;
    private FrameLayout.LayoutParams lp;

    public LineGroup(Context context, int xb, int yb, int xe, int ye, int beginPortSide, int endPortSide, int inPortId, int outPortId){
        lineViews = new Line [maxLineNum];
        x = new int [maxLineNum+1];
        y = new int [maxLineNum+1];
        lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.LEFT | Gravity.TOP;


        if(beginPortSide != endPortSide){
            if(xb < xe){
                x[0] = xb; x[1] = (xb+xe)/2;x[2] = (xb+xe)/2; x[3] = xe;
                y[0] = yb; y[1] = yb;y[2] = ye; y[3] = ye;
            }else{
                withExtensionLine = true;
                xb = xb+extensionLineLength;
                xe = xe-extensionLineLength;
                x[0] = xb; x[1] = xb;x[2] = xe; x[3] = xe;
                y[0] = yb; y[1] = (yb+ye)/2;y[2] = (yb+ye)/2; y[3] = ye;
            }
        }else{

        }
        for(int i = 0; i < maxLineNum; i++){
            lineViews[i] = new Line(context,x[i],y[i],x[i+1],y[i+1]);
        }
        inPid = inPortId;
        outPid = outPortId;
    }

    private void setCoordinate(int x, int y){
        lp.leftMargin = x;
        lp.topMargin = y;
    }






    public int getInPid(){return inPid;}
    public int getOutPid(){return outPid;}
    public Line[] getLineViews(){return lineViews;}

}
