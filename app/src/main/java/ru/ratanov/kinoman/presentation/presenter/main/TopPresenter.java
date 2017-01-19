package ru.ratanov.kinoman.presentation.presenter.main;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import ru.ratanov.kinoman.model.content.TopItem;
import ru.ratanov.kinoman.model.parsers.FilmParser;
import ru.ratanov.kinoman.presentation.view.main.MainView;

/**
 * Created by ACER on 15.01.2017.
 */

@InjectViewState
public class TopPresenter extends MvpPresenter<MainView> {

    public TopPresenter() {
    }

    public void loadData(String category) {
        FilmParser filmParser = new FilmParser(this);
        filmParser.getTopFilms(category);
    }

    public void onLoadComplete(List<TopItem> topItems) {
        getViewState().setupAdapter(topItems);
    }
}
