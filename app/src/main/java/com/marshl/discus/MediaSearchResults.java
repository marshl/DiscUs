package com.marshl.discus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MediaSearchResults extends AppCompatActivity {

    private List<Media> mediaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search_results);

        if(this.getActionBar() != null) {
            this.getActionBar().setTitle("Results");
        }

        Intent intent = this.getIntent();
        SearchParameters params = intent.getParcelableExtra("params");
        MediaSearchTask task = new MediaSearchTask();
        task.execute(params.getSearchText());

        ListView resultList = (ListView)this.findViewById(R.id.media_result_list);
        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Media media = (Media)parent.getAdapter().getItem(position);
                MediaSearchResults.this.openMediaDetails(media);
            }
        });
    }

    private void openMediaDetails(Media media){

        MediaLookupTask task = new MediaLookupTask();
        task.execute(media.getImdbId());
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

    public class MediaLookupTask extends AsyncTask<String, Integer, Media> {
        private Exception exception;
        private Media media;

        @Override
        protected Media doInBackground(String... imdbId) {

            try {
                MediaSearch searcher = new MediaSearch(null);
                this.media = searcher.lookupMediaWithId(imdbId[0]);
            } catch (Exception ex) {
                this.exception = ex;
                this.media = null;
            }

            Log.d("MediaSearchResults", "omg");
            return this.media;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Media result) {

            Log.d("MediaSearchResults", "It is done");
            super.onPostExecute(result);
            if (this.exception != null) {
                Toast toast = Toast.makeText(MediaSearchResults.this,
                        "An error occurred when retrieving the results: " + exception.getMessage(),
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                Intent intent = new Intent(MediaSearchResults.this, MediaDetailActivity.class);
                intent.putExtra("media", new ParcelableMedia(media));
                startActivity(intent);
            }
        }

    }
}
