package com.example.android.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Page;
import com.example.android.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

/**
 * The main activity of the app. It shows a grid of movie posters. When the user taps on a movie
 * poster it transitions to a details screen with additional information about the movie.
 * A setting option is available to change the sort order, either by top rated movies, or by most
 * popular ones.
 * The user can also search for movies inserting a query string.
 */
public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderCallbacks<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        SwipeRefreshLayout.OnRefreshListener {

    private final static int COLUMNS_IN_GRID_LAYOUT_PORTRAIT = 2;
    private final static int COLUMNS_IN_GRID_LAYOUT_LANDSCAPE = 4;
    private final static int MOVIE_LOADER_ID = 0;
    private static String sortByPreference;
    private ProgressBar mLoadingIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private MovieAdapter mAdapter;
    private TextView mErrorMessage;
    private final LoaderCallbacks<List<Movie>> callback = MainActivity.this;
    private FloatingActionButton floatingActionButtonLeft;
    private FloatingActionButton floatingActionButtonRight;
    private TextView actualPage;
    private boolean returnFromSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences;

        // Implements SwipeRefreshLayout in order to be able to refresh the RecyclerView
        // without having to close and restart the app
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Two FloatingActionButtons for navigating through pages
        floatingActionButtonLeft = findViewById(R.id.floatingActionButtonLeft);
        floatingActionButtonRight = findViewById(R.id.floatingActionButtonRight);

        // Shows the actual page and the total number of pages available
        // The user can click on the text view and an alert dialog is shown in which the user
        // can insert a page number to navigate to
        actualPage = findViewById(R.id.actual_pages);
        actualPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the alert dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle(getString(R.string.go_to_page));
                alertDialog.setMessage(getString(R.string.enter_page));

                // Create the EditText which will get the input from the user
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(layoutParams);
                alertDialog.setView(input);

                // Set the buttons
                alertDialog.setPositiveButton(getString(R.string.go),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int pageNUm = Integer.parseInt(input.getText().toString());
                                if ((pageNUm >= 1) && (pageNUm <= Page.getTotalPages())) {
                                    // Set the actual page as from the user input and restart the loader
                                    // to show the page the user chose
                                    Page.setPage(pageNUm);
                                    mAdapter.clear();
                                    getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, callback);
                                } else {
                                    // If the number was not in the available range, show a Toast
                                    Toast.makeText(MainActivity.this, getString(R.string.no_page_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                alertDialog.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel the dialog
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });

        // Initialize the error message and a loading indicator
        mErrorMessage = findViewById(R.id.tv_error_message);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        // Initialize the RecyclerView and the LayoutManager
        mRecyclerView = findViewById(R.id.rv_movies);
        mRecyclerView.setHasFixedSize(false);

        // Set the GridLayoutManager with a different number of columns depending on whether
        // the orientation is portrait or landscape (we limit this to 2 for portrait and 4 for
        // landscape. This should be ok both for smartphones and for tablets
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, COLUMNS_IN_GRID_LAYOUT_PORTRAIT);
        } else {
            gridLayoutManager = new GridLayoutManager(this, COLUMNS_IN_GRID_LAYOUT_LANDSCAPE);
        }

        // Set the LayoutManager for the RecyclerView
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Show or hide the FABs when the user is at page start or end
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if ((gridLayoutManager.findLastVisibleItemPosition() == mAdapter.getItemCount() - 1)
                        && (Page.getPage() < Page.getTotalPages())) {
                    floatingActionButtonRight.show();
                } else {
                    floatingActionButtonRight.hide();
                }
                if ((gridLayoutManager.findFirstVisibleItemPosition() == 0)
                        && (Page.getPage() > 1)) {
                    floatingActionButtonLeft.show();
                } else {
                    floatingActionButtonLeft.hide();
                }
            }
        });

        // Set the adapter
        mAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Initialize the default settings the first time the app is run
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Get the preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Register a listener which notifies when the preferences have been changed
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Get the sort order preference
        sortByPreference =
                sharedPreferences.getString(this.getString(R.string.pref_sortby_key),
                        this.getString(R.string.pref_sortby_default));

        // Set the click listeners on the two FABs
        floatingActionButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Page.getPage() > 1) {
                    // Navigate one page backwards
                    Page.setPage(Page.getPage() - 1);
                    mAdapter.clear();
                    getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, callback);
                }
            }
        });

        floatingActionButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Page.getPage() < Page.getTotalPages()) {
                    // Navigate one page forwards
                    Page.setPage(Page.getPage() + 1);
                    mAdapter.clear();
                    getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, callback);
                }
            }
        });

        // Initialize the adapter
        mAdapter.clear();

        // Start or restart the loader
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (returnFromSearch) {
            // If the user comes back from search, restart from page 1
            Page.setPage(1);
            mAdapter.clear();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, callback);
            returnFromSearch = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the listener for preference changes
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Manages clicks on RecyclerViews items.
     * When the user clicks on a poster image, an Inten is launched for DetailActivity, and through
     * the intent a Movie object is passed.
     *
     * @param movie the movie object which is passed through the intent
     */
    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        // Hide the RecyclerView and show the ProgressBar
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        // Create a new instance of MovieLoader
        return new MovieLoader(this);
    }

    @Override
    public void onRefresh() {
        // Restart the loader
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, callback);

        // Signal that refresh has finished
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        // Hide the ProgressBar and the right FAB
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        floatingActionButtonRight.hide();
        // Pass the movies to the adapter. If there is no data, show an error message
        // Hide or show the FABs according to the situation
        mAdapter.setMovies(data);
        if (data == null) {
            floatingActionButtonLeft.hide();
            actualPage.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        } else {
            // If we are at page 1 there is no need for a left FAB
            if (Page.getPage() == 1) {
                floatingActionButtonLeft.hide();
            } else {
                floatingActionButtonLeft.show();
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mErrorMessage.setVisibility(View.INVISIBLE);
            // Set the values of actual page and total pages and show the TextView
            actualPage.setText(getString(R.string.actual_page, Page.getPage(), Page.getTotalPages()));
            actualPage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        // Not implemented
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Get the sort order preference
        sortByPreference =
                sharedPreferences.getString(this.getString(R.string.pref_sortby_key),
                        this.getString(R.string.pref_sortby_default));

        // Clear the adapter
        Page.setPage(1);
        floatingActionButtonRight.hide();
        mAdapter.clear();

        // Restart the loader
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, callback);
    }

    // Inflate the settings menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Set a SearchView with which the user can insert a query string and search for movies
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                Context context = MainActivity.this;
                Class destinationClass = SearchActivity.class;
                Intent intent = new Intent(context, destinationClass);
                intent.putExtra(SearchActivity.EXTRA_QUERY_STRING, queryString);
                Page.setPage(1);
                returnFromSearch = true;
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    // Manage the selection of menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Context context = this;
        Class destinationClass;
        Intent intent;
        switch (id) {
            case R.id.action_settings:
                context = this;
                destinationClass = SettingsActivity.class;
                intent = new Intent(context, destinationClass);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                // Clear the adapter
                mAdapter.clear();

                // Restart the loader
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, callback);
                break;

            case R.id.action_credits:
                destinationClass = CreditsActivity.class;
                intent = new Intent(context, destinationClass);
                startActivity(intent);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A subclass of AsyncTaskLoader which is responsible for communicating with the API on a
     * background thread.
     */
    public static class MovieLoader extends AsyncTaskLoader<List<Movie>> {
        List<Movie> movies = null;

        MovieLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            // Check if there is cached data,
            if (movies != null) {
                // If there is cached data, call deliverResult and pass the cached data as argument
                deliverResult(movies);
            } else {
                // If there is no cached data, force load
                forceLoad();
            }
        }

        @Nullable
        @Override
        public List<Movie> loadInBackground() {
            int queryType = NetworkUtils.QUERY_LIST;
            // Get the url with which to make the http request
            URL movieRequestUrl = NetworkUtils.getMoviesListUrl(
                    sortByPreference,
                    String.valueOf(Page.getPage()),
                    queryType,
                    null);
            try {
                // Make the http request to the API
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                // Parse the JSON response into a List of Movie objects
                movies = NetworkUtils.parseMoviesList(jsonResponse);

                // Return the list
                return movies;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Cache data
         *
         * @param moviesList the List of Movie objects
         */
        public void deliverResult(List<Movie> moviesList) {
            movies = moviesList;
            super.deliverResult(moviesList);
        }
    }
}
