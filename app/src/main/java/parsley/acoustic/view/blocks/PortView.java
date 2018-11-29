package parsley.acoustic.view.blocks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import parsley.acoustic.R;
import parsley.acoustic.view.basic.DataType;

public class PortView extends View {
    public  static final int PORT_WIDTH = 40;
    public  static final int PORT_HEIGHT = 40;
    private Paint mPortPaint;
    private Paint mTextPaint;
    private int mLeft=0;
    private int mTop=0;
    private int mRight=20;
    private int mBottom=20;

    private BlockView parentBlockView;

    private int mPortType = 0;
    private DataType mDataType;
    private Rect mRect;
    private Port mParent;
    private ArrayList<Line> connectedCables;

    public void init(DataType d, Port parent){
        mParent = parent;
        mPortPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDataType = d;
        int color = 0xFFFFFF;
        Resources resources = getResources();
        switch(mDataType){
            case COMPLEX:
                color = resources.getColor(R.color.royalblue);
                mPortPaint.setColor(color);
                break;
            case FLOAT:
                color = resources.getColor(R.color.tomato);
                break;
            case BYTE:
                color = resources.getColor(R.color.mediumorchid);
                break;
            case BIT:
                color = resources.getColor(R.color.seagreen);
            case STRING:
                color = resources.getColor(R.color.gold);
            case INTEGER:
                color = resources.getColor(R.color.turquoise);
            default:
        }

        mPortPaint.setColor(color);
        mPortPaint.setAlpha(70);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(18);
        //mRect = rect;
    }

    public PortView(Context context, DataType d, Port parent){
        super(context);
        init(d,parent);
    }

//    public Port(Context context, Rect rect, DataType d, int portType){
//        super(context);
//        init(rect, d, portType);
//    }

    public PortView(Context context, AttributeSet attrs, DataType d, Port parent){
        super(context,attrs);
        init(d, parent);
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

        Rect rec = new Rect(0,0,PortView.PORT_WIDTH,PortView.PORT_HEIGHT);
        canvas.drawRect(rec,mPortPaint);
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



    public Port getParentPort(){
        return mParent;
    }

    public Paint getPortPaint(){return mPortPaint;}


}
