package ru.ratanov.kinoman.model.parsers;

import android.net.Uri;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.ratanov.kinoman.model.content.Film;
import ru.ratanov.kinoman.model.content.SameItem;
import ru.ratanov.kinoman.model.content.TopItem;
import ru.ratanov.kinoman.presentation.presenter.detail.DetailPresenter;
import ru.ratanov.kinoman.presentation.presenter.detail.SamePresenter;
import ru.ratanov.kinoman.presentation.presenter.main.TopPresenter;

import static ru.ratanov.kinoman.model.base.Constants.BASE_URL;
import static ru.ratanov.kinoman.model.base.Constants.BASE_URL_TOP;
import static ru.ratanov.kinoman.model.base.Constants.KINOPOISK_URL;

/**
 * Created by ACER on 27.11.2016.
 */

public class FilmParser {

    private TopPresenter mTopPresenter;
    private DetailPresenter mDetailPresenter;
    private SamePresenter mSamePresenter;

    public FilmParser() {

    }

    public FilmParser(TopPresenter topPresenter) {
        mTopPresenter = topPresenter;
    }

    public FilmParser(DetailPresenter detailPresenter) {
        mDetailPresenter = detailPresenter;
    }

    public FilmParser(SamePresenter samePresenter) {
        mSamePresenter = samePresenter;
    }

    // Public Methods

    public void getTopFilms(String category) {
        new GetTopTask().execute(category);
    }

