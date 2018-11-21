package parsley.acoustic.view.blocks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.R;
import parsley.acoustic.view.basic.Param;

/**
 * Created by tomsp on 2017/12/20.
 */

public class InfoBlock extends View {
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
    private int mBlockWidth = 400;
    private int mViewHeight;
    private int mViewWidth;
//    private int mMarginWidth=20;
//    private int mMarginHeight=20;
    private int mStrokeWidth = 10;
    private int isAnyInPort = 0;
    private int isAnyOutport = 0;
    private ArrayList<Port> mInPorts = new ArrayList<>();
    private ArrayList<Port> mOutPorts = new ArrayList<>();
    private int blockRectLeft;
    private int blockRectTop;
    private int blockRectRight;
    private int blockRectBottom;

    public InfoBlock(Context context){
        super(context);
    }
    public InfoBlock(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public InfoBlock(Context context, ArrayList<String> keys, Map<String, Param> params,
                         ArrayList<Port> inPorts, ArrayList<Port> outPorts){
        super(context);
        setParams(keys,params,inPorts,outPorts);
        //setWillNotDraw(false);

    }

    public InfoBlock(Context context, AttributeSet attrs,  ArrayList<String> keys, Map<String, Param> params,
                         ArrayList<Port> inPorts, ArrayList<Port> outPorts) {
        super(context, attrs);
        setParams(keys, params,inPorts, outPorts);
        setWillNotDraw(false);
    }

    private void setParams(ArrayList<String> keys, Map<String, Param> params, ArrayList<Port> inPorts, ArrayList<Port> outPorts){
        int inPortNum = inPorts.size();
        int outPortNum = outPorts.size();
        mBlockHeight = mRowHeight * (keys.size());
        mBlockHeight = Math.max(mBlockHeight,Math.max(inPortNum,outPortNum)*(15+ PortView.PORT_HEIGHT));
        mViewHeight = mBlockHeight + 2*mStrokeWidth;
        mViewWidth = mBlockWidth + 2*mStrokeWidth;
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

        blockRectLeft = mStrokeWidth;
        blockRectTop = mStrokeWidth;
        blockRectRight = mStrokeWidth+ +mBlockWidth;
        blockRectBottom = mStrokeWidth+mBlockHeight;
        setAllPaints();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int desiredWidth = mViewWidth, desiredHeight = mViewHeight;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width,height;
        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }
        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Rect rec = new Rect(blockRectLeft,blockRectTop,blockRectRight,blockRectBottom);
        int bound_left, bound_right, bound_top, bound_bottom;
        bound_left = blockRectLeft - mStrokeWidth;
        bound_top = blockRectTop - mStrokeWidth;
        bound_right = blockRectRight+mStrokeWidth;
        bound_bottom = blockRectBottom + mStrokeWidth;
        //bounding
        Rect boundingRect = new Rect(bound_left,bound_top,bound_right,bound_bottom);
        canvas.drawRect(boundingRect,mBoundPaint);
        //info block
        canvas.drawRect(rec, mModulePaint);
        Rect [] inPortViews = new Rect[mInPortNum];
        Rect [] outPortViews = new Rect[mOutPortNum];
        int inportInterval = (mBlockHeight - mInPortNum* PortView.PORT_HEIGHT) / (mInPortNum+2);
        int outportInterval = (mBlockHeight - mOutPortNum* PortView.PORT_HEIGHT) / (mOutPortNum+2);
        int inMarginVertical = (int)(1.5*inportInterval);
        int outMarginVertical = (int)(1.5*outportInterval);
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

    public int getBlockHeight(){return mBlockHeight;}
    public int getViewHeight(){return mViewHeight;}

//    public void updateParams(ArrayList<String> keys, Map<String,Param> params){
//        for(int i = 0; i < keys.size();i++){
//            mParameters.put(keys.get(i),params.get(keys.get(i)));
//        }
//    }
}
