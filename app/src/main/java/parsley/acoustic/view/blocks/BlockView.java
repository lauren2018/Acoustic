package parsley.acoustic.view.blocks;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.R;
import parsley.acoustic.view.BasicBlock;
import parsley.acoustic.view.InPort;
import parsley.acoustic.view.OutPort;
import parsley.acoustic.view.basic.Param;

/**
 * Created by tomsp on 2017/12/25.
 */

public class BlockView extends RelativeLayout{
    /*variables on layout*/
    private BlockTemplate mBlock;
    /*variables on logic, params*/
    private ArrayList<InPort> mInPorts = new ArrayList<InPort>();
    private ArrayList<OutPort> mOutPorts = new ArrayList<OutPort>();
    private Map<String, TextView> mTexts= new HashMap<String, TextView>();
    protected int mInPortsNum;
    protected int mOutPortsNum;
    protected ArrayList<String> mParameterKeys = new ArrayList<String >();
    protected Map<String, Param> mParameters = new HashMap<String,Param>();

    /*variables on logic, connection*/
    private LinearLayout textGroup;

    public BlockView(Context context, Integer id){
        super(context);
        //this.mBlockViewId = id;
    }

    public BlockView(Context context, AttributeSet attr, Integer id){
        super(context,attr);
        //this.mBlockViewId = id;
    }

    public BlockView(Context context,  ArrayList<String> keys, Map<String, Param> params, Integer id){
        super(context);
        //this.mBlockViewId = id;
        setParams(context);
    }

    public BlockView(Context context, AttributeSet attrs,  ArrayList<String> keys, Map<String, Param> params, Integer id){
        super(context, attrs);
        //this.mBlockViewId = id;
        setParams(context);
    }

    protected void setParams(Context context){
        mBlock = new BlockTemplate(context,mParameterKeys,mParameters,mInPortsNum,mOutPortsNum);
        //mBlock.setId(R.id.block);
//        in = new LinearLayout(context);
//        block = new RelativeLayout(context);
//        out = new LinearLayout(context);
//        textGroup = new LinearLayout(context);

//
//        for(int i = 0; i < mInPortsNum; i++){
//            mInPorts.add(new InPort(context,this));
//        }
//        for(int i = 0; i < mOutPortsNum; i++){
//            mOutPorts.add(new OutPort(context,this));
//        }
    }

    protected void _initViews(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.module_board,this);
        //add a basic block
        LinearLayout moduleboard_lo = (LinearLayout) findViewById(R.id.module_board);
        LinearLayout textview_lo = new LinearLayout(getContext());
        LinearLayout edittext_lo = new LinearLayout(getContext());
        //....
        LayoutParams textviewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        for(int i = 0; i < mParameters.size();i++){
            TextView tv = new TextView(getContext());
            EditText et = new EditText(getContext());
            //.....
            textview_lo.addView(tv);
            edittext_lo.addView(et);
        }


//        llayout.addView(in);
//        llayout.addView(block);
//        llayout.addView(out);
//        in.setOrientation(LinearLayout.VERTICAL);
//        out.setOrientation(LinearLayout.VERTICAL);
//        textGroup.setOrientation(LinearLayout.VERTICAL);
//        in.layout(0,0,0,10);
//        out.setPadding(0,0,0,10);
//        //add the block
//        block.addView(mBlock);
        //add the in ports
//        for(int i = 0; i < mInPortsNum; i++){
//            InPort tmp = mInPorts.get(i);
//            in.addView(tmp);
//            tmp.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //inport connection
//                    Toast.makeText(getContext(),"You Click InPort!",Toast.LENGTH_SHORT).show();
//                }
//            });
//            MarginLayoutParams marginParams = new MarginLayoutParams(tmp.getLayoutParams());
//            marginParams.setMargins(0,0,0,10);
//            LayoutParams layoutParams = new LayoutParams(marginParams);
//            tmp.setLayoutParams(layoutParams);
//        }
        //add the outports
//        for(int i = 0; i < mOutPortsNum; i++){
//            OutPort tmp = mOutPorts.get(i);
//            out.addView(tmp);
//            tmp.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //outport connection
//                    Toast.makeText(getContext(),"You Click OutPort!",Toast.LENGTH_SHORT).show();
//                }
//            });
//            MarginLayoutParams marginParams = new MarginLayoutParams(tmp.getLayoutParams());
//            marginParams.setMargins(0,0,0,10);
//            LayoutParams layoutParams = new LayoutParams(marginParams);
//            tmp.setLayoutParams(layoutParams);
//        }
        //add the text
        for(int i = 0; i < mParameters.size();i++){
            String key = mParameterKeys.get(i);
            Param p = mParameters.get(key);
            String value = p.toString();
            TextView tv = new TextView(getContext());
            tv.setTextSize(10);
            tv.setText(key + ": " + value);
            mTexts.put(key,tv);
            textGroup.addView(tv);
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_TOP,mBlock.getId());
        textGroup.setLayoutParams(lp);
        //block.addView(textGroup);
    }

    public void updateParams(ArrayList<String> keys, Map<String,Param> params){
        for(int i = 0; i < keys.size();i++){
            Param p = params.get(keys.get(i));
            mParameters.put(keys.get(i),p);
            String text = keys.get(i)+" : " + p.toString();
            mTexts.get(keys.get(i)).setText(text);
        }
    }

//    public Integer getBlockViewId(){
//        return this.mBlockViewId;
//    }


    public ArrayList<String> getParameterKeys(){
        return mParameterKeys;
    }

    public Param getParameter(String key){
        return mParameters.get(key);
    }

    public ArrayList<InPort> getInPorts(){
        return this.mInPorts;
    }

    public ArrayList<OutPort> getOutPorts(){
        return this.mOutPorts;
    }

}