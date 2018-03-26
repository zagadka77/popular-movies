package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter responsible for showing the movie poster images in MainActivity through a RecyclerView.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final MovieAdapterOnClickHandler mClickHandler;
    private List<Movie> mMovies;

    /**
     * The constructor which initializes the click handler
     *
     * @param clickHandler the click handler
     */
    MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * This takes care of the layout inflation for the RecyclerView's single item
     * and returns a ViewHolder.
     *
     * @param parent   the ViewGroup into which the new View will be added
     * @param viewType the type of the new View which will be created
     * @return the new ViewHolder
     */
    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new MovieAdapterViewHolder(view);
    }

    /**
     * This is executed to display the data in the specified position.
     *
     * @param holder   the ViewHolder which has to be updated
     * @param position the position of the item within the data set
     */
    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        String posterRelativeUrl = mMovies.get(position).getPosterImageUrl();
        String posterUrl =
                NetworkUtils.getImageUrl(posterRelativeUrl, NetworkUtils.IMAGE_POSTER).toString();

        // TODO manage the cases when there is no image or image is null
        Picasso.with(holder.posterIV.getContext())
                .load(posterUrl)
                .into(holder.posterIV);

        holder.titleTV.setText(mMovies.get(position).getTitle());
        holder.yearTV.setText(mMovies.get(position).getReleaseDate());
    }

    /**
     * Returns the total number of items in the adapter
     *
     * @return the number of items in the adapter
     */
    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        } else {
            return mMovies.size();
        }
    }

    /**
     * Sets the List of Movie objects and notify that the data set has changed.
     *
     * @param movies the List of Movie objects
     */
    void setMovies(List<Movie> movies) {
        mMovies = movies;

        notifyDataSetChanged();
    }

    /**
     * Clear the data set
     */
    void clear() {
        mMovies = null;
        notifyDataSetChanged();
    }

    /**
     * The interfaces which will be implemented to manage clicks on single movie items.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    /**
     * The ViewHolder which will manage the single items in the RecyclerView
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView posterIV;
        final TextView titleTV;
        final TextView yearTV;

        /**
         * The constructor
         *
         * @param view the view of the single item to be managed by the ViewHolder
         */
        MovieAdapterViewHolder(View view) {
            super(view);
            posterIV = view.findViewById(R.id.iv_poster_image);
            titleTV = view.findViewById(R.id.tv_movie_title);
            yearTV = view.findViewById(R.id.tv_movie_year);

            // Set an OnClickListener on the item
            view.setOnClickListener(this);
        }

        /**
         * Manages the click events on the single item, calling the onClick function in MainActivity
         *
         * @param view the view which has been clicked on it
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovies.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }
}
