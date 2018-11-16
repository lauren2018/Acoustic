package parsley.acoustic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.view.basic.Param;

/**
 * Created by tomsp on 2017/12/20.
 */

public class BasicBlock extends View {
    private Paint mModulePaint;
    private Paint mTextPaint;
    private Paint mPortPaint;
    private int mBlockWidth = 220;
    private int mBlockHeight = 60;
    private int mInPortNum = 1;
    private int mOutPortNum = 1;
    private int fontsize = 28;
    private boolean mShowBlock;
    private ArrayList<String> mParameterKeys = new ArrayList<String >();
    private Map<String, Param> mParameters = new HashMap<>();
    private final int mRowHeight = 40;


    private void setParams(ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum){
        mBlockHeight = mRowHeight * keys.size();
        mBlockHeight = Math.max(mBlockHeight,Math.max(inPortNum,outPortNum)*(15+InPort.PORT_HEIGHT));
        int len = 0;
        for(int i = 0; i < keys.size();i++){
            if(keys.get(i).length() > len)
                len = keys.get(i).length();
        }
        mBlockWidth = len*(2*fontsize/3);
        setAllPaints();
    }
    public BasicBlock(Context context){
        super(context);
    }
    public BasicBlock(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public BasicBlock(Context context,  ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum){
        super(context);
        setParams(keys,params,inPortNum,outPortNum);
        setWillNotDraw(false);

    }

    public BasicBlock(Context context, AttributeSet attrs,  ArrayList<String> keys, Map<String, Param> params, int inPortNum, int outPortNum) {
        super(context, attrs);
        setParams(keys, params,inPortNum,outPortNum);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int desiredWidth = mBlockWidth, desiredHeight = mBlockHeight;
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
        //draw the module body
        int left,right,top,bottom;
        left = (canvas.getWidth()-mBlockWidth)/2;
        top = (canvas.getHeight()-mBlockHeight)/2;
        right = (canvas.getWidth()+mBlockWidth)/2;
        bottom = (canvas.getHeight()+mBlockHeight)/2;
        Rect rec = new Rect(left, top, right,bottom);
        canvas.drawRect(rec, mModulePaint);
    }

    private void setAllPaints(){
        mModulePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mModulePaint.setColor(Color.BLACK);
        mModulePaint.setStyle(Paint.Style.FILL);
        mModulePaint.setAlpha(30);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(fontsize);
    }



    public void updateParams(ArrayList<String> keys, Map<String,Param> params){
        for(int i = 0; i < keys.size();i++){
            mParameters.put(keys.get(i),params.get(keys.get(i)));
        }
    }
}
