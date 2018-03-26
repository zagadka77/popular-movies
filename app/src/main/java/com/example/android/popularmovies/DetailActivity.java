package com.example.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.databinding.ActivityDetailBinding;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Shows the details of a particular movie.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    private ProgressBar progressBar;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView backdropImageIV;

        // Prepare data binding
        ActivityDetailBinding detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Movie movie;

        // Some ideas here are taken from the Sandwich app
        // (https://github.com/udacity/sandwich-club-starter-code)
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            movie = intent.getExtras().getParcelable(EXTRA_MOVIE);
        } else {
            movie = null;
        }

        // If there were any errors retrieving the movie details, close the activity
        if (movie == null) {
            closeOnError();
            return;
        }

        // Bind the data
        detailBinding.setMovie(movie);

        // Change the activity title with the movie's original title
        setTitle(movie.getOriginalTitle());

        // Get the backdrop image complete url
        String backdropRelativePath = movie.getBackdropImageUrl();
        String backdropUrl =
                NetworkUtils.getImageUrl(backdropRelativePath, NetworkUtils.IMAGE_BACKDROP)
                        .toString();

        progressBar = findViewById(R.id.detailProgressBar);
        errorTV = findViewById(R.id.detailErrorTV);
        backdropImageIV = findViewById(R.id.iv_backdrop_image);

        Picasso.with(this)
                // TODO verify what happens when backdrop_url is null in JSON, app crashes!
                .load(TextUtils.isEmpty(backdropUrl) ? null : backdropUrl)
                //.placeholder()
                .into(backdropImageIV, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                        errorTV.setVisibility(View.VISIBLE);
                    }
                });

    }

    /**
     * Handles errors closing the activity (and returning to MainActivity) and opening a Toast
     * message to warn the user.
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
