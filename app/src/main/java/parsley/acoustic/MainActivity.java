package parsley.acoustic;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import parsley.acoustic.fragment.ModuleListFragment;

import static android.media.AudioTrack.MODE_STREAM;


public class MainActivity extends AppCompatActivity {
    //drawer_navigation_layout
    private String [] mNavSelections;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    //module_list_layout
    LayoutInflater inflater;
    private String [][] mModules;
    private GridView[] mModuleClasses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mNavSelections = getResources().getStringArray(R.array.nav_selections);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_nav);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item,mNavSelections));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            selectItem(position);
            init_module_list();

        }

    }

    private void selectItem(int position){
        Fragment fragment = new ModuleListFragment();
        Bundle args = new Bundle();
        args.putInt(ModuleListFragment.VIEW_NAME,position);
        fragment.setArguments(args);
        //Insert the fragment by placing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        FrameLayout currentLayout = (FrameLayout) findViewById(R.id.content_frame);
        currentLayout.removeAllViews();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame,fragment)
                .commitNow();
        mDrawerList.setItemChecked(position, true);
        //setTitle(mNavSelections[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    private void init_module_list(){
        String packageName = getApplicationContext().getPackageName();

        String [] class_name = getResources().getStringArray(R.array.class_name);
        String [] gridview_module_class_id = getResources().getStringArray(R.array.gridview_module_class_id);
        int moduleClassNum = getResources().getInteger(R.integer.module_class_num);
        mModules = new String [moduleClassNum][];
        mModuleClasses = new GridView[moduleClassNum];

        int class_name_id;
        int gridview_id;
        LinearLayout framelayout_module_list = (LinearLayout)findViewById(R.id.module_list);
        for(int i = 0; i < mModules.length;i++){
            class_name_id = getResources().getIdentifier(class_name[i],"array",packageName);
            gridview_id = getResources().getIdentifier(gridview_module_class_id[i],
                        "id",packageName);
            mModules[i] = getResources().getStringArray(class_name_id);
            mModuleClasses[i] = (GridView) findViewById(gridview_id);
            mModuleClasses[i].setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item,mModules[i]));
        }
    }
}
