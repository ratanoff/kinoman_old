package ru.ratanov.kinomanmvp.ui.activity.base;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;

import ru.ratanov.kinomanmvp.R;

public class BaseActivity extends MvpAppCompatActivity {

    private SearchView mSearchView;
    private SearchHistoryTable mHistoryTable;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            mSearchView.open(true, item);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void setupSearchView() {
        mSearchView = (SearchView) findViewById(R.id.searchview);

        mHistoryTable = new SearchHistoryTable(this);
        mHistoryTable.setHistorySize(5);

        SearchAdapter searchAdapter = new SearchAdapter(this, new ArrayList<SearchItem>(5));
        searchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                String query = textView.getText().toString();
                doSearch(query);
            }
        });

        mSearchView.setAdapter(searchAdapter);

        mSearchView.setHint(R.string.search);
        mSearchView.setVersion(SearchView.VERSION_MENU_ITEM);
        mSearchView.setVersionMargins(SearchView.VERSION_MARGINS_MENU_ITEM);
        mSearchView.setTheme(SearchView.THEME_LIGHT);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    void doSearch(String query) {
        mHistoryTable.addItem(new SearchItem(query));
        mSearchView.close(true);
        Snackbar.make(mSearchView, query, Snackbar.LENGTH_SHORT).show();
    }


}