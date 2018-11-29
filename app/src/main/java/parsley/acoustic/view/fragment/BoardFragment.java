package parsley.acoustic.view.fragment;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import parsley.acoustic.R;
import parsley.acoustic.tools.Array;
import parsley.acoustic.view.blocks.BlockTemplate;
import parsley.acoustic.view.blocks.LineBoardLayout;
import parsley.acoustic.view.blocks.Port;
import parsley.acoustic.view.blocks.PopupWindowView;
import parsley.acoustic.view.blocks.BlockView;
import parsley.acoustic.view.blocks.DragBoardLayout;
import parsley.acoustic.view.blocks.PortView;

public class BoardFragment extends Fragment {
    private RelativeLayout mBoard_lo;
    private DragBoardLayout mDragBoard_lo;
    private LineBoardLayout mLineBoard_lo;
    private float dX = 0, dY=0, lastAction=0;
    private Port lastClickedPort;
    private Port currentFocusPort;

    @TargetApi(17)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        /**MainBoard Initialization*/
        mBoard_lo = new RelativeLayout(getContext());
        mBoard_lo.generateViewId();
        mBoard_lo.setId(R.id.board_lo_id);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        /**DragBoard Initialization*/
        mDragBoard_lo = new DragBoardLayout(getContext());
        mDragBoard_lo.generateViewId();
        mDragBoard_lo.setId(R.id.dragBoard_lo_id);
        mDragBoard_lo.setLayoutParams(lp);
        mDragBoard_lo.setBackgroundColor(getResources().getColor(R.color.transparent));
        /**LineBoard Initialization*/
        WindowManager wm = (WindowManager)getContext().getSystemService(getContext().WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        mLineBoard_lo = new LineBoardLayout(getContext(),display.getWidth(),display.getHeight());
        mLineBoard_lo.generateViewId();
        mLineBoard_lo.setId(R.id.lineBoard_lo_id);
        mLineBoard_lo.setLayoutParams(lp);
        TextView tv= new TextView(getContext());
        tv.setText("You are a pig");
        mLineBoard_lo.addView(tv);
        mLineBoard_lo.setSiblingDragBoardLayout(mDragBoard_lo);
        mDragBoard_lo.setSiblingDragBoardLayout(mLineBoard_lo);

        //LinearLayout view = (LinearLayout) inflater.inflate(R.layout.module_board,container, false);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT
//        );
//        mDragBoard_lo.setLayoutParams(params);
        mBoard_lo.addView(mLineBoard_lo);
        mBoard_lo.addView(mDragBoard_lo);
        return mBoard_lo;
    }

    public void setBlockViewListener(BlockView v){
        mDragBoard_lo.addView(v);
        ArrayList<Port> inPorts = v.getInPorts();
        ArrayList<Port> outPorts = v.getOutPorts();
        for(int i = 0; i < inPorts.size();i++){
            inPorts.get(i).getPortView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPortClickListener(v);
                }
            });
        }
        for(int i = 0; i < outPorts.size();i++){
            outPorts.get(i).getPortView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPortClickListener(v);
                }
            });
        }
        BlockTemplate bt = v.getBlockTemplate();
        bt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBlockTemplateTouchEvent((BlockTemplate) v,event);
                return true;
            }
        });
    }

    private void onPortClickListener(View v){
        PortView pv = (PortView) v;
        Port port = ((PortView) v).getParentPort();
        int checkPortType;
        if(port.getPortType() == R.integer.IN_PORT){
            checkPortType = R.integer.OUT_PORT;
        }else{
            checkPortType = R.integer.IN_PORT;
        }
        currentFocusPort = pv.getParentPort();
        if((lastClickedPort != null) && (lastClickedPort.getPortType()==checkPortType)){
            //if these two ports have been connected
            if(currentFocusPort.isInConnectedPorts(lastClickedPort) &&
                    lastClickedPort.isInConnectedPorts(currentFocusPort)) {
                if(lastClickedPort.getPortType() == R.integer.OUT_PORT){
                    mDragBoard_lo.view_disconnect(lastClickedPort, currentFocusPort, false);
                }else{
                    mDragBoard_lo.view_disconnect(currentFocusPort, lastClickedPort, false);
                }
            }else{
                if(lastClickedPort.getPortType() ==R.integer.OUT_PORT){
                    mDragBoard_lo.view_connect(lastClickedPort, currentFocusPort, false);
                }else{
                    mDragBoard_lo.view_connect(currentFocusPort, lastClickedPort, false);
                }
            }
            lastClickedPort = null;
            currentFocusPort = null;
        }else{
            lastClickedPort = ((PortView) v).getParentPort();
        }
    }

    private boolean onBlockTemplateTouchEvent(BlockTemplate view, MotionEvent event){
        long mTimerBegin = System.currentTimeMillis();
        long mTimerEnd = System.currentTimeMillis();
        BlockView parentView = (BlockView) view.getParent();
        boolean down = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = parentView.getX() - event.getRawX();
                dY = parentView.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                if(!down) {
                    mTimerBegin = System.currentTimeMillis();
                    down = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                //delete the original cables
                for(int i = 0; i < parentView.getInPorts().size();i++){
                    Port tmp = parentView.getInPorts().get(i);
                    if(tmp.isConnected()){
                        mDragBoard_lo.view_disconnect_batch(tmp,tmp.getConnectedPorts(),true);
                    }
                }
                for(int i = 0; i < parentView.getOutPorts().size();i++){
                    Port tmpo = parentView.getOutPorts().get(i);
                    mDragBoard_lo.view_disconnect_batch(tmpo, tmpo.getConnectedPorts(),true);
                }
                parentView.setY(event.getRawY() + dY);
                parentView.setX(event.getRawX() + dX);
                //add the new cables
                for(int i = 0; i < parentView.getInPorts().size();i++){
                    Port tmp = parentView.getInPorts().get(i);
                    if(tmp.isConnected()){
                        mDragBoard_lo.view_connect_batch(tmp,tmp.getConnectedPorts(),true);
                    }
                }
                for(int i = 0; i < parentView.getOutPorts().size();i++){
                    Port tmpo = parentView.getOutPorts().get(i);
                    mDragBoard_lo.view_disconnect_batch(tmpo,tmpo.getConnectedPorts(),true);
                }
                lastAction = MotionEvent.ACTION_MOVE;

                break;

            case MotionEvent.ACTION_UP:
                //click event
                mTimerEnd = System.currentTimeMillis();
                if (lastAction == MotionEvent.ACTION_DOWN ){
                    popupWindow(parentView);
                    return false;
                } else{
                    return true;
                }

            default:
                return false;
        }
        return true;
    }

    private void popupWindow(BlockView v){
        PopupWindowView pwv = new PopupWindowView(getContext(),v,mDragBoard_lo);
    }

    public DragBoardLayout getDragBoardLayout(){
        return mDragBoard_lo;
    }
    public LineBoardLayout getLineBoardLayout(){
        return mLineBoard_lo;
    }
    public RelativeLayout getBoardLayout(){
        return mBoard_lo;
    }
}
