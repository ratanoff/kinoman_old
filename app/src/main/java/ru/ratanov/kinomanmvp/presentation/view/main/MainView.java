package ru.ratanov.kinomanmvp.presentation.view.main;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import ru.ratanov.kinomanmvp.model.content.TopItem;

public interface MainView extends MvpView {
    void setupAdapter(List<TopItem> items);
    void notifyFilmBlocked();
}
