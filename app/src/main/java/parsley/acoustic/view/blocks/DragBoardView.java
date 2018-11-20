package parsley.acoustic.view.blocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tomsp on 2017/12/29.
 */

public class DragBoardView extends View {
    private Paint mCablePaint;
    private Paint mCanvasPaint;
    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;
    private int mBoardWidth;
    private int mBoardHeight;

    public DragBoardView(Context context){
        this(context,null);
    }

    public DragBoardView(Context context, int width, int height){
        this(context, null, width, height);
    }

    public DragBoardView(Context context, AttributeSet attrs){
        super(context, attrs);
        mBitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(), Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);
        mBitmapCanvas.drawColor(Color.GRAY);
        mCablePaint = new Paint();
        mCablePaint.setColor(Color.RED);
        mCablePaint.setStrokeWidth(6);
    }

    public DragBoardView(Context context, AttributeSet attrs, int width, int height){
        super(context, attrs);
        mBoardWidth = width;
        mBoardHeight = height;
        mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);
        mBitmapCanvas.drawColor(Color.GRAY);
        mCablePaint = new Paint();
        mCablePaint.setColor(Color.RED);
        mCablePaint.setStrokeWidth(6);
        mCanvasPaint = new Paint();
        mCanvasPaint.setColor(Color.GRAY);

    }

    @Override
    protected void onDraw(Canvas canvas){
        if(mBitmap != null){
            canvas.drawBitmap(mBitmap,0,0,mCablePaint);
        }
    }

    public void draw(int s_x, int s_y, int e_x, int e_y){
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
        mBitmapCanvas.drawLine(start.x,start.y,anchor1.x,anchor1.y,mCablePaint);
        mBitmapCanvas.drawLine(anchor1.x,anchor1.y,anchor2.x,anchor2.y,mCablePaint);
        mBitmapCanvas.drawLine(anchor2.x,anchor2.y,end.x,end.y,mCablePaint);
        invalidate();
    }

    public void drawCables(int s_x, int s_y, int e_x, int e_y){
        mCablePaint.setColor(Color.RED);
        draw(s_x,s_y,e_x,e_y);
    }

    public void deleteCables(int s_x, int s_y, int e_x, int e_y){
        mCablePaint.setColor(Color.GRAY);
        draw(s_x,s_y,e_x,e_y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }


}
