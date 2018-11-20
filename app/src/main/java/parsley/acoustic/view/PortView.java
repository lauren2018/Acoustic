package parsley.acoustic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import parsley.acoustic.view.basic.DataType;
import parsley.acoustic.view.blocks.BlockView;

public class PortView extends View {
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
    private Port connectedPort = null;
    private int mPortType = 0;
    private DataType mDataType;
    private Rect mRect;

    public void init(Rect rect){
        mBlockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlockPaint.setColor(Color.BLACK);
        mBlockPaint.setAlpha(70);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(18);
        mRect = rect;
    }

    public PortView(Context context, Rect rect){
        super(context);
        init(rect);
    }

//    public Port(Context context, Rect rect, DataType d, int portType){
//        super(context);
//        init(rect, d, portType);
//    }

    public PortView(Context context, AttributeSet attrs, Rect rect){
        super(context,attrs);
        init(rect);
    }

//    public Port(Context context, BlockView blockView){
//        super(context);
//        parentBlockView = blockView;
//        _init();
//    }

//    public Port(Context context, AttributeSet attrs, BlockView blockView){
//        super(context,attrs);
//        parentBlockView = blockView;
//        _init();
//    }

    protected void onDraw(Canvas canvas){
//        Rect rec = new Rect(mLeft,mTop,mRight,mBottom);
        canvas.drawRect(mRect,mBlockPaint);
        canvas.drawText("1",(mLeft+mRight)/2, (mTop+mBottom)/2,mTextPaint);
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

    public void setConnected(Port p){
        isConnected = true;
        connectedPort = p;
    }
    /*here reserve is a boolean value indicates that if these two ports are disconnected temporarily or permanently:
       true: temporarily
       false: permanently
   */
    public void dismissConnected(){
        isConnected = false;
        connectedPort = null;
    }

    public boolean isConnected(){
        return isConnected;
    }

    public Port getConnectedPort(){return connectedPort;}

    public BlockView getParentBlockView(){return this.parentBlockView;}

}
