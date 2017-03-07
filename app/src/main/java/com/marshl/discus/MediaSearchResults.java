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

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class MediaSearchResults extends AppCompatActivity {

    private ArrayList<Media> mediaList;
    private ProgressDialog progressDialog;
    private MediaSearcher mediaSearcher;
    private MediaResultAdapter resultAdapter;
    private ListView resultView;

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

        this.mediaList = new ArrayList<>();
        Intent intent = this.getIntent();
        final SearchParameters params = intent.getParcelableExtra(SearchParameters.SEARCH_PARAM_PARCEL_NAME);
        this.mediaSearcher = new MediaSearcher(params, MediaSearchResults.this);

        this.resultAdapter = new MediaResultAdapter(MediaSearchResults.this, this.mediaList);
        this.resultAdapter.setMediaSearcher(this.mediaSearcher);

        this.resultView = (ListView) this.findViewById(R.id.media_result_list);

        this.resultView.setAdapter(this.resultAdapter);

        this.resultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                progressDialog.show();

                Media media = (Media) parent.getAdapter().getItem(position);
                if (media != null) {
                    MediaSearchResults.this.openMediaDetails(media);
                } else {
                    MediaSearchTask searchTask = new MediaSearchTask();
                    searchTask.execute(params);
                }
            }
        });

        MediaSearchTask searchTask = new MediaSearchTask();
        searchTask.execute(params);
    }

    private void openMediaDetails(Media media) {

        MediaLookupTask task = new MediaLookupTask();
        task.execute(media.getImdbId());
    }


    public class MediaSearchTask extends AsyncTask<SearchParameters, Integer, ArrayList<Media>> {
        private Exception exception;

        @Override
        protected ArrayList<Media> doInBackground(SearchParameters... params) {

            ArrayList<Media> mediaList;

            try {
                mediaList = MediaSearchResults.this.mediaSearcher.runSearch();
            } catch (Exception ex) {
                this.exception = ex;
                mediaList = null;
                throw new RuntimeException(ex);
            }

            return mediaList;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(ArrayList<Media> result) {

            if (this.exception != null) {
                Toast toast = Toast.makeText(MediaSearchResults.this,
                        "An error occurred when retrieving the results: " + exception.getMessage(),
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                final ArrayList<Media> r = result;
                MediaSearchResults.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Media m : r) {
                            MediaSearchResults.this.resultAdapter.add(m);
                        }

                        MediaSearchResults.this.resultAdapter.notifyDataSetChanged();
                        MediaSearchResults.this.resultView.requestLayout();
                        MediaSearchResults.this.resultView.invalidateViews();
                    }
                });
            }
            progressDialog.dismiss();
            super.onPostExecute(result);
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
                throw new RuntimeException(ex);
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

            progressDialog.dismiss();

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

            super.onPostExecute(result);
        }

    }
}
