package parsley.acoustic.view.fragment;

import android.content.Context;

import parsley.acoustic.digital.OFDM;
import parsley.acoustic.view.blocks.BlockView;

public class FunctionCallOnClick {
    static BlockView callOnClick(Context context, String s){
        switch (s){
            case "ofdm": OFDM ofdm = new OFDM(context); return new BlockView(context,"ofdm",ofdm.getKeys(),ofdm.getParams(),
                    ofdm.getInType(), ofdm.getOutType(),ofdm.getInNum(), ofdm.getOutNum()
                    );

        }
        return null;
    }
}
