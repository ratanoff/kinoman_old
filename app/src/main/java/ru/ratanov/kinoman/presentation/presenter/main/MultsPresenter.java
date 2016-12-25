package ru.ratanov.kinoman.presentation.presenter.main;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import ru.ratanov.kinoman.model.content.TopItem;
import ru.ratanov.kinoman.model.parsers.FilmParser;
import ru.ratanov.kinoman.presentation.view.main.MainView;

/**
 * Created by ACER on 05.12.2016.
 */

@InjectViewState
public class MultsPresenter extends MvpPresenter<MainView> {
    private static final String TAG = "TopFragmentLog";
    private String mCategory = "2";

    public MultsPresenter() {
    }


    public void loadData() {
        FilmParser filmParser = new FilmParser(this);
        filmParser.getTopFilms(mCategory);
    }

    public void onLoadComplete(List<TopItem> topItems) {
        getViewState().setupAdapter(topItems);
    }
}
