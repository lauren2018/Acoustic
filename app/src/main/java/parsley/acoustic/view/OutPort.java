package parsley.acoustic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import parsley.acoustic.view.blocks.BlockView;

/**
 * Created by tomsp on 2017/12/20.
 */

public class OutPort extends View {
    public  static final int PORT_WIDTH = 40;
    public  static final int PORT_HEIGHT = 40;
    private Paint mBlockPaint;
    private Paint mTextPaint;
    private int mLeft=0;
    private int mTop=0;
    private int mRight=20;
    private int mBottom=20;

    private BlockView parentBlockView;
    private boolean isConnected = false;
    private ArrayList<InPort> connectedPorts = new ArrayList<InPort>();

    public void _init(int left,int top, int right, int bottom){
        mBlockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlockPaint.setColor(Color.BLACK);
        mBlockPaint.setAlpha(70);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(18);
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    public void _init(){
        mBlockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlockPaint.setColor(Color.BLACK);
        mBlockPaint.setAlpha(70);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(18);
        mLeft = 0;
        mTop = 0;
        mRight = PORT_WIDTH;
        mBottom = PORT_HEIGHT;
    }

    public OutPort(Context context, int left,int top, int right, int bottom){
        super(context);
        _init(left,top,right,bottom);
    }

    public OutPort(Context context, AttributeSet attrs, int left, int top, int right, int bottom){
        super(context,attrs);
        _init(left,top,right,bottom);
    }

    public OutPort(Context context, AttributeSet attrs, BlockView blockView){
        super(context,attrs);
        parentBlockView = blockView;
        _init();
    }

    public OutPort(Context context, BlockView blockView){
        super(context);
        parentBlockView = blockView;
        _init();
    }

    protected void onDraw(Canvas canvas){
        Rect rec = new Rect(mLeft,mTop,mRight,mBottom);
        canvas.drawRect(rec,mBlockPaint);
        canvas.drawText("2",(mLeft+mRight)/2, (mTop+mBottom)/2,mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        int desiredWidth = PORT_WIDTH, desiredHeight = PORT_HEIGHT;
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

    public void setConnected(InPort i){
        isConnected = true;
        connectedPorts.add(i);
    }

    public void dismissConnected(InPort i){
        isConnected = false;
        connectedPorts.remove(i);
    }
    public ArrayList<InPort> getConnectedPorts(){return connectedPorts;}

    public boolean isConnected(InPort i){
        return isConnected && (connectedPorts.contains(i));
    }

    public BlockView getParentBlockView(){return this.parentBlockView;}

}
