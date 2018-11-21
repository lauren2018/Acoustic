package parsley.acoustic.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parsley.acoustic.R;
import parsley.acoustic.tools.Array;
import parsley.acoustic.view.basic.DataType;
import parsley.acoustic.view.basic.Param;
import parsley.acoustic.view.blocks.BlockView;

public class ModuleListFragment extends Fragment {
    static public String VIEW_NAME = "module_list";
    private GridView mGridView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //mGridView = (GridView) findViewById(R.id.);
        LinearLayout moduleList_lo = (LinearLayout) inflater.inflate(R.layout.module_list, container, false);
        for(int i = 0; i < moduleList_lo.getChildCount();i++){

            GridView moduleClass = (GridView) moduleList_lo.getChildAt(i);
            moduleClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment moduleList_frag = fm.findFragmentByTag("module_list");
                    BoardFragment moduleBoard_frag = (BoardFragment) fm.findFragmentByTag("module_board");
                    BlockView v = clickOnItemEvent(moduleBoard_frag,adapter,view, position);
                    moduleBoard_frag.setBlockViewListener(v);
                    fm.beginTransaction().hide(moduleList_frag).show(moduleBoard_frag).commitNow();
                }
            });
        }
        return moduleList_lo;
    }

    public interface OnModuleSelectedListener{
        public void onModuleSelected();
    }

    private BlockView clickOnItemEvent(Fragment contentFrame, ArrayAdapter adapter, View v, int position){
        String prefix = getContext().getPackageName();
        String path = "signal";
        String s = (String) adapter.getItem(position);
        String className = prefix+'.'+path + '.' + s;
        ArrayList<String> keys = new ArrayList<>();
        keys.add("param1");
        keys.add("param2");
        HashMap<String, Param> kv = new HashMap<>();
        Param p1 = new Param("Param1", DataType.STRING,"hello, world!");
        Param p2 = new Param("Param2", DataType.STRING,"A test message!");
        kv.put("param1",p1);
        kv.put("param2",p2);
        try{
            Class tmp = Class.forName(className);
            Constructor cons = tmp.getDeclaredConstructor(Context.class,ArrayList.class,Map.class,Integer.class);
            BlockView newBlockView = (BlockView) cons.newInstance(getContext(),keys,kv,0);

            //cons.newInstance(getContext(),keys,kv,0);
            return newBlockView;
        }catch (Exception e){
            //do nothing
            e.printStackTrace();
            return null;
        }

    }

    private void onBlockViewTouchEvent(){

    }

}
