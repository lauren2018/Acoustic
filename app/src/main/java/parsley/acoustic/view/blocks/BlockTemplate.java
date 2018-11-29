package parsley.acoustic.view.blocks;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Map;

import parsley.acoustic.R;
import parsley.acoustic.view.basic.Param;

public class BlockTemplate extends LinearLayout {
    private Paint mModulePaint;
    private Paint mBoundPaint;
    private Paint mTextPaint;
    private Paint mInPortPaint;
    private Paint mOutPortPaint;
    //The following three params determine the height and width of the block
    private int mInPortNum = 1;
    private int mOutPortNum = 1;
    private int mTextRows = 1;

    //view params
    private final int mRowHeight = 80;
    private int mBlockHeight;
    private int mViewHeight;
    private int mBlockWidth = 400;
    private int mMarginWidth=20;
    private int mMarginHeight=20;
    private int strokeWidth = 10;
    private int isAnyInPort = 0;
    private int isAnyOutport = 0;
    private ArrayList<Port> mInPorts = new ArrayList<>();
    private ArrayList<Port> mOutPorts = new ArrayList<>();
    private int blockRectLeft;
    private int blockRectTop;
    private int blockRectRight;
    private int blockRectBottom;

    //sub ViewGroup/view
    private LinearLayout mInPortLayout;
    private InfoBlock mInfoBlock;
    private LinearLayout mOutPortLayout;

//
//    public BlockTemplate(Context context){
//        super(context);
//    }
//    public BlockTemplate(Context context, AttributeSet attrs){
//        super(context,attrs);
//    }

    public BlockTemplate(Context context, ArrayList<String> keys, Map<String, Param> params,
                         ArrayList<Port> inPorts, ArrayList<Port> outPorts){
        super(context);
        initLayout(keys,params,inPorts,outPorts);
//        setParams(keys,params,inPorts,outPorts);
        //setWillNotDraw(false);

    }

    public BlockTemplate(Context context, AttributeSet attrs,  ArrayList<String> keys, Map<String, Param> params,
                         ArrayList<Port> inPorts, ArrayList<Port> outPorts) {
        super(context, attrs);
        initLayout(keys, params, inPorts, outPorts);
//        setParams(keys, params,inPorts, outPorts);
        setWillNotDraw(false);
    }

    private void initLayout(ArrayList<String> keys, Map<String, Param> params,
                       ArrayList<Port> inPorts, ArrayList<Port> outPorts){
        setParams(keys, params,inPorts, outPorts);
        mInfoBlock = new InfoBlock(getContext(),keys, params, inPorts, outPorts);
        mBlockHeight = mInfoBlock.getBlockHeight();
        mViewHeight = mInfoBlock.getViewHeight();
        if(inPorts.size() > 0){
            mInPortLayout = new LinearLayout(getContext());
            addInPorts();
            this.addView(mInPortLayout);
        }
        this.addView(mInfoBlock);
        if(outPorts.size() > 0){
            mOutPortLayout = new LinearLayout(getContext());
            addOutPorts();
            this.addView(mOutPortLayout);
        }
    }

    @TargetApi(19)
    private void addInPorts(){
        int inportInterval = (mViewHeight - mInPorts.size()* PortView.PORT_HEIGHT) / (mInPorts.size()+2);
        int inportMatginHeight = (int)(1.5*inportInterval);
        /**Set layout parameters for mInPortLayout*/
        LayoutParams params = new LayoutParams(PortView.PORT_WIDTH, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0,inportMatginHeight,0,inportMatginHeight);
        mInPortLayout.setLayoutParams(params);
        //mInPortLayout.setPadding(0,0,0,40);
        mInPortLayout.setOrientation(VERTICAL);
        /**Set layout parameters for each portview of inports, mainly to set the margin between two portviews*/
        LayoutParams pvParams = new LayoutParams(PortView.PORT_WIDTH,PortView.PORT_HEIGHT);
        pvParams.setMargins(0,0,0,inportInterval);
        for(int i = 0; i < mInPorts.size();i++){
            PortView pv = mInPorts.get(i).initPortView(getContext());
            pv.setLayoutParams(pvParams);
            mInPortLayout.addView(pv);
        }
    }

