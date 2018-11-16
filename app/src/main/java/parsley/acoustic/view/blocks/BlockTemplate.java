package parsley.acoustic.view.blocks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

import parsley.acoustic.view.InPort;
import parsley.acoustic.view.OutPort;
import parsley.acoustic.view.Port;
import parsley.acoustic.view.basic.Param;

public class BlockTemplate extends View{
    private Paint mModulePaint;
    private Paint mTextPaint;
    private Paint mInPortPaint;
    private Paint mOutPortPaint;
    //The following three params determine the height and width of the block
    private int mInPortNum = 1;
    private int mOutPortNum = 1;
    private int mTextRows = 1;

    //view params
    private final int mRowHeight = 40;
    private int mBlockHeight;
    private int mBlockWidth;


    public BlockTemplate(Context context){
        super(context);
    }
    public BlockTemplate(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public BlockTemplate(Context context, ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum){
        super(context);
        setParams(keys,params,inPortNum,outPortNum);
        setWillNotDraw(false);

    }

    public BlockTemplate(Context context, AttributeSet attrs,  ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum) {
        super(context, attrs);
        setParams(keys, params,inPortNum,outPortNum);
        setWillNotDraw(false);
    }

    private void setParams(ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum){
        mBlockHeight = mRowHeight * keys.size();
        mBlockHeight = Math.max(mBlockHeight,Math.max(inPortNum,outPortNum)*(15+InPort.PORT_HEIGHT));
        int len = 0;
        for(int i = 0; i < keys.size();i++){
            if(keys.get(i).length() > len)
                len = keys.get(i).length();
        }
        mBlockWidth = 220;
        mInPortNum = inPortNum;
        mOutPortNum = outPortNum;
        //mBlockWidth = len*(2*fontsize/3);
        setAllPaints();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int desiredWidth = mBlockWidth, desiredHeight = mBlockHeight;
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
        left = (canvas.getWidth()-mBlockWidth)/2;
        top = (canvas.getHeight()-mBlockHeight)/2;
        right = (canvas.getWidth()+mBlockWidth)/2;
        bottom = (canvas.getHeight()+mBlockHeight)/2;
        Rect rec = new Rect(left, top, right,bottom);
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
        mModulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mModulePaint.setColor(Color.BLACK);
        mModulePaint.setStyle(Paint.Style.FILL);
        mModulePaint.setAlpha(30);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mTextPaint.setTextSize(fontsize);
    }
}
