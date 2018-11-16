package parsley.acoustic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tomsp on 2017/12/28.
 */

public class Cable extends View {
    private int s_x=0;
    private int s_y=0;
    private int e_x=80;
    private int e_y=80;
    private Paint mLinePaint;

    public Cable(Context context, int s_x, int s_y, int e_x, int e_y){
        super(context);
        this.s_x = s_x;
        this.s_y = s_y;
        this.e_x = e_x;
        this.e_y = e_y;
        _initPaint();
    }

    public Cable(Context context, AttributeSet attrs, int s_x, int s_y, int e_x, int e_y){
        super(context,attrs);
        this.s_x = s_x;
        this.s_y = s_y;
        this.e_x = e_x;
        this.e_y = e_y;
        _initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        //int desiredWidth = PORT_WIDTH, desiredHeight = PORT_HEIGHT;
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
            width = widthSize;
        } else {
            //Be whatever you want
            width = widthSize;
        }
        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
           // height = Math.min(desiredHeight, heightSize);
            height = heightSize;
        } else {
            //Be whatever you want
            height = heightSize;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas){
        //Draw line with some algorithm.....
        canvas.drawText("Hello",30,30,mLinePaint);
        int an_y = (e_y+s_y)/2;
        int  an_x = (e_x+s_x)/2;
        Point start, anchor1, anchor2, end;
        if(s_x > e_x){
            start = new Point(s_x,s_y);
            anchor1 = new Point(s_x, an_y);
            anchor2 = new Point(e_x, an_y);
            end = new Point(e_x,e_y);
        }
        else{
            start = new Point(s_x,s_y);
            anchor1 = new Point(an_x, s_y);
            anchor2 = new Point(an_x, e_y);
            end = new Point(e_x,e_y);
        }
        canvas.drawLine(start.x,start.y,anchor1.x,anchor1.y,mLinePaint);
        canvas.drawLine(anchor1.x,anchor1.y,anchor2.x,anchor2.y,mLinePaint);
        canvas.drawLine(anchor2.x,anchor2.y,end.x,end.y,mLinePaint);
        return;
    }

    private void _initPaint(){
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setTextSize(30);
        //this.getBackground().setAlpha(255);
    }
}