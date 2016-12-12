package ru.ratanov.kinomanmvp.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.arellomobile.mvp.MvpAppCompatFragment;

import java.util.List;

import butterknife.BindDimen;
import ru.ratanov.kinomanmvp.R;
import ru.ratanov.kinomanmvp.model.adapters.TopAdapter;
import ru.ratanov.kinomanmvp.model.content.TopItem;
import ru.ratanov.kinomanmvp.presentation.view.main.MainView;

public class BaseFragment extends MvpAppCompatFragment implements MainView {

    public static final String TAG = "TopFragmentLog";

    private RecyclerView mRecyclerView;

    @BindDimen(R.dimen.tile_padding)
    int titlePadding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.base_recyclerview, container, false);
        mRecyclerView.setPadding(titlePadding, titlePadding, titlePadding, titlePadding);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        // Define number of columns
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int tileWidth = getResources().getDimensionPixelSize(R.dimen.tile_width);
                int numColumns = mRecyclerView.getWidth() / tileWidth;
                ((GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanCount(numColumns);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        return mRecyclerView;
    }

    @Override
    public void setupAdapter(List<TopItem> items) {
        TopAdapter adapter = new TopAdapter(getContext(), items);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void notifyFilmBlocked() {
        Snackbar.make(mRecyclerView, R.string.blocked, Snackbar.LENGTH_SHORT).show();
    }
}
