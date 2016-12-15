package ru.ratanov.kinomanmvp.presentation.view.search;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import ru.ratanov.kinomanmvp.model.content.SearchItem;

public interface SearchView extends MvpView {
    void showProgress();
    void hideProgress();
    void updatePage(List<SearchItem> items);
}
