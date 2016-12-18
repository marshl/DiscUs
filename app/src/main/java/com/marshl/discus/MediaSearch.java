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
import java.net.URLEncoder;
import java.net.URLDecoder;

public class MediaSearch {
    private String queryString;

    public MediaSearch(String query) {
        this.queryString = query;
    }

    public List<Media> runSearch() throws IOException {

        Log.d("SearchResultsActivity", "Connecting...");
        HttpURLConnection urlConnection = null;
        try {
            String encodedQuery = "http://www.imdb.com/xml/find?json=1&nr=1&tt=on&i=on&q=" + URLEncoder.encode(this.queryString, "utf-8");
            Log.d("MediaSearch", "Url is: " + encodedQuery);
            URL url = new URL(encodedQuery);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            return this.readResultStream(stream);
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
            //Log.d("Name", name);
            //if (name.equals("title_popular") || name.equals("title_exact") || name.equals("title_substring"))
            {
                jsonReader.beginArray();

                while (jsonReader.hasNext()) {
                    //Log.d("Starting", "Title");
                    Media media = parseTitle(jsonReader);
                    mediaList.add(media);
                    //Log.d("Title", media.name);
                }

                jsonReader.endArray();
            }
        }

        jsonReader.endObject();
        return mediaList;
    }

    private static Media parseTitle(JsonReader jsonReader) throws IOException {
        Media media = new Media();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String titleName = jsonReader.nextName();
            String stringValue = jsonReader.nextString();
            stringValue = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(stringValue);
            //Log.d(titleName, stringValue);
            if ("id".equals(titleName)) {
                media.setId(stringValue);
            } else if ("title".equals(titleName)) {
                media.setTitle(stringValue);
            } else if ("name".equals(titleName)) {
                media.setName(stringValue);
            } else if ("title_description".equals(titleName)) {
                // title_description is a duplicate of description, do nothing
            } else if ("episode_title".equals(titleName)) {
                media.setEpisodeTitle(stringValue);
            } else if ("description".equals(titleName)) {
                media.setDescription(stringValue);
            }
        }

        jsonReader.endObject();
        return media;
    }

}
