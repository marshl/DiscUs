package com.marshl.discus;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends Activity {

    private TextView txtQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // get the action bar
        //ActionBar actionBar = getActionBar();

        // Enabling Back navigation on Action Bar icon
        //actionBar.setDisplayHomeAsUpEnabled(true);

        txtQuery = (TextView) findViewById(R.id.txtQuery);
        Log.d("foo", "bar");
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            /**
             * Use this query to display search results like
             * 1. Getting the data from SQLite and showing in listview
             * 2. Making webrequest and displaying the data
             * For now we just display the query only
             */
            txtQuery.setText("Search query: " + query);

            getResultTask task = new getResultTask();
            task.execute(query);
        }

    }

    private class getResultTask extends AsyncTask<String, Integer, List<Title>> {
        private Exception exception;

        protected List<Title> doInBackground(String... query) {

            Log.d("SearchResultsActivity", "Connecting...");
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=lost");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                //readStream(stream);
                return this.readResultStream(stream);
            } catch (MalformedURLException ex) {
                Log.e("SearchResultsActivity", ex.toString());
                this.exception = ex;
                return null;
            } catch (IOException ex) {
                Log.e("SearchResultsActivty", ex.toString());
                this.exception = ex;
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        protected void onProgressUpdate(Integer... progress) {

        }


        protected void onPostExecute(List<Title> result) {

        }

        private List<Title> readResultStream(InputStream stream) throws IOException {
            InputStreamReader reader = new InputStreamReader(stream, "utf-8");
            JsonReader jsonReader = new JsonReader(reader);

            List<Title> titleList = new ArrayList<>();

            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                Log.d("Name", name);
                if (name.equals("title_popular") || name.equals("title_exact")) {
                    jsonReader.beginArray();

                    while (jsonReader.hasNext()) {
                        Log.d("Starting", "Title");
                        Title title = parseTitle(jsonReader);
                        titleList.add(title);
                        Log.d("Title", title.name);
                    }

                    jsonReader.endArray();
                }
            }

            jsonReader.endObject();
            for (Title title : titleList) {
                Log.d("TEST", title.name);
            }
            return titleList;
        }
    }

    private static Title parseTitle(JsonReader jsonReader) throws IOException {
        Title t = new Title();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String titleName = jsonReader.nextName();
            String stringValue = jsonReader.nextString();
            Log.d(titleName, stringValue);
            if ("id".equals(titleName)) {
                t.id = stringValue;
            } else if ("title".equals(titleName)) {
                t.title = stringValue;
            } else if ("name".equals(titleName)) {
                t.name = stringValue;
            } else if ("title_description".equals(titleName)) {
                t.title_description = stringValue;
            } else if ("episode_title".equals(titleName)) {
                t.episode_title = stringValue;
            } else if ("description".equals(titleName)) {
                t.description = stringValue;
            }
        }

        jsonReader.endObject();
        return t;
    }

}
