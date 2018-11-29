package parsley.acoustic.view.blocks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.R;

/**
 * Created by tomsp on 2017/12/28.
 */

public class DragBoardLayout extends RelativeLayout  {
    private Port lastClickedPort;
    private Port currentFocusPort;

    //cable drawing params
    private LineBoardLayout mSiblingLineBoardLayout;

    public DragBoardLayout(Context context) {
        super(context);
    }

    public DragBoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void _initPopupWindow(BlockView v) {
        PopupWindowView pwv = new PopupWindowView(getContext(), v, this);
    }

    /**here reserved is a boolean value indicates that if these two ports are disconnected temporarily or permanently:
   true: temporarily
   false: permanently
*/

    public void view_connect(Port port1, Port port2, boolean reserved) {
        PortView pv1 = port1.getPortView(), pv2 = port2.getPortView();
        if (port1 != null && port2 != null) {
            int start_x = (int) getRelativeLeft(pv1) + pv1.getWidth() / 2;
            int start_y = (int) getRelativeTop(pv1) + pv1.getHeight() / 2;
            int end_x = (int) getRelativeLeft(pv2) + pv2.getWidth() / 2;
            int end_y = (int) getRelativeTop(pv2) + pv2.getHeight() / 2;
            /**Revision required*/

            mSiblingLineBoardLayout.drawCables(port1,port2,start_x, start_y, end_x, end_y);
            if (!reserved) {
                port1.setConnected(port2);
                port2.setConnected(port1);
            }
//            line.setOnTouchListener(mCableTouchListener);
//            this.addView(line);
        }
    }

    public void view_disconnect(Port port1, Port port2, boolean reserved) {
        PortView pv1 = port1.getPortView(), pv2 = port2.getPortView();
        if (port1 != null && port2 != null) {
            int start_x = (int) getRelativeLeft(pv1) + pv1.getWidth() / 2;
            int start_y = (int) getRelativeTop(pv1) + pv1.getHeight() / 2;
            int end_x = (int) getRelativeLeft(pv2) + pv2.getWidth() / 2;
            int end_y = (int) getRelativeTop(pv2) + pv2.getHeight() / 2;
            /**Revision required*/
            mSiblingLineBoardLayout.deleteCables(port1,port2,start_x, start_y, end_x, end_y);
            if (!reserved) {
                port1.dismissConnected(port2);
                port2.dismissConnected(port1);
            }

        }
    }

    public void view_connect_batch(Port port, ArrayList<Port> connectedPorts, boolean reserved) {
        if(port.getPortType() == R.integer.OUT_PORT){
            for (int i = 0; i < connectedPorts.size(); i++) {
                Port tmpPort = connectedPorts.get(i);
                view_connect(port, tmpPort, reserved);
            }
        }else{
            for (int i = 0; i < connectedPorts.size(); i++) {
                Port tmpPort = connectedPorts.get(i);
                view_connect(tmpPort, port, reserved);
            }
        }

    }

    //
    //
     /*here reserve is a boolean value indicates that if these two ports are disconnected temporarily or permanently:
        true: temporarily
        false: permanently
    */
    public void view_disconnect_batch(Port port, ArrayList<Port> connectedPorts, boolean reserved) {
        if(port.getPortType() == R.integer.OUT_PORT){
            for (int i = 0; i < connectedPorts.size(); i++) {
                Port tmpPort = connectedPorts.get(i);
                view_disconnect(port, tmpPort, reserved);
            }
        }else{
            for (int i = 0; i < connectedPorts.size(); i++) {
                Port tmpPort = connectedPorts.get(i);
                view_disconnect(tmpPort, port, reserved);
            }
        }

    }


    private void setPortClickListener(View v) {
        PortView pv = (PortView) v;
        Port port = ((PortView) v).getParentPort();
        int checkPortType = 0;
        if (port.getPortType() == R.integer.IN_PORT) {
            checkPortType = R.integer.OUT_PORT;
        } else {
            checkPortType = R.integer.IN_PORT;
        }
        currentFocusPort = pv.getParentPort();
        if ((lastClickedPort != null) && (lastClickedPort.getPortType() == checkPortType)) {
            //if these two ports have been connected
            if (currentFocusPort.isInConnectedPorts(lastClickedPort) &&
                    lastClickedPort.isInConnectedPorts(currentFocusPort)) {
                view_disconnect(lastClickedPort, currentFocusPort, false);
            } else {
                view_connect(lastClickedPort, currentFocusPort, false);
            }
            lastClickedPort = null;
            currentFocusPort = null;
        } else {
            lastClickedPort = ((PortView) v).getParentPort();
        }
    }

    private int getRelativeLeft(View v) {
        float left = v.getX();
        while (v.getParent() != this) {
            left += ((View) v.getParent()).getX();
            v = (View) v.getParent();
        }
        return (int) left;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private int getRelativeTop(View v) {
        float top = v.getY();
        while (v.getParent() != this) {
            top += ((View) v.getParent()).getY();
            v = (View) v.getParent();
        }
        return (int) top;
    }


    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
    }

    public void setSiblingDragBoardLayout(LineBoardLayout l){
        mSiblingLineBoardLayout = l;
    }
}


