package klara.lookbook.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import klara.lookbook.R;
import klara.lookbook.SynchronizeService;
import klara.lookbook.fragments.AddItemFragment;
import klara.lookbook.fragments.AddShopFragment;
import klara.lookbook.fragments.HomeFragment;
import klara.lookbook.fragments.NavigationDrawerFragment;
import klara.lookbook.fragments.ViewItemFragment;
import klara.lookbook.utils.AppPref;

public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        if(savedInstanceState == null) {
            SynchronizeService.startActionSynchronize(this);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        String backStackName = "default";
        switch(position) {
            case 0:
                fragment = HomeFragment.newInstance(position);
                backStackName = "homeFragment";
                break;
            case 1:
                fragment = AddItemFragment.newInstance();
                backStackName = "AddItemFragment";
                break;
            case 2:
                fragment = ViewItemFragment.newInstance();
                backStackName = "viewItemFragment";
                break;
            case 3:
                fragment = AddShopFragment.newInstance();
                backStackName = "AddShopFragment";
                break;
            case 4:
                AppPref.put(this, AppPref.KEY_PASSWORD, "");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }

        if(fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(backStackName)
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case NavigationDrawerFragment.SECTION_HOME:
                mTitle = getString(R.string.home_frag_title);
                break;
            case NavigationDrawerFragment.SECTION_ADD_ITEM:
                mTitle = getString(R.string.add_item_frag_title);
                break;
            case NavigationDrawerFragment.SECTION_ADD_SHOP:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_add_new_item) {
            mNavigationDrawerFragment.selectItem(NavigationDrawerFragment.SECTION_ADD_ITEM);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