    @TargetApi(19)
    private void addOutPorts(){
        int outportInterval = (mViewHeight - mOutPorts.size()* PortView.PORT_HEIGHT) / (mOutPorts.size()+2);
        int outportMatginHeight = (int)(1.5*outportInterval);
        /**Set layout parameters for mOutPortLayout*/
        LayoutParams params = new LayoutParams(PortView.PORT_WIDTH, mBlockHeight);
        params.setMargins(0,outportMatginHeight,0,outportMatginHeight);
        mOutPortLayout.setLayoutParams(params);
        mOutPortLayout.setOrientation(VERTICAL);

        /**Set layout parameters for each portview of outports, mainly to set the margin between two portviews*/
        LayoutParams pvParams = new LayoutParams(PortView.PORT_WIDTH,PortView.PORT_HEIGHT);
        pvParams.setMargins(0,0,0,outportInterval);
        for(int i = 0; i < mOutPorts.size();i++){
            PortView pv = mOutPorts.get(i).initPortView(getContext());
            mOutPortLayout.addView(pv);
        }
    }

    private void setParams(ArrayList<String> keys, Map<String, Param> params, ArrayList<Port> inPorts, ArrayList<Port> outPorts){
        int inPortNum = inPorts.size();
        int outPortNum = outPorts.size();
        mBlockHeight = mRowHeight * (keys.size());
        mBlockHeight = Math.max(mBlockHeight,Math.max(inPortNum,outPortNum)*(15+ PortView.PORT_HEIGHT));
        int len = 0;
        for(int i = 0; i < keys.size();i++){
            if(keys.get(i).length() > len)
                len = keys.get(i).length();
        }
        mInPortNum = inPortNum;
        mOutPortNum = outPortNum;
        //mBlockWidth = len*(2*fontsize/3);
        if(mInPortNum > 0) isAnyInPort = 1;
        if(mOutPortNum > 0) isAnyOutport = 1;
        for(int i = 0; i < inPorts.size(); i++){
            mInPorts.add(inPorts.get(i));
        }
        for(int i = 0; i < outPorts.size(); i++){
            mOutPorts.add(outPorts.get(i));
        }

        blockRectLeft = mMarginWidth+ isAnyInPort* PortView.PORT_WIDTH;
        blockRectTop = mMarginHeight;
        blockRectRight = mMarginWidth+ isAnyInPort* PortView.PORT_WIDTH+mBlockWidth;
        blockRectBottom = mMarginHeight+mBlockHeight;
        setAllPaints();
    }

    private void setAllPaints(){
        Resources resources = getResources();
        mModulePaint = new Paint();
        mModulePaint.setColor(resources.getColor(R.color.lavender));
        mModulePaint.setStyle(Paint.Style.FILL);
        //mModulePaint.setAlpha(30);
//        mModulePaint.setStrokeWidth(10);
        mBoundPaint = new Paint();
        mBoundPaint.setColor(resources.getColor(R.color.black));
        mBoundPaint.setStyle(Paint.Style.FILL);
//        mBoundPaint.setStrokeWidth(strokeWidth);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextSize(fontsize);
    }

    private LayoutParams getWrapContentParams(){
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public int getBlockWidth(){
        return mBlockWidth;
    }

    public int getBlockHeight(){
        return mBlockHeight;
    }

    public int getBlockRectLeft(){return blockRectLeft;}

    public int getBlockRectTop(){return blockRectTop;}

    public int getBlockRectRight(){return blockRectRight;}

    public int getBlockRectBottom(){return blockRectBottom;}

}