    public boolean isFilmBlocked(String url) {
        try {
            return new IsFilmBlockedTask().execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void getFilm(String url) {
        new GetFilmTask().execute(url);
    }

    public void getTrailerUrl(String kpUrl) {
        new GetTrailerUrl().execute(kpUrl);
    }

    public void getSameFilms(String searchUrl) {
        new GetSameFilms().execute(searchUrl);
    }


    // private AsyncTasks according to methods

    private class GetTopTask extends AsyncTask<String, Void, List<TopItem>> {

        String category;

        @Override
        protected List<TopItem> doInBackground(String... params) {

            category = params[0];

            List<TopItem> items = new ArrayList<>();
            String url = Uri.parse(BASE_URL_TOP)
                    .buildUpon()
                    .appendQueryParameter("t", params[0])
                    .build()
                    .toString();

            try {
                Document doc = Jsoup.connect(url).get();
                if (doc != null) {
                    Elements elements = doc.select("div.bx1").select("a");
                    for (Element entry : elements) {
                        String link = BASE_URL + entry.select("a").attr("href");
                        String title = entry.select("a").attr("title");
                        String pictureUrl = entry.select("a").select("img").attr("src");

                        TopItem item = new TopItem();
                        item.setLink(link);
                        item.setTitle(title);
                        item.setPictureUrl(pictureUrl);

                        items.add(item);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<TopItem> topItems) {
            mTopPresenter.onLoadComplete(topItems);
        }
    }

    private class IsFilmBlockedTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                Document document = Jsoup.connect(urls[0]).get();
                String pageTitle = document.select("title").text();
                if (pageTitle.equals("Доступ закрыт")) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private class GetFilmTask extends AsyncTask<String, Void, Film> {

        HashMap<String, String> hashMap = new HashMap<>();

        static final String KEY_TITLE = "Название";
        static final String KEY_ORIGINAL_TITLE = "Оригинальное название";
        static final String KEY_YEAR = "Год выпуска";
        static final String KEY_GENRE = "Жанр";
        static final String KEY_PRODUCTION = "Выпущено";
        static final String KEY_DIRECTOR = "Режиссер";
        static final String KEY_CAST = "В ролях";
        static final String KEY_QUALITY = "Качество";
        static final String KEY_VIDEO = "Видео";
        static final String KEY_AUDIO = "Аудио";
        static final String KEY_SIZE = "Размер";
        static final String KEY_LENGTH = "Продолжительность";
        static final String KEY_TRANSLATE = "Перевод";
        static final String KEY_CATEGORY = "Категория";

        @Override
        protected Film doInBackground(String... strings) {

            try {
                Document doc = Jsoup.connect(strings[0]).get();
                fillHashMap(doc.select("h2").html());
                fillHashMap(doc.select("div.justify.mn2.pad5x5").html());

                Film film = new Film();

                film.setTitle(doc.select("h1").text());
                film.setPosterUrl(doc.select("img.p200").attr("src"));
                film.setQuality(getPair(KEY_QUALITY));
                film.setVideo(getPair(KEY_VIDEO));
                film.setAudio(getPair(KEY_AUDIO));
                film.setSize(getPair(KEY_SIZE));
                film.setLength(getPair(KEY_LENGTH));
                film.setTranslate((hashMap.get(KEY_TRANSLATE) == null) ? KEY_TRANSLATE + ": Не требуется" : getPair(KEY_TRANSLATE));
                film.setYear(getPair(KEY_YEAR));
                film.setGenre(getPair(KEY_GENRE));

                Elements elements = doc.select("ul.men.w200").select("li");
                for (Element element : elements) {
                    if (element.text().contains("Раздают")) {
                        film.setSeeds(element.select("span").text());
                    }
                    if (element.text().contains("Обновлен")) {
                        film.setDateTitle("Обновлен");
                        film.setDate(element.select("span").text());
                    }
                    if (element.text().contains("Залит")) {
                        film.setDateTitle("Залит");
                        film.setDate(element.select("span").text());
                    }
                    if (element.text().contains("Кинопоиск")) {
                        film.setRating(element.text().substring(9));
                        film.setKpUrl(element.select("a").attr("href"));
                    }
                }

                film.setDescription(doc.select("div.bx1.justify").select("p").text());
                film.setSameLink(BASE_URL + doc.select("td.w90p").select("a").attr("href"));

                return film;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Film film) {
            if (film != null) {
                mDetailPresenter.updatePage(film);
            }
        }

        private void fillHashMap(String html) {
            Document doc = Jsoup.parse(html);
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
            doc.select("br").append("_break_");
            String string = Jsoup.clean(doc.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
            String[] block = string.split("_break_");
            for (int i = 0; i < block.length; i++) {
                block[i] = block[i].trim();
                String[] pairs = block[i].split(": ");
                hashMap.put(pairs[0], pairs[1]);
            }
        }

        private String getPair(String key) {
            return key + ": " + hashMap.get(key);
        }
    }

    private class GetTrailerUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0] + "/video").get();
                Elements links = document.select("a.all");

                String trailerPage = KINOPOISK_URL + links.first().attr("href");
                document = Jsoup.connect(trailerPage).get();
                Elements videos = document.select("a.continue");
                for (Element video : videos) {
                    if (video.text().contains("Высокое качество")) {
                        int start = video.attr("href").indexOf("http");
                        return video.attr("href").substring(start);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String trailerUrl) {
            mDetailPresenter.showTrailer(trailerUrl);
        }
    }

    private class GetSameFilms extends AsyncTask<String, Void, List<SameItem>> {

        List<SameItem> items = new ArrayList<>();

        @Override
        protected List<SameItem> doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect(strings[0]).get();
                Elements elements = doc.select("tr.bg");
                for (Element row : elements) {
                    SameItem sameItem = new SameItem();

                    sameItem.setTitle(row.select("td.nam").text());
                    sameItem.setSize(row.select("td").get(3).text());
                    sameItem.setSeeds(row.select("td").get(4).text());
                    sameItem.setDate(row.select("td").get(6).text());
                    sameItem.setPageUrl(BASE_URL + row.select("td.nam").select("a").attr("href"));

                    items.add(sameItem);
                }
                return items;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<SameItem> sameItems) {
            mSamePresenter.showSameFilms(sameItems);
        }
    }
}
