package parsley.acoustic;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.view.blocks.DragBoardLayout;
import parsley.acoustic.view.InPort;
import parsley.acoustic.view.OutPort;



public class SDARBoard extends AppCompatActivity implements  View.OnTouchListener {
    float dX, dY, lastAction;
    private Button addButton;
    private View.OnTouchListener mOnTouchListener = this;
    private Map<String,View> moduleList = new HashMap<String,View>();
    private LinearLayout rlayout;
    private DragBoardLayout mDragBoard;
    private InPort focusInPort;
    private boolean inPortFocused = false;
    private OutPort focusOutPort;
    private boolean outPortFocused = false;
    private Integer id = 0;

    private Button start_tx;
    private Boolean start = false;
    //private BFSKMod transceiver;
    private Thread transceiverThread;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_board);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.module_board);
        mDragBoard = new DragBoardLayout(getApplicationContext());
        mDragBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        final ArrayList<String> arr = new ArrayList<String>();
        final Map<String, Object> testmap = new HashMap<>();
        testmap.put("inPortNum",2);
        testmap.put("outPortNum",2);

        rlayout = (LinearLayout) findViewById(R.id.module_board);
        rlayout.setOrientation(LinearLayout.VERTICAL);
        rlayout.addView(mDragBoard, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            }
        });

    }
    public boolean onTouch(View view, MotionEvent event){return false;}

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}

