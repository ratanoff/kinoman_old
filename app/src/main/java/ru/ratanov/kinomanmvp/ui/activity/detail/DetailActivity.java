package ru.ratanov.kinomanmvp.ui.activity.detail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.ratanov.kinomanmvp.R;
import ru.ratanov.kinomanmvp.model.content.Film;
import ru.ratanov.kinomanmvp.presentation.view.detail.DetailView;
import ru.ratanov.kinomanmvp.presentation.presenter.detail.DetailPresenter;
import ru.ratanov.kinomanmvp.ui.activity.base.BaseActivity;
import ru.ratanov.kinomanmvp.ui.fragment.detail.SameFragment;


import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;

public class DetailActivity extends BaseActivity implements DetailView {

    @InjectPresenter
    DetailPresenter mDetailPresenter;

    @BindView(R.id.detail_layout_main) ConstraintLayout mainLayout;
    @BindView(R.id.detail_title) TextView title;
    @BindView(R.id.detail_poster) ImageView poster;
    @BindView(R.id.detail_quality) TextView quality;
    @BindView(R.id.detail_video) TextView video;
    @BindView(R.id.detail_audio) TextView audio;
    @BindView(R.id.detail_size) TextView size;
    @BindView(R.id.detail_length) TextView length;
    @BindView(R.id.detail_translate) TextView translate;
    @BindView(R.id.detail_year) TextView year;
    @BindView(R.id.detail_genre) TextView genre;
    @BindView(R.id.detail_rating) TextView rating;
    @BindView(R.id.detail_date_title) TextView dateTitle;
    @BindView(R.id.detail_date) TextView date;
    @BindView(R.id.detail_seeds) TextView seeds;
    @BindView(R.id.detail_description) TextView description;

    public static final String TAG = "DetailActivity";
    public static final String EXTRA_URL = "extra_url";

    private String mUrl;
    private String kpUrl;

    private ProgressDialog mProgressDialog;

    public static Intent newIntent(final Context context, String url) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);

        setupToolBar();
        setupSearchView();

        //
        mainLayout.setVisibility(View.VISIBLE);

        mUrl = getIntent().getStringExtra(EXTRA_URL);
        initProgressDialog();

//        mDetailPresenter.loadFilm(mUrl);
    }


    void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    public void showProgress(String message) {
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    @Override
    public void hideProgress() {
        mProgressDialog.dismiss();
    }

    @Override
    public void updatePage(final Film film) {
        kpUrl = film.getKpUrl();

        title.setText(film.getTitle());
        Picasso.with(this).load(film.getPosterUrl()).into(poster);
        quality.setText(film.getQuality());
        video.setText(film.getVideo());
        audio.setText(film.getAudio());
        size.setText(film.getSize());
        length.setText(film.getLength());
        translate.setText(film.getTranslate());
        year.setText(film.getYearText());
        genre.setText(film.getGenre());
        rating.setText(film.getRating());
        dateTitle.setText(film.getDateTitle());
        date.setText(film.getDate());
        seeds.setText(film.getSeeds());
        description.setText(film.getDescription());
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = DescriptionActivity.newIntent(DetailActivity.this, film.getDescription());
                startActivity(intent);
            }
        });

        mainLayout.setVisibility(View.VISIBLE);

        String sameLink = Uri.parse(film.getSameLink())
                .buildUpon()
                .appendQueryParameter("d", film.getYearNumber())
                .appendQueryParameter("t", "1")
                .build()
                .toString();

        initSameFragment(sameLink);
    }

    void initSameFragment(String sameLink) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.detail_fragment_container);

        if (fragment == null) {
            fragment = SameFragment.newInstance(sameLink);
            fm.beginTransaction()
                    .add(R.id.detail_fragment_container, fragment)
                    .commit();
        }

    }

    // Trailer

    @OnClick(R.id.detail_trailer_button)
    public void loadTrailer(View view) {
        mDetailPresenter.loadTrailer(kpUrl);
    }

    @Override
    public void showTrailer(String trailerUrl) {
        if (trailerUrl != null) {
            Intent playTrailer = new Intent(Intent.ACTION_VIEW);
            playTrailer.setDataAndType(Uri.parse(trailerUrl), "video/mp4");
            startActivity(playTrailer);
        } else {
            Snackbar.make(mainLayout, "Трейлер не найден", Snackbar.LENGTH_SHORT).show();
        }
    }

    // Download

    @OnClick(R.id.detail_download_button)
    public void download(View view) {
        mDetailPresenter.download(this, mUrl);
    }

    @Override
    public void showAddingProgress() {
        Snackbar snackbar = Snackbar.make(mainLayout, "Добавлене торрента...", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.addView(new ProgressBar(this));
        snackbar.show();
    }

    @Override
    public void showAddingResult(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("ОК", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
    }

    public String getUrl() {
        return mUrl;
    }
}
