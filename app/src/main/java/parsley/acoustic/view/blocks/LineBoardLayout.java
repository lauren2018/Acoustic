package parsley.acoustic.view.blocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import parsley.acoustic.R;

public class LineBoardLayout extends FrameLayout {
//    private Bitmap mBitmap;
//    private Paint mCablePaint;
//    private Canvas mBitmapCanvas;
    private DragBoardLayout mSiblingDragBoardLayout;
    private final int maxConnectedPorts = 10;
    private final int maxPortNum = 200;
//    private LineGroup [][] mLineGroups;
    private ArrayList<LineGroup> mLineGroups = new ArrayList<>();
    public LineBoardLayout(Context context, int width, int height){
        super(context);
//        mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//        mBitmapCanvas = new Canvas(mBitmap);
//        mBitmapCanvas.drawColor(Color.WHITE);
//        this.draw(mBitmapCanvas);
//        mCablePaint = new Paint();
//        mCablePaint.setColor(Color.RED);
//        mCablePaint.setStrokeWidth(6);
//        mBitmapCanvas.drawRect(new Rect(
//                20,20,200,200
//        ), mCablePaint);
        setWillNotDraw(false);
//        mLineGroups = new LineGroup[maxPortNum][maxConnectedPorts];
    }

//    @Override
//    protected void onDraw(Canvas canvas){
//        if(mBitmap != null){
//            canvas.drawBitmap(mBitmap,0,0,mCablePaint);
//        }
//    }

    public void connectPorts(int s_x, int s_y, int e_x, int e_y){
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
//        mBitmapCanvas.drawLine(start.x,start.y,anchor1.x,anchor1.y,mCablePaint);
//        mBitmapCanvas.drawLine(anchor1.x,anchor1.y,anchor2.x,anchor2.y,mCablePaint);
//        mBitmapCanvas.drawLine(anchor2.x,anchor2.y,end.x,end.y,mCablePaint);
        invalidate();
    }

    public void drawCables(Port p1, Port p2, int s_x, int s_y, int e_x, int e_y){
//        mCablePaint.setColor(Color.BLACK);
        LineGroup lg = new LineGroup(getContext(),s_x,s_y,e_x,e_y, R.integer.BLOCK_RIGHT,R.integer.BLOCK_LEFT
        ,p1.getPid(),p2.getPid());
        mLineGroups.add(lg);
        //int location = new location[];
        p1.addLineGroup(lg);
        p2.addLineGroup(lg);

        drawLineGroup(lg);
        //connectPorts(s_x,s_y,e_x,e_y);
    }

    public void deleteCables(Port p1, Port p2, int s_x, int s_y, int e_x, int e_y){
//        mCablePaint.setColor(Color.WHITE);
        //connectPorts(s_x,s_y,e_x,e_y);
        for(LineGroup lg:mLineGroups){
            if(lg.getInPid() == p1.getPid() && lg.getOutPid() == p2.getPid()){
                mLineGroups.remove(lg);
                p1.removeLineGroup(lg);
                p2.removeLineGroup(lg);
                removeLineGroup(lg);
                break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }



    public void setSiblingDragBoardLayout(DragBoardLayout d){
        mSiblingDragBoardLayout = d;
    }

    public void drawLineGroup(LineGroup lg){
        Line [] lines = lg.getLineViews();
        int i = 0;
        for(Line l:lines){
            if (l != null){
                //setCoordinate(x[i],y[i]);
                this.addView(l);
                i++;
            }
        }
    }

    public void removeLineGroup(LineGroup lg){
        Line [] lines = lg.getLineViews();
        int i = 0;
        for(Line l:lines){
            if (l != null){
                //setCoordinate(x[i],y[i]);
                this.removeView(l);
            }
            i++;
        }
    }
}
