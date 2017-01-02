package com.marshl.discus;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringEscapeUtils;

public class MediaSearch {
    private String queryString;

    public MediaSearch(String query) {
        this.queryString = query;
    }

    public List<Media> runSearch() throws IOException {

        Log.d("SearchResultsActivity", "Connecting...");
        HttpURLConnection urlConnection = null;
        try {
            String encodedQuery = "http://www.omdbapi.com/?plot=full&r=json&s=" + URLEncoder.encode(this.queryString, "utf-8");
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
            Log.d("Name", name);

            switch(name)
            {
                case "Search":

                    jsonReader.beginArray();

                    while (jsonReader.hasNext()) {
                        Media media = MediaSearch.parseTitle(jsonReader);
                        mediaList.add(media);
                    }

                    jsonReader.endArray();
                    break;
                case "totalResults":
                    String resultCount = jsonReader.nextString();
                    Log.d("MediaSearch", resultCount);
                    break;
                case "Response":
                    String response = jsonReader.nextString();
                    Log.d("MediaSearch", response);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown element name " + name);
            }
        }

        jsonReader.endObject();
        return mediaList;
    }

    private static Media parseTitle(JsonReader jsonReader) throws IOException {
        Media media = new Media();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String nameValue = jsonReader.nextName();
            String stringValue = jsonReader.nextString();
            stringValue = StringEscapeUtils.unescapeHtml4(stringValue);

            switch(nameValue) {
                case "Title":
                    media.setTitle(stringValue);
                    break;
                case "Year":
                    int year = Integer.parseInt(stringValue);
                    media.setYear(year);
                    break;
                case "imdbID":
                    media.setImdbId(stringValue);
                    break;
                case "Type":
                    media.setType(stringValue);
                    break;
                case "Poster":
                    media.setPosterUrl(stringValue);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown element value " + nameValue + ": " + stringValue);
            }

            Log.d(nameValue, stringValue);
        }

        jsonReader.endObject();
        return media;
    }

}
