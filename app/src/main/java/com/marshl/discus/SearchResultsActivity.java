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
            txtQuery.setText("Search Query: " + query);

            new getResultTask().execute(query);
        }

    }

    private class getResultTask extends AsyncTask<String, Integer, List<Title>> {
        private Exception exception;

        protected List<Title> doInBackground(String... query) {

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=lost");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                //readStream(stream);
                return this.readResultStream(stream);
            } catch (MalformedURLException ex) {
                Log.e("SearchResultsActivity", ex.toString());
                return null;
            } catch (IOException ex) {
                Log.e("SearchResultsActivty", ex.toString());
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
                if (name.equals("title_popular") || name.equals("title_exact")) {
                    jsonReader.beginArray();

                    while (jsonReader.hasNext()) {
                        Title title = parseTitle(jsonReader);
                        titleList.add(title);
                    }

                    jsonReader.endArray();
                }
            }

            jsonReader.endObject();
            return titleList;
        }

        private Title parseTitle(JsonReader jsonReader) throws IOException
        {
            Title title = new Title();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String titleName = jsonReader.nextName();
                switch(titleName){
                    case "id":
                        title.id = jsonReader.nextString();
                        break;
                    case "title":
                        title.title = jsonReader.nextString();
                        break;
                    case "name":
                        title.name = jsonReader.nextString();
                        break;
                    case "title_description":
                        title.title_description = jsonReader.nextString();
                        break;
                    case "episode_title":
                        title.episode_title = jsonReader.nextString();
                        break;
                    case "description":
                        title.description = jsonReader.nextString();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown name " + titleName);
                }
            }
            jsonReader.endObject();
            return title;
        }

    }

    private class Title {
        public String id;
        public String title;
        public String name;
        public String title_description;
        public String episode_title;
        public String description;
    }

}
