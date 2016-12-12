package ru.ratanov.kinomanmvp.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.arellomobile.mvp.presenter.InjectPresenter;

import ru.ratanov.kinomanmvp.presentation.presenter.main.MultsPresenter;
import ru.ratanov.kinomanmvp.presentation.view.main.MainView;

/**
 * Created by ACER on 05.12.2016.
 */

public class MultsFragment extends BaseFragment implements MainView {

    @InjectPresenter
    MultsPresenter mMultsPresenter;

    public MultsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMultsPresenter.loadData();
    }
}
