package parsley.acoustic;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import parsley.acoustic.view.blocks.DragBoardLayout;
import parsley.acoustic.view.fragment.BoardFragment;
import parsley.acoustic.view.fragment.ModuleListFragment;


public class MainActivity extends AppCompatActivity {
    //drawer_navigation_layout
    private String [] mNavSelections;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    //module_list_layout
    LayoutInflater inflater;
    private String [][] mModules;
    private GridView[] mModuleClasses;

    Fragment board_frag;
    Fragment modulelist_frag;
    FragmentManager fm;

    //layout
    DrawerLayout parentLayout;
    DragBoardLayout mDragBoard_lo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mNavSelections = getResources().getStringArray(R.array.nav_selections);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /**Initialize a home page, home page is a framelayout*/
        board_frag = new BoardFragment();
        parentLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.home, board_frag,"module_board").commitNow();
        /**drawer navigation**/
        mDrawerList = (ListView) findViewById(R.id.left_nav);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item,mNavSelections));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            selectItem(position);
        }

    }

    private void selectItem(int position){
        if (position == 0){
            //DrawerLayout parentLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            modulelist_frag = (ModuleListFragment) fm.findFragmentByTag("module_list");
            if(modulelist_frag == null || !modulelist_frag.isAdded()){
                modulelist_frag = new ModuleListFragment();
                fm.beginTransaction().add(R.id.home,modulelist_frag,"module_list").
                        hide(board_frag).commitNow();
                init_module_list();
            }else{
                fm.beginTransaction().hide(board_frag).show(modulelist_frag).commitNow();
            }
            mDrawerList.setItemChecked(position, true);
            //setTitle(mNavSelections[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }


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

    @Override
    public void onStart(){
        super.onStart();
        BoardFragment fragment = (BoardFragment)fm.findFragmentByTag("module_board");
        mDragBoard_lo = fragment.getDragBoardLayout();
        mDragBoard_lo.initListener();
        WindowManager wm = (WindowManager)getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        mDragBoard_lo.initCanvas(display.getWidth(), display.getHeight());

    }

    public void onMoudleSelected(){}
}
