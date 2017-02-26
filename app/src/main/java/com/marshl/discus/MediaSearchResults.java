package com.marshl.discus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MediaSearchResults extends AppCompatActivity {

    private List<Media> mediaList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search_results);

        if (this.getActionBar() != null) {
            this.getActionBar().setTitle(R.string.title_activity_media_results);
        }

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle(getString(R.string.search_progress_dialog_title));
        this.progressDialog.setMessage(getString(R.string.search_progress_dialog_message));
        this.progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        this.progressDialog.show();

        Intent intent = this.getIntent();
        SearchParameters params = intent.getParcelableExtra(SearchParameters.SEARCH_PARAM_PARCEL_NAME);
        MediaSearchTask task = new MediaSearchTask();
        task.execute(params);

        ListView resultList = (ListView) this.findViewById(R.id.media_result_list);
        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Media media = (Media) parent.getAdapter().getItem(position);
                MediaSearchResults.this.openMediaDetails(media);
            }
        });
    }

    private void openMediaDetails(Media media) {
        MediaLookupTask task = new MediaLookupTask();
        task.execute(media.getImdbId());
    }


    public class MediaSearchTask extends AsyncTask<SearchParameters, Integer, MediaSearcher> {
        private Exception exception;

        @Override
        protected MediaSearcher doInBackground(SearchParameters... params) {

            try {
                MediaSearcher mediaSearcher = new MediaSearcher(params[0], MediaSearchResults.this);
                mediaSearcher.runSearch();
                return mediaSearcher;
            } catch (Exception ex) {
                this.exception = ex;
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(MediaSearcher searcher) {

            if (this.exception != null) {
                Toast toast = Toast.makeText(MediaSearchResults.this,
                        "An error occurred when retrieving the results: " + exception.getMessage(),
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                ListView resultView = (ListView) findViewById(R.id.media_result_list);
                resultView.setAdapter(new MediaResultAdapter(MediaSearchResults.this, searcher));
            }

            progressDialog.dismiss();
            super.onPostExecute(searcher);
        }

    }

    public class MediaLookupTask extends AsyncTask<String, Integer, Media> {
        private Exception exception;
        private Media media;

        @Override
        protected Media doInBackground(String... imdbId) {

            try {
                MediaSearcher searcher = new MediaSearcher(null, MediaSearchResults.this);
                this.media = searcher.lookupMediaWithId(imdbId[0]);
            } catch (Exception ex) {
                this.exception = ex;
                this.media = null;
            }

            return this.media;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Media result) {

            super.onPostExecute(result);
            if (this.exception != null) {
                Toast toast = Toast.makeText(MediaSearchResults.this,
                        "An error occurred when retrieving the results: " + exception.getMessage(),
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                Intent intent = new Intent(MediaSearchResults.this, MediaDetailActivity.class);
                intent.putExtra(ParcelableMedia.MEDIA_PARCEL_NAME, new ParcelableMedia(media));
                startActivity(intent);
            }
        }

    }
}
