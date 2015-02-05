package org.eecs499.russtrup.ketchup;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


// This class exists to enable efficient use of the Toolbar to allow
// for a material theme in KitKat applications
public abstract class BaseActivity extends ActionBarActivity
        implements  MyShowsFragment.OnFragmentInteractionListener,
                    FindShowsFragment.OnFragmentInteractionListener,
                    SettingsFragment.OnFragmentInteractionListener {

    private int currentSelectedPosition = 0;
    private String currentTitle;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager navigationLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResource());

        initView();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.bringToFront();
        }

        initDrawer();

    }

    protected void loadFragment(int position) {
        // No loading fragment for header
        if (position < 0) return;

        String[] menuItems = getResources().getStringArray(R.array.nav_items);

        currentTitle = menuItems[position];
        getSupportActionBar().setTitle(currentTitle);

        currentSelectedPosition = position;

        ContentFragment contentFragment = getContentFragment(position);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, contentFragment);
        fragmentTransaction.commit();
    }

    protected void loadCurrentFragment() {
        loadFragment(currentSelectedPosition);
    }


    private void initView() {

        String[] nav_items = getResources().getStringArray(R.array.nav_items);
//        String[] nav_icons = getResources().getStringArray(R.array.nav_icons);
        int nav_icons[] = {R.drawable.ic_action_computer,R.drawable.ic_action_computer,R.drawable.ic_action_settings};
        int[] nav_ids = {0, 1, 2};
        String NAME = "Russell Trupiano";
        String EMAIL = "russelltrupiano@gmail.com";
        int PIC = R.drawable.ic_launcher;

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.left_drawer);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerViewAdapter = new NavAdapter(nav_items, nav_icons, NAME, EMAIL, PIC, this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);


//        leftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                loadFragment(position);
//
//
//            }
//        });
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(mRecyclerView);
    }

    private ContentFragment getContentFragment(int position) {

        switch(position) {
            case 0:
                return new MyShowsFragment();
            case 1:
                return new FindShowsFragment();
            case 2:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        )
        {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }


        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Used by children to load the proper layout
    protected abstract int getLayoutResource();

}
