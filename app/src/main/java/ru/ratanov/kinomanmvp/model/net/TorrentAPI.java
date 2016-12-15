package ru.ratanov.kinomanmvp.model.net;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.MvpPresenter;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;

import cz.msebera.android.httpclient.Header;
import ru.ratanov.kinomanmvp.model.utils.QueryPreferences;
import ru.ratanov.kinomanmvp.presentation.presenter.detail.DetailPresenter;
import ru.ratanov.kinomanmvp.presentation.presenter.detail.SamePresenter;

public class TorrentAPI {
    static Activity sActivity;
    static MvpPresenter sMvpPresenter;

    public static final String TAG = "TorrentAPI";

    private static String token = null;
    private static String SERVER_URL = null;
    private static String sSERVER = "192.168.1.106";
    private static String sPORT = "8888";
    private static String sLOGIN = "admin";
    private static String sPASSWORD = "742882";

    public static void setContext(Activity activity, MvpPresenter mvpPresenter) {
        sActivity = activity;
        sMvpPresenter = mvpPresenter;
    }

    public static void getToken() {

        getServerSettings();

        if (sSERVER == null || sPORT == null || sLOGIN == null || sPASSWORD == null) {
//            SetServerDialog dialog = new SetServerDialog();
//            AppCompatActivity activity = (AppCompatActivity) sActivity;
//            dialog.show(activity.getSupportFragmentManager(), "add_server");
        } else {

            SERVER_URL = "http://" + sSERVER + ":" + sPORT + "/gui/";

            String url = SERVER_URL + "token.html";

            HttpClient.setBasicAuth(sLOGIN, sPASSWORD);
            HttpClient.get(url, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String doc = new String(responseBody);
                    token = Jsoup.parse(doc).select("div#token").text();
                    Log.i(TAG, "Got token " + token);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, "Failure get token: " + statusCode);
                }
            });
        }
    }

    private static void getServerSettings() {
        sSERVER = QueryPreferences.getStoredQuery(sActivity, QueryPreferences.PREF_SERVER);
        sPORT = QueryPreferences.getStoredQuery(sActivity, QueryPreferences.PREF_PORT);
        sLOGIN = QueryPreferences.getStoredQuery(sActivity, QueryPreferences.PREF_LOGIN);
        sPASSWORD = QueryPreferences.getStoredQuery(sActivity, QueryPreferences.PREF_PASSWORD);
    }

    public static void addTorrent(final String magnetLink) {

        SERVER_URL = "http://" + sSERVER + ":" + sPORT + "/gui/";

        String url = SERVER_URL + "token.html";

        Log.d(TAG, "addTorrent: " + url);

        HttpClient.setBasicAuth(sLOGIN, sPASSWORD);
        HttpClient.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String doc = new String(responseBody);
                token = Jsoup.parse(doc).select("div#token").text();
                Log.i(TAG, "Got token " + token);

                String url = Uri.parse(SERVER_URL)
                        .buildUpon()
                        .appendQueryParameter("token", token)
                        .appendQueryParameter("action", "add-url")
                        .appendQueryParameter("s", magnetLink)
                        .build()
                        .toString();

                HttpClient.get(url, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        showResultMessgae("Торрент добавлен");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        showResultMessgae("Ошибка добавления торрента (" + statusCode + ")");
                    }
                });

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showResultMessgae("Нет связи  с сервером (" + statusCode + ")");
            }
        });
    }

    private static void showResultMessgae(String message) {
        if (sMvpPresenter instanceof DetailPresenter) {
            ((DetailPresenter) sMvpPresenter).showResult(message, false);
        } else if (sMvpPresenter instanceof SamePresenter) {
            ((SamePresenter) sMvpPresenter).showResult(message, false);
        }
    }
}
