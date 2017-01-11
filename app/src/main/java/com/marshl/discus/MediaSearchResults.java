package com.marshl.discus;

import android.app.SearchManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MediaSearchResults extends AppCompatActivity {

    private List<Media> mediaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search_results);

        String query = "foo";
        MediaSearchTask task = new MediaSearchTask();
        task.execute(query);
    }

    public class MediaSearchTask extends AsyncTask<String, Integer, List<Media>> {
        private Exception exception;
        private List<Media> mediaList;

        @Override
        protected List<Media> doInBackground(String... query) {

            try {
                MediaSearch mediaSearch = new MediaSearch(query[0]);
                this.mediaList = mediaSearch.runSearch();
            } catch (Exception ex) {
                this.exception = ex;
                this.mediaList = null;
            }

            return this.mediaList;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(List<Media> result) {

            if (this.exception != null) {
                Toast toast = Toast.makeText(MediaSearchResults.this,
                        "An error occurred when retrieving the results: " + exception.getMessage(),
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                ListView resultView = (ListView) findViewById(R.id.media_result_list);
                resultView.setAdapter(new MediaResultAdapter(MediaSearchResults.this, result));
            }
            super.onPostExecute(result);
        }

    }
}
