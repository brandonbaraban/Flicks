package com.codepath.flicks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.codepath.flicks.MovieListActivity.API_BASE_URL;
import static com.codepath.flicks.MovieListActivity.API_KEY_PARAM;

public class MovieDetailsActivity extends AppCompatActivity {

    // tag for logging from this activity
    public final static String TAG = "MovieDetailsActivity";

    // the movie to display
    Movie movie;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivTrailer;
    TextView tvReleaseDate;

    // client for video id request
    AsyncHttpClient client;

    String videoKey;
    String releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // initialize the client
        client = new AsyncHttpClient();

        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        ivTrailer = (ImageView) findViewById(R.id.ivTrailer);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s'", movie.getTitle()));

        // load image using glide
        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; // crop margin, set to 0 for corners with no crop
        Context context = ivTrailer.getContext();
        Glide.with(context)
                .load(movie.getTrailerImageUrl())
                //.placeholder(R.drawable.flicks_backdrop_placeholder)
                //.error(R.drawable.flicks_backdrop_placeholder)
                .into(ivTrailer);

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        getReleaseDate();

        // set on click listener for trailer
        ivTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MovieDetailsActivity.this, "Loading movie trailer", Toast.LENGTH_SHORT).show();
                getVideoKey();
            }
        });
    }

    // get the video key of movie
    private void getVideoKey() {
        // create the url object
        String url = API_BASE_URL + "/movie/" + movie.getId() + "/videos";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        // execute GET request expecting JSON object in response
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // load the results into the videoKey
                    JSONArray results = response.getJSONArray("results");
                    videoKey = results.getJSONObject(0).getString("key");
                    Log.i(TAG, String.format("Loaded %s video key(s)", results.length()));
                    Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                    intent.putExtra("video_key", videoKey);
                    MovieDetailsActivity.this.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Failed to fetch video key");
            }
        });
    }

    // get the release date of the movie
    // get the video key of movie
    private void getReleaseDate() {
        // create the url object
        String url = API_BASE_URL + "/movie/" + movie.getId() + "/release_dates";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        // execute GET request expecting JSON object in response
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // load the results into the releaseDate
                    JSONArray results = response.getJSONArray("results");
                    releaseDate = results.getJSONObject(0).getJSONArray("release_dates").getJSONObject(0).getString("release_date").substring(0, 10);
                    Log.i(TAG, String.format("Loaded %s video key(s)", results.length()));
                    tvReleaseDate.setText("Release date: " + releaseDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Failed to fetch release date");
            }
        });
    }
}
