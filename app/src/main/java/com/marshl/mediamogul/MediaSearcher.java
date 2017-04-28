package com.marshl.mediamogul;

import android.app.Activity;
import android.util.JsonReader;
import android.util.Log;

import com.marshl.util.Connectivity;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import static com.marshl.mediamogul.SearchParameters.SearchType.NOT_USER_OWNED;
import static com.marshl.mediamogul.SearchParameters.SearchType.USER_OWNED;

public class MediaSearcher {
    private static final String URL_ENCODING = "utf-8";
    private SearchParameters searchParams;
    private Activity context;
    private int resultsFoundSoFar = 0;
    private int totalResultCount = 0;
    private int currentSearchPage = 1;

    public MediaSearcher(SearchParameters params, Activity context) {
        this.searchParams = params;
        this.context = context;
    }

    public boolean hasMoreResults() {
        return resultsFoundSoFar < totalResultCount;
    }

    public ArrayList<Media> runSearch() throws MediaSearchException {

        ArrayList<Media> results;

        switch (this.searchParams.getSearchType()) {
            case BOTH:
            case NOT_USER_OWNED:
                results = this.runApiSearch();
                this.resultsFoundSoFar += results.size();
                this.currentSearchPage += 1;
                break;
            case USER_OWNED:
            case ON_WISHLIST:
                results = this.runDatabaseSearch();
                this.resultsFoundSoFar = this.totalResultCount = results.size();
                break;
            default:
                throw new IllegalStateException("Unknown search type " + this.searchParams.getSearchType());
        }

        MediaReaderDbHelper dbHelper = new MediaReaderDbHelper(this.context);

        for (Iterator<Media> iterator = results.iterator(); iterator.hasNext(); ) {
            Media media = iterator.next();
            media.setOwnershipStatus(dbHelper.getMediaOwnershipStatus(media.getImdbId()));

            if (this.searchParams.getSearchType() == NOT_USER_OWNED && media.getOwnershipStatus() == Media.OWNERSHIP_OWNED) {
                iterator.remove();
            } else if (this.searchParams.getSearchType() == USER_OWNED && media.getOwnershipStatus() != Media.OWNERSHIP_OWNED) {
                iterator.remove();
            }
        }

        return results;
    }

