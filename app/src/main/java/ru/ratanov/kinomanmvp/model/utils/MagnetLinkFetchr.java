package ru.ratanov.kinomanmvp.model.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arellomobile.mvp.MvpPresenter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.ratanov.kinomanmvp.model.net.TorrentAPI;
import ru.ratanov.kinomanmvp.presentation.presenter.detail.DetailPresenter;
import ru.ratanov.kinomanmvp.presentation.presenter.detail.SamePresenter;

/*
Single method of this class getHashLink(Context context, String linkId)
takes 2 parameters: Context and Kinozal.tv film linkId (e.g. '12345678').
Then load into background WebView page (e.g. 'http://kinozal.me/details.php?id=12345678').
Through injection of JavaScript commands trying to:
    - Login on page
    - Show hidden block with magnet link
    - Pass HTML source of page to predefined 'MyJavaInterface'
      and trying to parse magnet link
If success, pass magnet link to 'TorrentAPI' class for add new download
*/
public class MagnetLinkFetchr {

    public static final String TAG = "MagnetLinkFetchr";

    private static String SERVER;
    private static String PORT;
    private static String LOGIN;
    private static String PASSWORD;


    private Activity mActivity;
    private MvpPresenter mMvpPresenter;
    private WebView mWebView;
    private String magnetLinkHash;

    private class MyJavaInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHtml(String html) {
            Log.i(TAG, "processHtml: " + html.length());
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("div#containerdata").select("li");
            if (elements.size() < 3) {
                parse();
            }
            for (Element li : elements) {
                if (li.text().contains("Инфо хеш")) {
                    magnetLinkHash = li.text().substring(10);
                    Log.i(TAG, "processHtml: " + magnetLinkHash);
                    String magnetLink = "magnet:?xt=urn:btih:" + magnetLinkHash;

                    TorrentAPI torrentAPI = new TorrentAPI(mActivity, mMvpPresenter);
                    torrentAPI.addTorrent(magnetLink);

                    break;
                } else {
                    Log.i(TAG, "processHtml: not found");

                    showResultMessage("Ссылка не найдена", false);
                }
            }
        }
    }

    public void getHashLink(Activity activity, MvpPresenter mvpPresenter, final String linkId) {

        mActivity = activity;
        mMvpPresenter = mvpPresenter;

        if (!isServerSettedUp()) {
            showResultMessage("Заполните данные сервера в настройках", true);
            return;
        }

        mWebView = new WebView(mActivity);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.addJavascriptInterface(new MyJavaInterface(), "HTMLOUT");
        // Block pop-ups
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "shouldOverrideUrlLoading: ");
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Login
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript("javascript: {" +
                                    "document.forms[1].username.value = 'rbaloo';" +
                                    "document.forms[1].password.value = '756530';" +
                                    "document.forms[1].submit(); };"
                            , null);
                } else {
                    mWebView.loadUrl("javascript: {" +
                            "document.forms[1].username.value = 'rbaloo';" +
                            "document.forms[1].password.value = '756530';" +
                            "document.forms[1].submit(); };");
                }
                Log.i(TAG, "onPageFinished: Login");

                // Show hash

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript("get_torm(" + linkId + ",2,'Список файлов всего 111','')", null);
                } else {
                    mWebView.loadUrl("javascript:get_torm(" + linkId + ",2,'Список файлов всего 111','');");
                }
                Log.i(TAG, "onPageFinished: Show Hash");

                // Parse
                parse();
            }
        });

        String url = "http://kinozal.me/details.php?id=" + linkId;
        mWebView.loadUrl(url);
        Log.i(TAG, "loadUrl" + url);
    }

    private void parse() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:window.HTMLOUT.processHtml(document.getElementsByTagName('html')[0].innerHTML);");
            }
        });
//        mWebView.loadUrl("javascript:window.HTMLOUT.processHtml(document.getElementsByTagName('html')[0].innerHTML);");
        Log.i(TAG, "onPageFinished: Parse");
    }

    private void showResultMessage(String message, boolean setupServer) {
        if (mMvpPresenter instanceof DetailPresenter) {
            ((DetailPresenter) mMvpPresenter).showResult(message, setupServer);
        } else if (mMvpPresenter instanceof SamePresenter) {
            ((SamePresenter) mMvpPresenter).showResult(message, setupServer);
        }
    }

    private boolean isServerSettedUp() {
        SERVER = QueryPreferences.getStoredQuery(mActivity, QueryPreferences.PREF_SERVER);
        PORT = QueryPreferences.getStoredQuery(mActivity, QueryPreferences.PREF_PORT);
        LOGIN = QueryPreferences.getStoredQuery(mActivity, QueryPreferences.PREF_LOGIN);
        PASSWORD = QueryPreferences.getStoredQuery(mActivity, QueryPreferences.PREF_PASSWORD);

        return !(TextUtils.isEmpty(SERVER) ||
                TextUtils.isEmpty(PORT) ||
                TextUtils.isEmpty(LOGIN) ||
                TextUtils.isEmpty(PASSWORD));
    }
}

