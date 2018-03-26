package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO Class that represents a single movie.
 * <p>
 * It implements Parcelable in order to be able to pass a Movie object through an intent.
 * The app does that when it transitions from MainActivity to DetailActivity, to show the details
 * of a particular movie.
 */
public class Movie implements Parcelable {

    /**
     * Implements the Parcelable.Creator interface
     */
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private int mMovieId; // The movie id
    private String mOriginalTitle; // original_title in the API
    private String mTitle; // title in the API
    private String mPosterImageUrl; // poster_path in the API
    private String mBackdropImageUrl; // backdrop_path in the API
    private String mOverview; // overview in the API
    private double mUserRating; // vote_average in the API
    private String mReleaseDate; // release_date in the API

    /**
     * Constructor which sets all the movie's member variables
     *
     * @param movieId          the movie's id
     * @param originalTitle    the movie's original title
     * @param title            the movie's title
     * @param posterImageUrl   the movie's poster image url
     * @param backdropImageUrl the movie's backdrop image url
     * @param overview         a synopsis of the movie
     * @param userRating       the movie's user rating
     * @param releaseDate      the movie's release date
     */
    public Movie(int movieId,
                 String originalTitle,
                 String title,
                 String posterImageUrl,
                 String backdropImageUrl,
                 String overview,
                 double userRating,
                 String releaseDate) {

        mMovieId = movieId;
        mOriginalTitle = originalTitle;
        mTitle = title;
        mPosterImageUrl = posterImageUrl;
        mBackdropImageUrl = backdropImageUrl;
        mOverview = overview;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
    }

    /**
     * Constructor to read from a Parcelable
     *
     * @param in a parcel to read
     */
    private Movie(Parcel in) {
        this.mMovieId = in.readInt();
        this.mOriginalTitle = in.readString();
        this.mTitle = in.readString();
        this.mPosterImageUrl = in.readString();
        this.mBackdropImageUrl = in.readString();
        this.mOverview = in.readString();
        this.mUserRating = in.readDouble();
        this.mReleaseDate = in.readString();
    }

    /**
     * Getter method for mMovieId
     *
     * @return the movie's id
     */
    public int getMovieId() {
        return mMovieId;
    }

    /**
     * Setter method for mMovieId
     *
     * @param movieId the movie's id
     */
    public void setMovieId(int movieId) {
        mMovieId = movieId;
    }

    /**
     * Getter method for mOriginalTitle
     *
     * @return the movie's original title
     */
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    /**
     * Setter method for mOriginalTitle
     *
     * @param originalTitle the movie's original title
     */
    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    /**
     * Getter method for mTitle
     *
     * @return the movie's title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Setter method for Title
     *
     * @param title the movie's original title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Getter method for mPosterImageUrl
     *
     * @return the movie's poster image url
     */
    public String getPosterImageUrl() {
        return mPosterImageUrl;
    }

    /**
     * Setter method for mPosterImageUrl
     *
     * @param posterImageUrl the movie's poster image url
     */
    public void setPosterImageUrl(String posterImageUrl) {
        mPosterImageUrl = posterImageUrl;
    }

    /**
     * Getter method for mBackdropImageUrl
     *
     * @return the movie's backdrop image url
     */
    public String getBackdropImageUrl() {
        return mBackdropImageUrl;
    }

    /**
     * Setter method for mBackdropImageUrl
     *
     * @param backdropImageUrl the movie's backdrop image url
     */
    public void setBackdropImageUrl(String backdropImageUrl) {
        mBackdropImageUrl = backdropImageUrl;
    }

    /**
     * Getter method for mOverview
     *
     * @return a synopsis of the movie
     */
    public String getOverview() {
        return mOverview;
    }

    /**
     * Setter method for mOverview
     *
     * @param overview a synopsis of the movie
     */
    public void setOverview(String overview) {
        mOverview = overview;
    }

    /**
     * Getter method for mUserRating
     *
     * @return the movie's user rating
     */
    public double getUserRating() {
        return mUserRating;
    }

    /**
     * Setter method for mUserRating
     *
     * @param userRating the movie's user rating
     */
    public void setUserRating(double userRating) {
        mUserRating = userRating;
    }

    /**
     * Getter method for mReleaseDate
     *
     * @return the movie's release date
     */
    public String getReleaseDate() {
        return mReleaseDate;
    }

    /**
     * Setter method for mReleaseDate
     *
     * @param releaseDate the movie's release date
     */
    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    /**
     * Method required from the Parcelable interface
     *
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Method required from the Parcelable interface
     *
     * @param parcel the parcel to write on
     * @param i      optional flags
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mMovieId);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mTitle);
        parcel.writeString(mPosterImageUrl);
        parcel.writeString(mBackdropImageUrl);
        parcel.writeString(mOverview);
        parcel.writeDouble(mUserRating);
        parcel.writeString(mReleaseDate);

    }
}
