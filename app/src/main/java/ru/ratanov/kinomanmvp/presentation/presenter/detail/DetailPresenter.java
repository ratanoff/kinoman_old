package ru.ratanov.kinomanmvp.presentation.presenter.detail;


import android.app.Activity;
import android.content.Context;

import ru.ratanov.kinomanmvp.model.content.Film;
import ru.ratanov.kinomanmvp.model.parsers.FilmParser;
import ru.ratanov.kinomanmvp.model.utils.MagnetLinkFetchr;
import ru.ratanov.kinomanmvp.presentation.view.detail.DetailView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

@InjectViewState
public class DetailPresenter extends MvpPresenter<DetailView> {

    public void loadFilm(String url) {
        getViewState().showProgress(null);
        FilmParser filmParser = new FilmParser(this);
        filmParser.getFilm(url);
    }

    public void updatePage(Film film) {
        getViewState().hideProgress();
        getViewState().updatePage(film);
    }

    public void loadTrailer(String kpUrl) {
        getViewState().showProgress("Поиск трейлера");
        new FilmParser(this).getTrailerUrl(kpUrl);
    }

    public void showTrailer(String trailerUrl) {
        getViewState().hideProgress();
        getViewState().showTrailer(trailerUrl);
    }

    public void download(Activity activity, String url) {
        getViewState().showAddingProgress();
        new MagnetLinkFetchr().getHashLink(activity, this, url.substring(33));
    }

    public void showResult (String result) {
        getViewState().showAddingResult(result);
    }

}
