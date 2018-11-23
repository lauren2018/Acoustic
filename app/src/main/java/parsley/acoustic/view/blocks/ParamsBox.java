package parsley.acoustic.view.blocks;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.view.blocks.BlockView;
import parsley.acoustic.view.basic.Param;

/**
 * Created by tomsp on 2017/12/27.
 */

public class ParamsBox extends LinearLayout{
    private BlockView mBlockView;
    private LinearLayout paramsBox;
    private ArrayList<LinearLayout> mParamItem;
    private Map<String, EditText> mParamValues = new HashMap<String, EditText>();

    private ArrayList<String> mParameterKeys = new ArrayList<String>();
    private Map<String, Param> mParameters = new HashMap<String, Param>();
    private Button applyBtn = new Button(getContext());
    private Button cancelBtn = new Button(getContext());

    public ParamsBox(Context context, BlockView blockView){
        super(context);
        this.mBlockView = blockView;
        _initViews();
    }

    public ParamsBox(Context context, AttributeSet attrs, BlockView blockView){
        super(context,attrs);
        this.mBlockView = blockView;
        _initViews();
    }

    public LinearLayout getBoxLayout(){return this.paramsBox;}

    private void _initViews(){
        paramsBox = new LinearLayout(getContext());
        paramsBox.setBackgroundColor(Color.GRAY);
        paramsBox.setAlpha((float)0.9);
        paramsBox.setOrientation(LinearLayout.VERTICAL);
//        paramsBox = (LinearLayout) findViewById(R.id.params_box);
        ArrayList<String> paramKeys = mBlockView.getParameterKeys();
        for(int i = 0; i < paramKeys.size();i++){
            LinearLayout tmp = new LinearLayout(getContext());
            tmp.setOrientation(LinearLayout.HORIZONTAL);
            String key = paramKeys.get(i);
            mParameterKeys.add(key);
            Param p = mBlockView.getParameter(key);
            //String value = (Strin p.getValue();
            mParameters.put(key,p);
            TextView paramKey = new TextView(getContext());
            paramKey.setText(key+": ");
            EditText paramVal = new EditText(getContext());
           // paramVal.setEditableFactory(FOCUS_DOWN);
            paramVal.setText(p.toString());

            mParamValues.put(key,paramVal);

            paramsBox.addView(tmp);
            tmp.addView(paramKey);
            tmp.addView(paramVal);
        }
        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        //Button applyBtn = new Button(getContext());
        applyBtn.setText("Apply");
       // Button cancelBtn = new Button(getContext());
        cancelBtn.setText("Cancel");

        paramsBox.addView(buttonLayout);
        buttonLayout.addView(applyBtn);
        buttonLayout.addView(cancelBtn);


        applyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < mParameters.size();i++){
                    Param p = mParameters.get(mParameterKeys.get(i));
                    p.setValue( mParamValues.get(mParameterKeys.get(i)).getText().toString());
                    mParameters.put(mParameterKeys.get(i), p);
                    mBlockView.updateParams(mParameterKeys,mParameters);
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You Click Cancel!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public Button getApplyButton() {
        return this.applyBtn;
    }

    public Button getCancelButton() {
        return this.cancelBtn;
    }

    public Map<String,EditText> getParamValues(){
        return this.mParamValues;
    }

}
