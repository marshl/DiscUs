package com.marshl.discus;

import android.app.Activity;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.marshl.discus.SearchParameters.SearchType.NOT_USER_OWNED;
import static com.marshl.discus.SearchParameters.SearchType.USER_OWNED;
import static java.lang.Integer.parseInt;

public class MediaSearcher {
    private static final String URL_ENCODING = "utf-8";
    private static final int API_RESULT_PAGE_LENGTH = 10;
    private SearchParameters searchParams;
    private Activity context;
    private List<Media> mediaList;
    private Integer resultCount;
    private Integer lastFetchedPage;

    public MediaSearcher(SearchParameters params, Activity context) {
        this.searchParams = params;
        this.context = context;
    }

    public int getResultCount() {
        if (this.resultCount == null) {
            throw new IllegalStateException("Cannot get result count until search has been performed");
        }

        return this.resultCount;
    }

    public Media getMedia(int position) throws MediaSearchException {
        if (this.resultCount == null) {
            throw new IllegalStateException("Cannot retrieve media until search has been performed");
        }

        if (position > this.resultCount) {
            throw new IllegalArgumentException("Media position is too high: position=" + position + " resultCount=" + this.resultCount);
        }

        if (this.searchParams.getSearchType() == SearchParameters.SearchType.USER_OWNED) {
            throw new IllegalStateException("Cannot find more results of a database-only search");
        }

        while (position >= this.mediaList.size()) {
            this.runPagedSearch(this.lastFetchedPage + 1);
        }

        return this.mediaList.get(position);
    }

    public void runSearch() throws MediaSearchException {
        switch (this.searchParams.getSearchType()) {
            case BOTH:
            case NOT_USER_OWNED:
                this.runApiSearch();
                break;
            case USER_OWNED:
                this.runDatabaseSearch();
                break;
            default:
                throw new IllegalStateException("Unknown search type " + this.searchParams.getSearchType());
        }

        MediaReaderDbHelper dbHelper = new MediaReaderDbHelper(this.context);

        for (Iterator<Media> iterator = this.mediaList.iterator(); iterator.hasNext(); ) {
            Media media = iterator.next();
            media.setOwnershipStatus(dbHelper.getMediaOwnershipStatus(media.getImdbId()));

            if (this.searchParams.getSearchType() == NOT_USER_OWNED && media.getOwnershipStatus() == Media.OwnershipType.OWNED) {
                iterator.remove();
            } else if (this.searchParams.getSearchType() == USER_OWNED && media.getOwnershipStatus() != Media.OwnershipType.OWNED) {
                iterator.remove();
            }
        }
    }

    private void runApiSearch() throws MediaSearchException {
        this.mediaList = new ArrayList<>();
        this.runPagedSearch(1);
    }

    private void runPagedSearch(int pageNumber) throws MediaSearchException {

        this.lastFetchedPage = pageNumber;
        HttpURLConnection urlConnection = null;
        try {
            String encodedQuery = "http://www.omdbapi.com/?plot=full&r=json&s=" + URLEncoder.encode(this.searchParams.getSearchText(), URL_ENCODING) + "&page=" + pageNumber;
            Log.d("MediaSearcher", "Url is: " + encodedQuery);
            URL url = new URL(encodedQuery);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            this.readResultStream(stream);
        } catch (IOException | ParseException ex) {
            throw new MediaSearchException(ex, ex.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }


    private void runDatabaseSearch() {
        MediaReaderDbHelper dbHelper = new MediaReaderDbHelper(this.context);
        this.mediaList = dbHelper.runMediaSearch(this.searchParams);
        this.resultCount = this.mediaList.size();
    }

    public Media lookupMediaWithId(String imdbId) throws MediaSearchException {
        Log.d("MediaSearcher", "Looking up media with id " + imdbId);
        HttpURLConnection urlConnection;
        try {
            String encodedQuery = "http://www.omdbapi.com/?i=" + URLEncoder.encode(imdbId, URL_ENCODING);
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

    private void readResultStream(InputStream stream) throws IOException, MediaSearchException, ParseException {
        InputStreamReader reader = new InputStreamReader(stream, URL_ENCODING);
        JsonReader jsonReader = new JsonReader(reader);

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            Log.d("Name", name);

            switch (name) {
                case "Search":
                    jsonReader.beginArray();

                    while (jsonReader.hasNext()) {
                        Media media = this.parseTitle(jsonReader);
                        this.mediaList.add(media);
                    }

                    jsonReader.endArray();
                    break;
                case "totalResults":
                    this.resultCount = Integer.parseInt(jsonReader.nextString());
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
                    if (!stringValue.equals("N/A")) {
                        media.setContentRating(stringValue);
                    }
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
                    media.setDurationMinutes(parseInt(minutes));
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
                        int metascore = parseInt(stringValue);
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
                        int voteCount = parseInt(stringValue.replace(",", ""));
                        media.setImdbVotes(voteCount);
                    } catch (NumberFormatException ex) {
                        media.setImdbVotes(null);
                    }
                    break;
                case "imdbID":
                    media.setImdbId(stringValue);
                    break;
                case "totalSeasons":
                    try {
                        int voteCount = parseInt(stringValue);
                        media.setTotalSeasons(voteCount);
                    } catch (NumberFormatException ex) {
                        media.setTotalSeasons(null);
                    }
                    break;
                case "Type":
                    media.setType(stringValue);
                    break;
                case "Response":
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown element value " + nameValue + ": " + stringValue);
            }
        }

        jsonReader.endObject();
        return media;
    }
}