    private ArrayList<Media> runApiSearch() throws MediaSearchException {
        Log.d("SearchResultsActivity", "Connecting...");
        HttpURLConnection urlConnection = null;
        try {
            String encodedQuery = "http://www.omdbapi.com/?plot=full&r=json&s=" + URLEncoder.encode(this.searchParams.getSearchText(), URL_ENCODING) + "&page=" + this.currentSearchPage;
            Log.d("MediaSearcher", "Url is: " + encodedQuery);
            URL url = new URL(encodedQuery);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            return this.readResultStream(stream);
        } catch (IOException | ParseException ex) {
            throw new MediaSearchException(ex, ex.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private ArrayList<Media> runDatabaseSearch() {
        MediaReaderDbHelper dbHelper = new MediaReaderDbHelper(this.context);
        ArrayList<Media> results = dbHelper.runMediaSearch(this.searchParams);
        return results;
    }

    public Media lookupMediaWithId(String imdbId) throws MediaSearchException {
        Log.d("MediaSearcher", "Looking up media with id " + imdbId);

        MediaReaderDbHelper dbHelper = new MediaReaderDbHelper(this.context);
        Media localResult = dbHelper.getMediaDetails(imdbId);

        if (Connectivity.isConnected(this.context)) {
            HttpURLConnection urlConnection;
            try {
                String encodedQuery = "http://www.omdbapi.com/?i=" + URLEncoder.encode(imdbId, URL_ENCODING);
                Log.d("MediaSearcher", "running query " + encodedQuery);
                URL url = new URL(encodedQuery);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                Media apiResult = this.readLookupResultStream(stream);
                if (localResult != null) {
                    apiResult.setOwnershipStatus(localResult.getOwnershipStatus());
                } else {
                    apiResult.setOwnershipStatus(Media.OWNERSHIP_NOT_OWNED);
                }
                return apiResult;
            } catch (IOException | ParseException ex) {
                Log.e("lookupMediaWithId", ex.toString());
                throw new MediaSearchException(ex, ex.toString());
            }
        } else {
            return localResult;
        }
    }

    private ArrayList<Media> readResultStream(InputStream stream) throws IOException, MediaSearchException, ParseException {
        InputStreamReader reader = new InputStreamReader(stream, URL_ENCODING);
        JsonReader jsonReader = new JsonReader(reader);

        ArrayList<Media> mediaList = new ArrayList<>();
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
                    this.totalResultCount = Integer.parseInt(jsonReader.nextString());
                    break;
                case "Response":
                    String response = jsonReader.nextString();
                    Log.d("MediaSearcher", response);
                    break;
                case "Error":
                    String errorMessage = jsonReader.nextString();

                    // Ignore the "no movie found" error
                    if (!errorMessage.equals("Movie not found!")) {
                        throw new MediaSearchException(errorMessage);
                    }
                    break;

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

            switch (nameValue) {
                case "Title": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.TITLE_KEY, stringValue);
                    break;
                }
                case "Year": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(media.YEAR_KEY, stringValue);
                    break;
                }
                case "Rated": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    if (!stringValue.equals("N/A")) {
                        media.setValue(Media.CONTENT_RATING_KEY, stringValue);
                    }
                    break;
                }
                case "Released": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.RELEASE_DATE_KEY, stringValue);
                    break;
                }
                case "Runtime": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    if (stringValue.length() == 0 || !stringValue.contains(" ")) {
                        break;
                    }
                    String minutes = stringValue.substring(0, stringValue.indexOf(' '));
                    media.setValue(Media.RUNTIME_KEY, minutes);
                    break;
                }
                case "Genre": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.GENRES_KEY, stringValue);
                    break;
                }
                case "Director": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.DIRECTOR_KEY, stringValue);
                    break;
                }
                case "Writer": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.WRITER_KEY, stringValue);
                    break;
                }
                case "Actors": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.ACTOR_KEY, stringValue);
                    break;
                }
                case "Plot": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.PLOT_KEY, stringValue);
                    break;
                }
                case "Language": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.LANGUAGE_KEY, stringValue);
                    break;
                }
                case "Country": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.COUNTRY_KEY, stringValue);
                    break;
                }
                case "Awards": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.AWARDS_KEY, stringValue);
                    break;
                }
                case "Poster": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.POSTER_URL_KEY, stringValue);
                    break;
                }
                case "Metascore": {
                    String stringValue = jsonReader.nextString();
                    if (stringValue.equals("N/A")) {
                        stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                        media.setValue(Media.METASCORE_KEY, stringValue);
                    }
                    break;
                }
                case "imdbRating": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.IMDB_RATING_KEY, stringValue);
                    break;
                }
                case "imdbVotes": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    stringValue = stringValue.replace(",", "");
                    media.setValue(Media.IMDB_VOTES_KEY, stringValue);
                    break;
                }
                case "imdbID": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setImdbId(stringValue);
                    break;
                }
                case "totalSeasons": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.TOTAL_SEASONS_KEY, stringValue);
                    break;
                }
                case "Type": {
                    String stringValue = jsonReader.nextString();
                    stringValue = StringEscapeUtils.unescapeHtml4(stringValue);
                    media.setValue(Media.TYPE_KEY, stringValue);
                    break;
                }
                case "Ratings": {
                    this.parseRatingList(jsonReader, media);
                    break;
                }
                case "Response":
                    jsonReader.nextString();
                    break;
                default:
                    Log.d("MediaSearcher", "Unknown element " + nameValue);
                    jsonReader.skipValue();
                    break;
            }
        }

        jsonReader.endObject();
        return media;
    }

    void parseRatingList(JsonReader jsonReader, Media media) throws IOException {
        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
                String sourceKey = jsonReader.nextName();
                String source = jsonReader.nextString();

                String valueKey = jsonReader.nextName();
                String value = jsonReader.nextString();

                switch (source) {
                    case "Internet Movie Database": {
                        String ratingValue = value.substring(0, value.indexOf('/'));
                        media.setValue(Media.IMDB_RATING_KEY, ratingValue);
                        break;
                    }
                    case "Rotten Tomatoes": {
                        //TODO: Support Rotten Tomatoes
                        break;
                    }
                    case "Metacritic": {
                        String ratingValue = value.substring(0, value.indexOf('/'));
                        media.setValue(Media.METASCORE_KEY, ratingValue);
                        break;
                    }
                }
            }

            jsonReader.endObject();
        }

        jsonReader.endArray();
    }

}
