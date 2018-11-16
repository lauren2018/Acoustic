package parsley.acoustic.view;

import android.content.Context;
import android.util.AttributeSet;

import parsley.acoustic.view.blocks.BlockView;

public class PreambleAttachView extends BlockView {

    public PreambleAttachView(Context context, Integer id) {
        super(context, id);
        params_config();
        setParams(context);
        _initViews();
    }

    public PreambleAttachView(Context context, AttributeSet attrs, Integer id) {
        super(context, attrs, id);
        params_config();
        setParams(context);
        _initViews();
    }

    private void params_config() {
        mInPortsNum = 1;
        mOutPortsNum = 1;
    }

} 