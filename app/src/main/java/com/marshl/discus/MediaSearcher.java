package com.marshl.discus;

import android.util.JsonReader;
import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MediaSearcher {
    private String queryString;

    public MediaSearcher(String query) {
        this.queryString = query;
    }

    public List<Media> runSearch() throws MediaSearchException {

        Log.d("SearchResultsActivity", "Connecting...");
        HttpURLConnection urlConnection = null;
        try {
            String encodedQuery = "http://www.omdbapi.com/?plot=full&r=json&s=" + URLEncoder.encode(this.queryString, "utf-8");
            Log.d("MediaSearcher", "Url is: " + encodedQuery);
            URL url = new URL(encodedQuery);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            return this.readResultStream(stream);
        } catch (IOException | ParseException ex) {
            Log.e("runSearch", ex.toString());
            throw new MediaSearchException(ex, ex.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public Media lookupMediaWithId(String imdbId) throws MediaSearchException {
        Log.d("MediaSearcher", "Looking up media with id " + imdbId);
        HttpURLConnection urlConnection;
        try {
            String encodedQuery = "http://www.omdbapi.com/?i=" + URLEncoder.encode(imdbId, "utf-8");
            Log.d("MediaSearcher", "running query " + encodedQuery);
            URL url = new URL(encodedQuery);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            return this.readLookupResultStream(stream);
        } catch (IOException | ParseException ex) {
            Log.e("lookupMediaWithId", ex.toString());
            throw new MediaSearchException(ex, ex.toString());
        }
    }

    private List<Media> readResultStream(InputStream stream) throws IOException, MediaSearchException, ParseException {
        InputStreamReader reader = new InputStreamReader(stream, "utf-8");
        JsonReader jsonReader = new JsonReader(reader);

        List<Media> mediaList = new ArrayList<>();

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            Log.d("Name", name);

            switch (name) {
                case "Search":

                    jsonReader.beginArray();

                    while (jsonReader.hasNext()) {
                        Media media = this.parseTitle(jsonReader);
                        mediaList.add(media);
                    }

                    jsonReader.endArray();
                    break;
                case "totalResults":
                    String resultCount = jsonReader.nextString();
                    Log.d("MediaSearcher", resultCount);
                    break;
                case "Response":
                    String response = jsonReader.nextString();
                    Log.d("MediaSearcher", response);
                    break;
                case "Error":
                    String errorMessage = jsonReader.nextString();
                    throw new MediaSearchException(errorMessage);
                default:
                    throw new UnsupportedOperationException("Unknown element name " + name);
            }
        }

        jsonReader.endObject();
        jsonReader.close();
        return mediaList;
    }


    private Media readLookupResultStream(InputStream stream) throws IOException, MediaSearchException, ParseException {
        InputStreamReader reader = new InputStreamReader(stream, "utf-8");
        JsonReader jsonReader = new JsonReader(reader);

        if (!jsonReader.hasNext()) {
            throw new MediaSearchException("No data was found in the stream");
        }

        Media media = this.parseTitle(jsonReader);
        jsonReader.close();
        return media;
    }

    private Media parseTitle(JsonReader jsonReader) throws IOException {
        Media media = new Media();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String nameValue = jsonReader.nextName();
            String stringValue = jsonReader.nextString();
            Log.d(nameValue, stringValue);
            stringValue = StringEscapeUtils.unescapeHtml4(stringValue);

            switch (nameValue) {
                case "Title":
                    media.setTitle(stringValue);
                    break;
                case "Year":
                    media.setYear(stringValue);
                    break;
                case "Rated":
                    media.setContentRating(stringValue);
                    break;
                case "Released":
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                        media.setReleaseDate(format.parse(stringValue));
                    } catch (ParseException ex) {
                        media.setReleaseDate(null);
                    }
                    break;
                case "Runtime":
                    if (stringValue.length() == 0 || !stringValue.contains(" ")) {
                        media.setDurationMinutes(null);
                        break;
                    }
                    String minutes = stringValue.substring(0, stringValue.indexOf(' '));
                    media.setDurationMinutes(Integer.parseInt(minutes));
                    break;
                case "Genre":
                    media.setGenres(stringValue);
                    break;
                case "Director":
                    media.setDirector(stringValue);
                    break;
                case "Writer":
                    media.setWriter(stringValue);
                    break;
                case "Actors":
                    media.setActors(stringValue);
                    break;
                case "Plot":
                    media.setPlot(stringValue);
                    break;
                case "Language":
                    media.setLanguages(stringValue);
                    break;
                case "Country":
                    media.setCountry(stringValue);
                    break;
                case "Awards":
                    media.setAwards(stringValue);
                    break;
                case "Poster":
                    media.setPosterUrl(stringValue);
                    break;
                case "Metascore":
                    try {
                        int metascore = Integer.parseInt(stringValue);
                        media.setMetascore(metascore);
                    } catch (NumberFormatException ex) {
                        media.setMetascore(0);
                    }
                    break;
                case "imdbRating":
                    try {
                        float rating = Float.parseFloat(stringValue);
                        media.setImdbRating(rating);
                    } catch (NumberFormatException ex) {
                        media.setImdbRating(null);
                    }
                    break;
                case "imdbVotes":
                    try {
                        int voteCount = Integer.parseInt(stringValue.replace(",", ""));
                        media.setImdbVotes(voteCount);
                    } catch (NumberFormatException ex) {
                        media.setImdbVotes(null);
                    }
                    break;
                case "imdbID":
                    media.setImdbId(stringValue);
                    break;
                case "Type":
                    media.setType(stringValue);
                    break;
                case "Response":
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown element value " + nameValue + ": " + stringValue);
            }

            //Log.d(nameValue, stringValue);
        }

        jsonReader.endObject();
        return media;
    }
}
