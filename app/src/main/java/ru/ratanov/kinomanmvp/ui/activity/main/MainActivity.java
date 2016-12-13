package ru.ratanov.kinomanmvp.ui.activity.main;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ru.ratanov.kinomanmvp.R;
import ru.ratanov.kinomanmvp.ui.activity.base.BaseActivity;
import ru.ratanov.kinomanmvp.ui.fragment.main.FilmsFragment;
import ru.ratanov.kinomanmvp.ui.fragment.main.MultsFragment;
import ru.ratanov.kinomanmvp.ui.fragment.main.SerialsFragment;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        //
        setupToolBar();
        setupSearchView();
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        adapter.addFragment(new FilmsFragment(), "Фильмы");
        adapter.addFragment(new MultsFragment(), "Мульты");
        adapter.addFragment(new SerialsFragment(), "Сериалы");

        viewPager.setAdapter(adapter);
    }


    // Adapter for ViewPager
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            notifyDataSetChanged();
        }
    }

}
