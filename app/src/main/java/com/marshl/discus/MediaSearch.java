package com.marshl.discus;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MediaSearch {
    private String queryString;

    public MediaSearch(String query) {
        this.queryString = query;
    }

    public List<Media> runSearch() throws IOException{

        Log.d("SearchResultsActivity", "Connecting...");
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=lost");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            return this.readResultStream(stream);
        } catch (MalformedURLException ex) {
            Log.e("SearchResultsActivity", ex.toString());
            throw ex;
        } catch (IOException ex) {
            Log.e("SearchResultsActivty", ex.toString());
            throw ex;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private List<Media> readResultStream(InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(stream, "utf-8");
        JsonReader jsonReader = new JsonReader(reader);

        List<Media> mediaList = new ArrayList<>();

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            Log.d("Name", name);
            if (name.equals("title_popular") || name.equals("title_exact")) {
                jsonReader.beginArray();

                while (jsonReader.hasNext()) {
                    Log.d("Starting", "Title");
                    Media media = parseTitle(jsonReader);
                    mediaList.add(media);
                    Log.d("Title", media.name);
                }

                jsonReader.endArray();
            }
        }

        jsonReader.endObject();
        for (Media media : mediaList) {
            Log.d("TEST", media.name);
        }
        return mediaList;
    }

    private static Media parseTitle(JsonReader jsonReader) throws IOException {
        Media media = new Media();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String titleName = jsonReader.nextName();
            String stringValue = jsonReader.nextString();
            Log.d(titleName, stringValue);
            if ("id".equals(titleName)) {
                media.id = stringValue;
            } else if ("title".equals(titleName)) {
                media.title = stringValue;
            } else if ("name".equals(titleName)) {
                media.name = stringValue;
            } else if ("title_description".equals(titleName)) {
                media.title_description = stringValue;
            } else if ("episode_title".equals(titleName)) {
                media.episode_title = stringValue;
            } else if ("description".equals(titleName)) {
                media.description = stringValue;
            }
        }

        jsonReader.endObject();
        return media;
    }

}
