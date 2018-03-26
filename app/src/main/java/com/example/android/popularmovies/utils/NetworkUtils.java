package com.example.android.popularmovies.utils;

import android.net.Uri;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utilities to communicate with the API and to manipulate JSON Strings.
 */
public class NetworkUtils {
    // Selectors for query type
    public static final int QUERY_LIST = 1;
    public static final int QUERY_SEARCH = 2;

    // Selectors for image type
    public static final int IMAGE_POSTER = 0;
    public static final int IMAGE_BACKDROP = 1;

    // API Key for themoviedb.org
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String API_KEY_QUERY = "api_key";

    // URL parts for images
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";
    private static final String IMAGE_SIZE_W342 = "w342";
    private static final String IMAGE_SIZE_W780 = "w780";

    // URL parts for movies list
    private static final String API_BASE_URL = "http://api.themoviedb.org/3";
    private static final String API_MOVIE = "movie";
    private static final String API_PAGE = "page";
    private static final String API_SEARCH = "search";
    private static final String API_QUERY = "query";
    private static final String API_LANGUAGE = "language";
    private static final String API_LANGUAGE_EN = "en-US";

    // JSON constants
    private static final String JSON_TOTAL_PAGES = "total_pages";
    private static final String JSON_RESULTS = "results";
    private static final String JSON_ID = "id";
    private static final String JSON_ORIGINAL_TITLE = "original_title";
    private static final String JSON_TITLE = "title";
    private static final String JSON_POSTER = "poster_path";
    private static final String JSON_BACKDROP = "backdrop_path";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RATING = "vote_average";
    private static final String JSON_DATE = "release_date";

    /**
     * Builds the complete url to an image.
     *
     * @param imageRelativePath the relative path of the image
     * @param imageType the image type (poster or backdrop)
     * @return the complete url of the image
     */
    public static URL getImageUrl(String imageRelativePath, int imageType) {
        String imageSize;

        // Check the image type and sets the image size
        switch (imageType) {
            case IMAGE_BACKDROP:
                imageSize = IMAGE_SIZE_W780;
                break;
            case IMAGE_POSTER:
            default:
                imageSize = IMAGE_SIZE_W342;
                break;
        }

        // Build the Uri
        Uri uri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(imageSize)
                .appendPath(imageRelativePath.substring(1))
                .build();

        URL url = null;
        try {
            // Transform the Uri into a URL
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Gets the url which will be used to retrieve the movies list
     *
     * @param sortBy sort order preference
     * @param page the actual page
     * @param type the type of query (show a list of movies or search for movies)
     * @param query a search query
     * @return the url for the movies list
     */
    public static URL getMoviesListUrl(String sortBy, String page, int type, String query) {
        Uri uri;
        if (type == NetworkUtils.QUERY_LIST) {
            // Build the Uri
            uri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendPath(API_MOVIE)
                    .appendPath(sortBy)
                    .appendQueryParameter(API_PAGE, String.valueOf(page))
                    .appendQueryParameter(API_LANGUAGE, API_LANGUAGE_EN)
                    .appendQueryParameter(API_KEY_QUERY, API_KEY)
                    .build();
        } else if (type == NetworkUtils.QUERY_SEARCH) {
            // Build the Uri
            uri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendPath(API_SEARCH)
                    .appendPath(API_MOVIE)
                    .appendQueryParameter(API_PAGE, String.valueOf(page))
                    .appendQueryParameter(API_LANGUAGE, API_LANGUAGE_EN)
                    .appendQueryParameter(API_KEY_QUERY, API_KEY)
                    .appendQueryParameter(API_QUERY, query)
                    .build();
        } else {
            uri = null;
        }

        URL url = null;
        try {
            if (uri != null) {
                // Transform the Uri into a URL
                url = new URL(uri.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Parses a JSON String containing the movies list, and returns a List of Movie objects
     *
     * @param jsonString the JSON String containing the movies list
     * @return a List of Movie objects
     */
    public static List<Movie> parseMoviesList(String jsonString) {
        List<Movie> movies = new ArrayList<>();

        int movieId;
        String originalTitle;
        String title;
        String posterPath;
        String backdropPath;
        String overview;
        double userRating;
        String releaseDate;

        // Extract all movie properties
        try {
            JSONObject list = new JSONObject(jsonString);
            Page.setTotalPages(Integer.parseInt(list.optString(JSON_TOTAL_PAGES)));
            JSONArray results = list.getJSONArray(JSON_RESULTS);
            for (int i = 0; i < results.length(); i++) {
                JSONObject movieJson = results.optJSONObject(i);
                movieId = movieJson.optInt(JSON_ID);
                originalTitle = movieJson.optString(JSON_ORIGINAL_TITLE);
                title = movieJson.optString(JSON_TITLE);
                posterPath = movieJson.optString(JSON_POSTER);
                backdropPath = movieJson.optString(JSON_BACKDROP);
                overview = movieJson.optString(JSON_OVERVIEW);
                userRating = movieJson.optDouble(JSON_RATING);
                releaseDate = movieJson.optString(JSON_DATE);

                // Pick only the year from the release date, we don't care about month and day
                if (!releaseDate.equals("")) {
                    releaseDate = releaseDate.substring(0, 4);
                }

                // If there is no original title, substitute it with the title
                if (originalTitle.equals("") && (!title.equals(""))) {
                    originalTitle = title;
                }

                // If there is no title, substitute it with the original title
                if (!originalTitle.equals("") && (title.equals(""))) {
                    title = originalTitle;
                }

                // Create a single Movie object
                Movie movie = new Movie(movieId, originalTitle, title, posterPath, backdropPath,
                        overview, userRating, releaseDate);

                // Add the Movie object to the List
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (movies.size() > 0) {
            // Return the List of Movie objects
            return movies;
        } else {
            return null;
        }
    }

    /**
     * Returns the entire result from the HTTP response.
     * (Taken from the Sunshine project: https://github.com/udacity/ud851-Sunshine)
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
