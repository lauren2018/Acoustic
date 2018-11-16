package parsley.acoustic.activity;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.view.blocks.BlockView;
import parsley.acoustic.view.DragBoardView;
import parsley.acoustic.view.InPort;
import parsley.acoustic.view.OutPort;
import parsley.acoustic.view.PopupWindowView;

/**
 * Created by tomsp on 2017/12/28.
 */

public class DragBoardLayout extends RelativeLayout implements View.OnTouchListener{
    float dX, dY, lastAction;
    float cx,cy,lx,ly;
    private OnTouchListener mOnTouchListener = this;

    private Integer id = 0;
    private Map<Integer,BlockView> moduleList = new HashMap<Integer, BlockView>();
    private LinearLayout parentLayout;
    private DragBoardView mBoardView;

    private OnTouchListener mCableTouchListener;
    private ArrayList<ArrayList<InPort>> inPortList = new ArrayList<ArrayList<InPort>>();
    private ArrayList<ArrayList<OutPort>> outPortList = new ArrayList<ArrayList<OutPort>>();
    private InPort focusInPort;
    private boolean inPortSelected = false;
    private OutPort focusOutPort;
    private boolean outPortSelected = false;

    public DragBoardLayout(Context context, LinearLayout l){
        super(context);
        _init(l);
    }

    public DragBoardLayout(Context context, AttributeSet attrs, LinearLayout l){
        super(context,attrs);
        _init(l);
    }

    private void _init(LinearLayout l) {
        parentLayout = l;
        int width = getScreenWidth();
        int height = getScreenHeight();
        mBoardView = new DragBoardView(getContext(),width,height);
        this.addView(mBoardView);
        mCableTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long mTimerBegin = System.currentTimeMillis();
                long currentTime;
                boolean down = false;
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!down) {
                            mTimerBegin = System.currentTimeMillis();
                            down = true;
                        } else {
                            currentTime = System.currentTimeMillis();
                            if (currentTime - mTimerBegin >= 2000L) {
                                //delete the view

                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        down = false;
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };
    }
    @Override
    public boolean onTouch(View view, MotionEvent event){
        view = (BlockView) view;
        long mTimerBegin = System.currentTimeMillis();
        long mTimerEnd = System.currentTimeMillis();
        boolean down = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                if(!down) {
                    mTimerBegin = System.currentTimeMillis();
                    down = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                //delete the original cables
                for(int i = 0; i < ((BlockView) view).getInPorts().size();i++){
                    InPort tmp = ((BlockView) view).getInPorts().get(i);
                    if(tmp.isConnected()){
                        view_disconnect_connect(tmp,tmp.getConnectedPort(),true);
                    }
                }
                for(int i = 0; i < ((BlockView) view).getOutPorts().size();i++){
                    OutPort tmpo = ((BlockView) view).getOutPorts().get(i);
                    for(int j = 0; j < tmpo.getConnectedPorts().size();j++){
                        InPort tmpi = tmpo.getConnectedPorts().get(j);
                        view_disconnect_connect(tmpi,tmpo,true);
                    }
                }
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                //add the new cables
                for(int i = 0; i < ((BlockView) view).getInPorts().size();i++){
                    InPort tmp = ((BlockView) view).getInPorts().get(i);
                    if(tmp.isConnected()){
                        view_connect(tmp,tmp.getConnectedPort(),true);
                    }
                }
                for(int i = 0; i < ((BlockView) view).getOutPorts().size();i++){
                    OutPort tmpo = ((BlockView) view).getOutPorts().get(i);
                    for(int j = 0; j < tmpo.getConnectedPorts().size();j++){
                        InPort tmpi = tmpo.getConnectedPorts().get(j);
                        view_connect(tmpi,tmpo,true);
                    }
                }
                lastAction = MotionEvent.ACTION_MOVE;

                break;

            case MotionEvent.ACTION_UP:
                //click event
                mTimerEnd = System.currentTimeMillis();
                if (lastAction == MotionEvent.ACTION_DOWN ){
                    _initPopupWindow((BlockView)view);
                    return false;
                } else{
                    return true;
                }

            default:
                return false;
        }
        return true;
    }

