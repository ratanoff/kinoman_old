package ru.ratanov.kinomanmvp.ui.fragment.detail;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;
import java.util.Locale;

import ru.ratanov.kinomanmvp.R;
import ru.ratanov.kinomanmvp.model.adapters.SameAdapter;
import ru.ratanov.kinomanmvp.model.content.SameItem;
import ru.ratanov.kinomanmvp.presentation.presenter.detail.SamePresenter;
import ru.ratanov.kinomanmvp.presentation.view.detail.SameView;
import ru.ratanov.kinomanmvp.ui.activity.detail.DetailActivity;

public class SameFragment extends MvpAppCompatFragment implements SameView {

    @InjectPresenter
    SamePresenter mSamePresenter;

    public static final String ARG_URL = "arg_url";

    ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    TextView mCountTextView;

    public static SameFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        SameFragment fragment = new SameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());

        String searchUrl = getArguments().getString(ARG_URL);
        mSamePresenter.loadSameFilms(searchUrl);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_same_fragment, container, false);

        mCountTextView = (TextView) view.findViewById(R.id.same_fragment_count_textview);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        return view;
    }

    @Override
    public void setupAdapter(List<SameItem> items) {
        String parentUrl = ((DetailActivity) getActivity()).getUrl();
        for (SameItem sameItem : items) {
            if (parentUrl.equals(sameItem.getPageUrl())) {
                items.remove(sameItem);
                break;
            }
        }

        mCountTextView.setText(String.format(Locale.ROOT, "%d %s", items.size(), getString(R.string.found_same_films)));
        SameAdapter adapter = new SameAdapter(getActivity(),mSamePresenter , items);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void showProgress() {
        mProgressDialog.show();
    }

    @Override
    public void hideProgress() {
        mProgressDialog.dismiss();
    }

    @Override
    public void showAddingProgress() {
        Snackbar snackbar = Snackbar.make(mRecyclerView, "Добавлене торрента...", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.addView(new ProgressBar(getActivity()));
        snackbar.show();
    }

    @Override
    public void showAddingResult(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("ОК", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
    }
}
