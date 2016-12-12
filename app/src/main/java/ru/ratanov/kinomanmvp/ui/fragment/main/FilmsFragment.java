package ru.ratanov.kinomanmvp.ui.fragment.main;

import android.os.Bundle;

import com.arellomobile.mvp.presenter.InjectPresenter;

import ru.ratanov.kinomanmvp.presentation.presenter.main.FilmsPresenter;
import ru.ratanov.kinomanmvp.presentation.view.main.MainView;

/**
 * Created by ACER on 05.12.2016.
 */

public class FilmsFragment extends BaseFragment implements MainView {

    @InjectPresenter
    FilmsPresenter mFilmsPresenter;

    public FilmsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilmsPresenter.loadData();
    }
}
