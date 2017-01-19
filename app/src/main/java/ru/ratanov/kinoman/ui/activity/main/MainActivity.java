package ru.ratanov.kinoman.ui.activity.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;


import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ratanov.kinoman.R;
import ru.ratanov.kinoman.model.query.FilterParams;
import ru.ratanov.kinoman.model.views.LabelledSpinner;
import ru.ratanov.kinoman.ui.activity.base.BaseActivity;
import ru.ratanov.kinoman.ui.fragment.main.TopFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";
    private static TabLayout mTabs;
    private Adapter mAdapter;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(viewPager);
        // Setup ToolBar
        setupToolBar();
        setupSearchView();
        // Setup FAB
        setupFAB();
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new Adapter(getSupportFragmentManager());

        mAdapter.addFragment(new TopFragment(), "Фантастика, фэнтэзи");
        mAdapter.addFragment(new TopFragment(), "Мульты");
        mAdapter.addFragment(new TopFragment(), "Сериалы");

        viewPager.setAdapter(mAdapter);
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
        }
    }

    private void setupFAB() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFab.hide();

                new FilterDialogFragment().show(getSupportFragmentManager(), "filter");
            }
        });
    }

    public static class FilterDialogFragment extends BottomSheetDialogFragment {

        private String category_params = "0";
        private String year_params = "0";
        private String country_params = "0";
        private String format_params = "0";
        private String added_params = "0";
        private String sort_params = "0";

        @BindView(R.id.category_spinner) LabelledSpinner mCategorySpinner;
        @BindView(R.id.year_spinner) LabelledSpinner mYearSpinner;
        @BindView(R.id.country_spinner) LabelledSpinner mCountrySpinner;
        @BindView(R.id.format_spinner) LabelledSpinner mFormatSpinner;
        @BindView(R.id.added_spinner) LabelledSpinner mAddedSpinner;
        @BindView(R.id.sort_spinner) LabelledSpinner mSortSpinner;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.main_bottom_sheet_filter, container, false);

            ButterKnife.bind(this, view);

            initSpinner(mCategorySpinner, "Раздел", FilterParams.CATEGORY_PARAMS);
            initSpinner(mYearSpinner, "Год выпуска", FilterParams.YEAR_PARAMS);
            initSpinner(mCountrySpinner, "Страна", FilterParams.COUNTRY_PARAMS);
            initSpinner(mFormatSpinner, "Формат", FilterParams.FORMAT_PARAMS);
            initSpinner(mAddedSpinner, "Добавлен", FilterParams.ADDED_PARAMS);
            initSpinner(mSortSpinner, "Сортировать", FilterParams.SORT_PARAMS);

            Button updateButton = (Button) view.findViewById(R.id.bottom_sheet_update_button);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mYearSpinner.setSelection(2);

                    String url = Uri.parse("https://kinozal-tv.appspot.com/top.php")
                            .buildUpon()
                            .appendQueryParameter("t", category_params)
                            .appendQueryParameter("d", year_params)
                            .appendQueryParameter("k", country_params)
                            .appendQueryParameter("f", format_params)
                            .appendQueryParameter("w", added_params)
                            .appendQueryParameter("s", sort_params)
                            .build()
                            .toString();

                    System.out.println(url);

                    int page = mTabs.getSelectedTabPosition();
                    TabLayout.Tab tab = mTabs.getTabAt(page);
                    System.out.println(tab.getText());
                    tab.setText(year_params);
                }
            });

            return view;
        }

        private void initSpinner(LabelledSpinner spinner, String label, final ArrayList<FilterParams> paramsArrayList) {

            spinner.setItemsArray(paramsArrayList);
            spinner.setLabelText(label);
            spinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
                @Override
                public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                    FilterParams filterParams = (FilterParams) adapterView.getSelectedItem();
                    switch (filterParams.getParamKey()) {
                        case "t":
                            category_params = filterParams.getId();
                            break;
                        case "d":
                            year_params = filterParams.getId();
                            break;
                        case "k":
                            country_params = filterParams.getId();
                            break;
                        case "f":
                            format_params = filterParams.getId();
                            break;
                        case "w":
                            added_params = filterParams.getId();
                            break;
                        case "s":
                            sort_params = filterParams.getId();
                            break;
                    }
                }

                @Override
                public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

                }
            });
        }
    }


}
