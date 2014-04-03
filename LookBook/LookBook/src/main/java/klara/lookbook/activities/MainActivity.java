package klara.lookbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import klara.lookbook.R;
import klara.lookbook.SynchronizeService;
import klara.lookbook.fragments.AddItemFragment;
import klara.lookbook.fragments.AddShopFragment;
import klara.lookbook.fragments.HomeFragment;
import klara.lookbook.fragments.ItemDetailFragment;
import klara.lookbook.fragments.NavigationDrawerFragment;
import klara.lookbook.fragments.ViewItemFragment;
import klara.lookbook.fragments.ViewShopFragment;
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
            case NavigationDrawerFragment.SECTION_HOME:
                fragment = HomeFragment.newInstance(position);
                backStackName = "homeFragment";
                break;
            case NavigationDrawerFragment.SECTION_ADD_ITEM:
                fragment = AddItemFragment.newInstance(position);
                backStackName = "AddItemFragment";
                break;
            case NavigationDrawerFragment.SECTION_ADD_SHOP:
                fragment = AddShopFragment.newInstance(position);
                backStackName = "AddShopFragment";
                break;
            case NavigationDrawerFragment.SECTION_VIEW_ITEMS:
                fragment = ViewItemFragment.newInstance(position);
                backStackName = "ViewItemFragment";
                break;
            case NavigationDrawerFragment.SECTION_VIEW_SHOPS:
                fragment = ViewShopFragment.newInstance(position);
                backStackName = "ViewShopFragment";
                break;
            case NavigationDrawerFragment.SECTION_LOG_OUT:
                AppPref.put(this, AppPref.KEY_PASSWORD, "");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }

        if(fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, fragment.getClass().getName())
                    .addToBackStack(backStackName)
                    .commit();
        }
    }

    public void onCustomSectionSelected(int position, Object params) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        String backStackName = "default";
        switch(position) {

            case NavigationDrawerFragment.SECTION_ITEM_DETAIL:
                fragment = ItemDetailFragment.newInstance(position, (Integer)params);
                backStackName = "ItemDetailFragment";
                break;
        }

        if(fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, fragment.getClass().getName())
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
                mTitle = getString(R.string.add_shop_frag_title);
                break;
            case NavigationDrawerFragment.SECTION_VIEW_ITEMS:
                mTitle = getString(R.string.view_item_frag_title);
                break;
            case NavigationDrawerFragment.SECTION_VIEW_SHOPS:
                mTitle = getString(R.string.view_shop_frag_title);
                break;
            case NavigationDrawerFragment.SECTION_ITEM_DETAIL:
                mTitle = getString(R.string.item_detail_frag_title);
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
        if(id == R.id.action_add_new_shop) {
            mNavigationDrawerFragment.selectItem(NavigationDrawerFragment.SECTION_ADD_SHOP);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
