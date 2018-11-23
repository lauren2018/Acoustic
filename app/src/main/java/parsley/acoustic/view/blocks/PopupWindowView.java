package parsley.acoustic.view.blocks;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.view.basic.Param;

/**
 * Created by tomsp on 2017/12/27.
 */

public class PopupWindowView {
    private PopupWindow mWindow;
    private BlockView mBlockView;
    private ParamsBox mBox;
    private Map<String, EditText> mParamValues;
    private Button applyButton;
    private Button cancelButton;

    public PopupWindowView(Context context, BlockView blockView, View layout){
        mBlockView = blockView;
        mBox = new ParamsBox(context,blockView);
        show(layout);
    }

    private void show(View layout){
        mParamValues = mBox.getParamValues();
        applyButton = mBox.getApplyButton();
        cancelButton = mBox.getCancelButton();
        mWindow = new PopupWindow(mBox.getBoxLayout(), LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        mWindow.showAtLocation(layout, Gravity.CENTER,10,10);
        mWindow.setFocusable(true);
        mWindow.update();
        applyButton.setOnClickListener(new View.OnClickListener() {
            ArrayList<String> mParameterKeys = mBlockView.getParameterKeys();
            Map<String, Param> mParameters = new HashMap<String,Param>();

            @Override
            public void onClick(View v) {
                for(int i = 0; i < mParameterKeys.size();i++){
                    Param p = mBlockView.getParameter(mParameterKeys.get(i));
                    p.setValue(mParamValues.get(mParameterKeys.get(i)).getText().toString());
                    mParameters.put(mParameterKeys.get(i), p);
                }
                mBlockView.updateParams(mParameterKeys,mParameters);
                mWindow.dismiss();
            }

        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

    }
}