    private void _initPopupWindow(BlockView v){
        PopupWindowView pwv = new PopupWindowView(getContext(),v,this);
    }
/*
addModule() add the new modules to the module list dragging board. Note that it is logically adding, not add view.
* */
    public void addModule(BlockView blockView){
//        super.addView(v);
        moduleList.put(id++, blockView);
        blockView.setOnTouchListener(this);
        inPortList.add(blockView.getInPorts());
        for(int i = 0; i < blockView.getInPorts().size();i++){
            blockView.getInPorts().get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    InPort port = (InPort) v;
                    if(outPortSelected){
                        inPortSelected = true;
                        focusInPort = port;
                        //if they have been connected, delete the cable
                        if(focusInPort.isConnected() && focusInPort.getConnectedPort() == focusOutPort){
                            view_disconnect_connect(focusInPort,focusOutPort,false);
                        }
                        else{
                            //connect focusInport and focusOutport
                            view_connect(focusInPort,focusOutPort,false);
                        }
                        inPortSelected = false;
                        outPortSelected = false;
                        focusInPort = null;
                        focusOutPort = null;
                    }
                    else{
                        inPortSelected = true;
                        focusInPort = (InPort) port;
                    }
                }
            });
        }
        outPortList.add(blockView.getOutPorts());
        for(int i = 0; i < blockView.getOutPorts().size();i++){
            blockView.getOutPorts().get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OutPort port = (OutPort) v;
                    if(inPortSelected){
                        outPortSelected = true;
                        focusOutPort = port;
                        //if they have been connected, delete the cable
                        if(focusInPort.isConnected() && focusInPort.getConnectedPort() == focusOutPort){
                            view_disconnect_connect(focusInPort,focusOutPort,false);
                        }
                        else{
                            //connect focusInport and focusOutport
                            view_connect(focusInPort,focusOutPort,false);
                        }
                        inPortSelected = false;
                        outPortSelected = false;
                        focusInPort = null;
                        focusOutPort = null;
                    }
                    else{
                        outPortSelected = true;
                        focusOutPort = (OutPort) port;
                    }
                }
            });
        }
    }

    /*here reserved is a boolean value indicates that if these two ports are disconnected temporarily or permanently:
   true: temporarily
   false: permanently
*/
    public void view_connect(InPort inPort, OutPort outPort, boolean reserved){
        if(inPort != null && outPort != null){
            int start_x = (int)getRelativeLeft(inPort)+inPort.getWidth()/2;
            int start_y =  (int)getRelativeTop(inPort)+inPort.getHeight()/2;
            int end_x = (int)getRelativeLeft(outPort)+outPort.getWidth()/2;
            int end_y =(int)getRelativeTop(outPort)+outPort.getHeight()/2;
            mBoardView.drawCables(start_x,start_y,end_x,end_y);
            if(!reserved) {
                inPort.setConnected(outPort);
                outPort.setConnected(inPort);
            }
            //line.setOnTouchListener(mCableTouchListener);
            //this.addView(line);
        }
    }

    //
    //
     /*here reserve is a boolean value indicates that if these two ports are disconnected temporarily or permanently:
        true: temporarily
        false: permanently
    */
    public void view_disconnect_connect(InPort inPort, OutPort outPort, boolean reserved){
        if(inPort != null && outPort != null){
            int start_x = (int)getRelativeLeft(inPort)+inPort.getWidth()/2;
            int start_y =  (int)getRelativeTop(inPort)+inPort.getHeight()/2;
            int end_x = (int)getRelativeLeft(outPort)+outPort.getWidth()/2;
            int end_y =(int)getRelativeTop(outPort)+outPort.getHeight()/2;
            mBoardView.deleteCables(start_x,start_y,end_x,end_y);
            if(!reserved){
                inPort.dismissConnected();
                outPort.dismissConnected(inPort);
            }

            //line.setOnTouchListener(mCableTouchListener);
            //this.addView(line);
        }
    }

    private int getRelativeLeft(View v){
        float left = v.getX();
        while(v.getParent() != this){
            left += ((View)v.getParent()).getX();
            v = (View)v.getParent();
        }
        return (int)left;
    }
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    private int getRelativeTop(View v){
        float top = v.getY();
        while(v.getParent() != this){
            top += ((View)v.getParent()).getY();
            v = (View)v.getParent();
        }
        return (int)top;
    }
}
