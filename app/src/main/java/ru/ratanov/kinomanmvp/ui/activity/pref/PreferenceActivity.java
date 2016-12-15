package ru.ratanov.kinomanmvp.ui.activity.pref;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.ratanov.kinomanmvp.R;
import ru.ratanov.kinomanmvp.model.net.TorrentAPI;

public class PreferenceActivity extends android.preference.PreferenceActivity implements TorrentAPI.OnTestConnectionListener {

    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setContentView(R.layout.pref_button_test);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.test_button)
    void test(View view) {
        mView = view;
        TorrentAPI torrentAPI = new TorrentAPI(this);
        torrentAPI.testConnection();

    }

    @Override
    public void onTestFinish(boolean result) {
        if (result) {
            Snackbar.make(mView, "Связь с сервером установлена", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mView, "Нет связи с сервером", Snackbar.LENGTH_SHORT).show();
        }
    }
}
