package parsley.acoustic.view.fragment;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import parsley.acoustic.R;
import parsley.acoustic.view.blocks.Port;
import parsley.acoustic.view.blocks.PopupWindowView;
import parsley.acoustic.view.blocks.BlockView;
import parsley.acoustic.view.blocks.DragBoardLayout;

public class BoardFragment extends Fragment {
    private DragBoardLayout mDragBoard_lo;
    private float dX = 0, dY=0, lastAction=0;

    @TargetApi(17)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mDragBoard_lo = new DragBoardLayout(getContext());
        mDragBoard_lo.generateViewId();
        mDragBoard_lo.setId(R.id.moduleBoard_lo_id);
        //LinearLayout view = (LinearLayout) inflater.inflate(R.layout.module_board,container, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        mDragBoard_lo.setLayoutParams(params);
        return mDragBoard_lo;
    }

    public void setBlockViewListener(BlockView v){
        mDragBoard_lo.addView(v);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBlockViewTouchEvent((BlockView) v,event);
                return true;
            }
        });
    }

    private boolean onBlockViewTouchEvent(BlockView view, MotionEvent event){
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
                for(int i = 0; i < view.getInPorts().size();i++){
                    Port tmp = view.getInPorts().get(i);
                    if(tmp.isConnected()){
//                        view_disconnect_connect(tmp,tmp.getConnectedPort(),true);
                    }
                }
                for(int i = 0; i < view.getOutPorts().size();i++){
                    Port tmpo = view.getOutPorts().get(i);
                    for(int j = 0; j < tmpo.getConnectedPorts().size();j++){
                        Port tmpi = tmpo.getConnectedPorts().get(j);
//                        view_disconnect_connect(tmpi,tmpo,true);
                    }
                }
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                //add the new cables
                for(int i = 0; i < ((BlockView) view).getInPorts().size();i++){
                    Port tmp = ((BlockView) view).getInPorts().get(i);
                    if(tmp.isConnected()){
//                        view_connect(tmp,tmp.getConnectedPort(),true);
                    }
                }
                for(int i = 0; i < view.getOutPorts().size();i++){
                    Port tmpo = view.getOutPorts().get(i);
                    for(int j = 0; j < tmpo.getConnectedPorts().size();j++){
                        Port tmpi = tmpo.getConnectedPorts().get(j);
//                        view_connect(tmpi,tmpo,true);
                    }
                }
                lastAction = MotionEvent.ACTION_MOVE;

                break;

            case MotionEvent.ACTION_UP:
                //click event
                mTimerEnd = System.currentTimeMillis();
                if (lastAction == MotionEvent.ACTION_DOWN ){
                    popupWindow(view);
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
}
