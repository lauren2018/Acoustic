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
import java.util.Map;

import parsley.acoustic.R;
import parsley.acoustic.view.InPort;
import parsley.acoustic.view.OutPort;
import parsley.acoustic.view.Port;
import parsley.acoustic.view.basic.Param;

public class BlockTemplate extends View{
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
    private int mBlockWidth;
    private int mMarginWidth=20;
    private int mMarginHeight=20;
    private int strokeWidth = 10;
    private int isAnyInPort = 0;
    private int isAnyOutport = 0;

    public BlockTemplate(Context context){
        super(context);
    }
    public BlockTemplate(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public BlockTemplate(Context context, ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum){
        super(context);
        setParams(keys,params,inPortNum,outPortNum);
        //setWillNotDraw(false);

    }

    public BlockTemplate(Context context, AttributeSet attrs,  ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum) {
        super(context, attrs);
        setParams(keys, params,inPortNum,outPortNum);
        setWillNotDraw(false);
    }

    private void setParams(ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum){
        mBlockHeight = mRowHeight * (keys.size());
        mBlockHeight = Math.max(mBlockHeight,Math.max(inPortNum,outPortNum)*(15+InPort.PORT_HEIGHT));
        int len = 0;
        for(int i = 0; i < keys.size();i++){
            if(keys.get(i).length() > len)
                len = keys.get(i).length();
        }
        mBlockWidth = 400;
        mInPortNum = inPortNum;
        mOutPortNum = outPortNum;
        //mBlockWidth = len*(2*fontsize/3);
        if(mInPortNum > 0) isAnyInPort = 1;
        if(mOutPortNum > 0) isAnyOutport = 1;
        setAllPaints();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int port_num = 0;
        if(mInPortNum > 0) port_num++;
        if(mOutPortNum > 0) port_num++;
        int desiredWidth = port_num*InPort.PORT_WIDTH+ 2 * mMarginWidth+mBlockWidth,
                desiredHeight = 2*mMarginHeight+mBlockHeight;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int width,height;
        //Measure Width
        if (widthMode == View.MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }
        //Measure Height
        if (heightMode == View.MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
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
        //draw the module body
        int left,right,top,bottom;
        left = mMarginWidth+ isAnyInPort*InPort.PORT_WIDTH;
        top = mMarginHeight+isAnyOutport*InPort.PORT_HEIGHT;
        right = mMarginWidth+ isAnyInPort*InPort.PORT_WIDTH+mBlockWidth;
        bottom = mMarginHeight+isAnyOutport*InPort.PORT_HEIGHT+mBlockHeight;
        Rect rec = new Rect(left, top, right,bottom);

        int bound_left, bound_right, bound_top, bound_bottom;
        bound_left = left - strokeWidth;
        bound_top = top - strokeWidth;
        bound_right = right+strokeWidth;
        bound_bottom = bottom + strokeWidth;
        canvas.drawRect(new Rect(
                bound_left,bound_top,bound_right,bound_bottom
        ),mBoundPaint);
        canvas.drawRect(rec, mModulePaint);
        Rect [] inPortViews = new Rect[mInPortNum];
        Rect [] outPortViews = new Rect[mOutPortNum];
        int inportInterval = (mBlockHeight - mInPortNum*InPort.PORT_HEIGHT) / (mInPortNum+2);
        int outportInterval = (mBlockHeight - mOutPortNum*InPort.PORT_HEIGHT) / (mOutPortNum+2);
        int inMarginVertical = (int)(1.5*inportInterval);
        int outMarginVertical = (int)(1.5*outportInterval);
        //draw InPorts
        for(int i = 0; i < mInPortNum; i++){
            int tmptop = rec.top+inMarginVertical+rec.top + i*(inportInterval+InPort.PORT_HEIGHT);
            inPortViews[i] = new Rect(rec.left-InPort.PORT_WIDTH, tmptop,
                    rec.left,tmptop+InPort.PORT_HEIGHT);
            canvas.drawRect(inPortViews[i],mInPortPaint);
        }
        //draw Outports
        for(int i = 0; i < mOutPortNum; i++){
            int tmptop = rec.top+outMarginVertical+rec.top + i*(outportInterval+OutPort.PORT_HEIGHT);
            outPortViews[i] = new Rect(rec.right, tmptop,
                    rec.right+OutPort.PORT_WIDTH,tmptop+OutPort.PORT_HEIGHT);
            canvas.drawRect(inPortViews[i],mOutPortPaint);

        }
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

    public int getBlockWidth(){
        return mBlockWidth;
    }

    public int getBlockHeight(){
        return mBlockHeight;
    }

}
