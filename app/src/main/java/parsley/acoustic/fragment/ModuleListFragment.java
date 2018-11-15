package parsley.acoustic.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import parsley.acoustic.R;

public class ModuleListFragment extends Fragment {
    static public String VIEW_NAME = "module_list";
    private GridView mGridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //mGridView = (GridView) findViewById(R.id.);
        View v = inflater.inflate(R.layout.module_list, container, false);
        return v;
    }
}
