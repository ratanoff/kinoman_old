package ru.ratanov.kinomanmvp.presentation.presenter.detail;

import android.app.Activity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import ru.ratanov.kinomanmvp.model.content.SameItem;
import ru.ratanov.kinomanmvp.model.parsers.FilmParser;
import ru.ratanov.kinomanmvp.model.utils.MagnetLinkFetchr;
import ru.ratanov.kinomanmvp.presentation.view.detail.SameView;

@InjectViewState
public class SamePresenter extends MvpPresenter<SameView> {

    public void loadSameFilms(String searchUrl) {
        new FilmParser(this).getSameFilms(searchUrl);
    }

    public void showSameFilms(List<SameItem> items) {
        getViewState().setupAdapter(items);
    }

    public void download(Activity activity, String url) {
        getViewState().showAddingProgress();
        new MagnetLinkFetchr().getHashLink(activity, this, url.substring(33));
    }

    public void showResult (String result) {
        getViewState().showAddingResult(result);
    }
}
