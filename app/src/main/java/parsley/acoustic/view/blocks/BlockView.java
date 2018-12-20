package parsley.acoustic.view.blocks;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.R;
import parsley.acoustic.view.basic.DataType;
import parsley.acoustic.view.basic.Param;

/**
 * Created by tomsp on 2017/12/25.
 */

public class BlockView extends RelativeLayout{
    /**variables on logic, params*/
    protected String mBlockName;
    protected ArrayList<Port> mInPorts = new ArrayList<Port>();
    protected ArrayList<Port> mOutPorts = new ArrayList<Port>();
    private Map<String, TextView> mTexts= new HashMap<String, TextView>();
    protected ArrayList<String> mParameterKeys = new ArrayList<String >();
    protected Map<String, Param> mParameters = new HashMap<String,Param>();

    /**Views on logic, connection*/
    private BlockTemplate mBlock;
    private LinearLayout textGroup;
    private RelativeLayout mBlockLayout;

    /**connected graphs*/
    //private ArrayList<BlockView> nextViews= new ArrayList<>();

    public BlockView(Context context, String blockName, ArrayList<String> keys, Map<String, Param> params, DataType inType, DataType outType, int inNum, int outNum){
        super(context);
        blockviewInit(context, blockName, keys, params, inType, outType, inNum, outNum);
    }

    public BlockView(Context context, AttributeSet attrs,  ArrayList<String> keys, Map<String, Param> params){
        super(context, attrs);
    }

    protected void setParams(Context context){
        mBlock = new BlockTemplate(context,mParameterKeys,mParameters,mInPorts, mOutPorts);
        mBlockLayout = new RelativeLayout(context);
    }

    protected void blockviewInit(Context context, String blockName, ArrayList<String> keys, Map<String, Param> params, DataType inType, DataType outType, int inNum, int outNum){
        mBlockName = blockName;
        for(int i = 0; i < inNum;i++){
            mInPorts.add(new Port(inType, R.integer.IN_PORT,this));
        }
        for(int i = 0; i < outNum;i++){
            mOutPorts.add(new Port(outType, R.integer.OUT_PORT,this));
        }
        for(int i = 0; i < keys.size();i++){
            mParameterKeys.add(keys.get(i));
            mParameters.put(keys.get(i),params.get(keys.get(i)));
        }
        setParams(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LinearLayout blockParams = createBlockParamsView();
        lp.addRule(RelativeLayout.ALIGN_TOP,mBlock.getId());
        lp.setMargins(20+mBlock.getBlockRectLeft(),20,10,0);
        blockParams.setLayoutParams(lp);
        this.addView(mBlock);
        this.addView(blockParams);

    }

    public void updateParams(ArrayList<String> keys, Map<String,Param> params){
        for(int i = 0; i < keys.size();i++){
            Param p = params.get(keys.get(i));
            mParameters.put(keys.get(i),p);
            String text = p.toString();
            mTexts.get(keys.get(i)).setText(text);
        }
    }

    @TargetApi(23)
    private TextView createKeyTextView(String s){
        TextView tag = new TextView(getContext());
        tag.setText(s);
        tag.setTextAppearance(R.style.TagTextView);
        tag.setSingleLine(true);
        tag.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        tag.setWidth((int)(mBlock.getBlockWidth()*1.0/4));
        return tag;
    }

    @TargetApi(23)
    private TextView createValueTextView(String s){
        TextView value = new TextView(getContext());
        value.setText(s);
        value.setTextAppearance(R.style.ValueTextView);
        value.setSingleLine(true);
        value.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        value.setWidth((int)(mBlock.getBlockWidth()*3.0/4));
        return value;
    }

    //create the linear layout for each row in the block parameters
    private LinearLayout createBlockRowView(String key, String value){
        LinearLayout rowParams = new LinearLayout(getContext());
        rowParams.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = getWrapContentParams();
        rowParams.setLayoutParams(params);
        TextView keyView = createKeyTextView(key), valueView = createValueTextView(value);
        rowParams.addView(keyView);
        rowParams.addView(valueView);
        /**store the key-textview pair into mTexts for later update*/
        mTexts.put(key,valueView);
        return rowParams;
    }

    private LinearLayout createBlockParamsView(){
        LinearLayout blockParams = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = getWrapContentParams();
        blockParams.setOrientation(LinearLayout.VERTICAL);
        blockParams.setLayoutParams(params);
        for(int i = 0; i < mParameterKeys.size();i++){
            String key = mParameterKeys.get(i);
            String value = mParameters.get(key).toString();
            blockParams.addView(createBlockRowView(key,value));
        }
        return blockParams;
    }

    private LinearLayout.LayoutParams getWrapContentParams(){
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public ArrayList<String> getParameterKeys(){
        return mParameterKeys;
    }

    public Param getParameter(String key){
        return mParameters.get(key);
    }

    public ArrayList<Port> getInPorts(){
        return this.mInPorts;
    }

    public ArrayList<Port> getOutPorts(){
        return this.mOutPorts;
    }

    public BlockTemplate getBlockTemplate(){ return mBlock; }
}