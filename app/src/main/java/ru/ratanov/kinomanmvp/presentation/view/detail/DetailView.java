package ru.ratanov.kinomanmvp.presentation.view.detail;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.ratanov.kinomanmvp.model.content.Film;

@StateStrategyType(SkipStrategy.class)
public interface DetailView extends MvpView {
    void showProgress(String message);
    void hideProgress();

    void updatePage(Film film);
    void showTrailer(String trailerUrl);

    void showAddingProgress();

    void showAddingResult(String message);
}
