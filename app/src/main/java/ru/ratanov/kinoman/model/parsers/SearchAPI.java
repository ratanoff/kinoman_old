package ru.ratanov.kinoman.model.parsers;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.ratanov.kinoman.model.content.SearchItem;
import ru.ratanov.kinoman.presentation.presenter.search.SearchPresenter;

import static ru.ratanov.kinoman.model.base.Constants.BASE_URL;
import static ru.ratanov.kinoman.model.base.Constants.BASE_URL_SEARCH;

public class SearchAPI {
    public static final String TAG = "SearchAPI";
    public static final String KEY_YEAR = "Год выпуска";
    public static final String KEY_GENRE = "Жанр";
    public static final String KEY_PRODUCTION = "Выпущено";

    private SearchPresenter mSearchPresenter;
    private List<SearchItem> mSearchItems = new ArrayList<>();

    private HashMap<String, String> mHashMap = new HashMap<>();

    public SearchAPI(SearchPresenter searchPresenter) {
        mSearchPresenter = searchPresenter;
    }

    public void search(String query) {
        new SearchTask().execute(query);
    }

    private class SearchTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            LinkedHashMap<String, String> linksMap = new LinkedHashMap<>();

            String url = Uri.parse(BASE_URL_SEARCH)
                    .buildUpon()
                    .appendQueryParameter("s", strings[0])
                    .appendQueryParameter("t", "1")
                    .build()
                    .toString();

            try {
                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("tr.bg");

                for (Element element : elements) {
                    String fullTitle = element.select("td.nam").select("a").text().split(" / ")[0];
                    String category = element.select("img").attr("src").split("/")[3].replace(".gif", "");
                    String title;
                    if (category.equals("45") || category.equals("46")) {
                        title = SearchItem.getTitleWithoutSeries(fullTitle);
                    } else {
                        title = fullTitle;
                    }
                    String link = BASE_URL + element.select("td.nam").select("a").attr("href");

                    if (SearchItem.isValid(category) && !linksMap.containsKey(title)) {
                        linksMap.put(title, link);
                    }
                }

                for (Map.Entry<String, String> pair : linksMap.entrySet()) {
                    Log.d(TAG, "doInBackground: " + pair.getKey() + " - " + pair.getValue());

                    doc = Jsoup.connect(pair.getValue()).get();
                    fillHashMap(doc.select("h2").html());
                    String rating = "";
                    elements = doc.select("a[href]");

                    for (Element element : elements) {
                        if (element.attr("href").contains("kinopoisk")) {
                            rating = element.text().substring(9);
                            break;
                        }
                    }

                    String production = mHashMap.get(KEY_PRODUCTION);
                    String country;
                    int isOneCountry = production.indexOf(",");
                    if (isOneCountry != -1) {
                        country = production.substring(0, production.indexOf(","));
                    } else {
                        country = production;
                    }

                    SearchItem item = new SearchItem();

                    item.setTitle(pair.getKey());
                    item.setLink(pair.getValue());
                    item.setPosterUrl(doc.select("img.p200").attr("src"));
                    item.setYear(mHashMap.get(KEY_YEAR));
                    item.setGenre(mHashMap.get(KEY_GENRE));
                    item.setRating(rating);
                    item.setCountry(country);

                    mSearchItems.add(item);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute: " + mSearchItems.size());
            mSearchPresenter.updatePage(mSearchItems);
        }
    }

    private void fillHashMap(String html) {
        mHashMap.clear();
        Document doc = Jsoup.parse(html);
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
        doc.select("br").append("_break_");
        String string = Jsoup.clean(doc.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        String[] block = string.split("_break_");
        for (int i = 0; i < block.length; i++) {
            block[i] = block[i].trim();
            String key = block[i].split(": ")[0];
            String value = block[i].substring(block[i].indexOf(": ") + 1);
            mHashMap.put(key, value);
        }
    }
}
