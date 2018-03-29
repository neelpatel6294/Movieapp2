package com.example.android.movieapp;


import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.Adapter.Movie;
import com.example.android.movieapp.Adapter.MovieReview;
import com.example.android.movieapp.Adapter.MovieReviewAdapter;
import com.example.android.movieapp.Adapter.MovieTrailer;
import com.example.android.movieapp.Adapter.MovieTrailerAdapter;
import com.example.android.movieapp.Data.Contract;
import com.example.android.movieapp.Network.NetworkUtils;
import com.facebook.stetho.Stetho;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import es.dmoral.toasty.Toasty;
/**
 * Created by PATEL on 2/16/2018.
 */
public class DetailActivity extends Activity implements OnLikeListener {

    private TextView txt_Title;
    private TextView txt_Plot;
    private TextView txt_Rating;
    private TextView txt_Release;
    private ImageView img_Poster;

    private LikeButton lykbtn;

    // for Trailer
    private RecyclerView mRecyclerView;
    private MovieTrailerAdapter mMovieTrailerAdapter;

    //for review
    private RecyclerView mRecyclerViewReview;
    private MovieReviewAdapter mMovieReviewAdapter;

    private ShareActionProvider mShareActionProvider;
    public String First_trailer_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        txt_Title = findViewById(R.id.title);
        img_Poster = findViewById(R.id.image_poster);
        txt_Plot = findViewById(R.id.plot);
        txt_Rating = findViewById(R.id.rating);
        txt_Release = findViewById(R.id.release);
        lykbtn = findViewById(R.id.star_button);
        lykbtn.setOnLikeListener(this);
        mRecyclerView = findViewById(R.id.recycler_trailer);
        mRecyclerViewReview = findViewById(R.id.recycler_review);

        Stetho.initializeWithDefaults(this);


        ActionBar actionBar = this.getActionBar();
        getActionBar();

        // for trailer adapter----------------------------------------------------------------------
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //for review adapter------------------------------------------------------------------------
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setAutoMeasureEnabled(true);
        mRecyclerViewReview.setLayoutManager(manager);
        mRecyclerViewReview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerViewReview.setItemAnimator(new DefaultItemAnimator());


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("DETAILS...");
        }

        final Movie movie = getIntent().getParcelableExtra("data");
        txt_Title.setText(movie.getTitle());
        txt_Plot.setText(movie.getOverview());
        txt_Rating.setText(movie.getVoteAverage() + "/10");
        txt_Release.setText(movie.getReleaseDate());
        Picasso.with(img_Poster.getContext()).load("https://image.tmdb.org/t/p/w500" + movie.getImage()).into(img_Poster);


        // pressing the button to save the list to the content provider

        lykbtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                Toasty.success(DetailActivity.this, movie.getTitle() + " added to Favorites !!").show();


                ContentValues contentValues = new ContentValues();

                contentValues.put(Contract.Entry.COLUMN_MOVIE_ID, movie.getId());
                contentValues.put(Contract.Entry.COLUMN_MOVIE_TITLE, movie.getTitle());
                contentValues.put(Contract.Entry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
                contentValues.put(Contract.Entry.COLUMN_MOVIE_VOTE, movie.getVoteAverage());
                contentValues.put(Contract.Entry.COLUMN_MOVIE_DATE, movie.getReleaseDate());
                contentValues.put(Contract.Entry.COLUMN_POSTER_PATH, movie.getImage());

                Uri uri = getContentResolver().insert(Contract.Entry.CONTENT_URI, contentValues);


            }

            @Override
            public void unLiked(LikeButton likeButton) {

                Toasty.error(DetailActivity.this, "SWIPE TO DELETE !!").show();

            }
        });


        // for movie trailer------------------------------------------------------------------------
        URL url = NetworkUtils.buildTrailerURl(movie.getId());
        new MovieTrailerAsyncTask().execute(url);

        // for movie review-------------------------------------------------------------------------
        URL url1 = NetworkUtils.buildUrlReview(movie.getId());
        new MovieReviewAsyncTask().execute(url1);

        //fetching the first trailer link to share youtube videos

    }


    //Async task for Movie Trailer------------------------------------------------------------------
    public class MovieTrailerAsyncTask extends AsyncTask<URL, Void, List<MovieTrailer>> {


        @Override
        protected void onPreExecute() {


            super.onPreExecute();
        }


        @Override
        protected List<MovieTrailer> doInBackground(URL... urls) {
            List<MovieTrailer> movieTrailersm = null;
            if (isOnline()) {
                List<MovieTrailer> result = NetworkUtils.fetchMovieTrialerData(urls[0]);
                Log.i("result", String.valueOf(result));
                movieTrailersm = result;
                return movieTrailersm;
            }
            return movieTrailersm;
        }


        @Override
        protected void onPostExecute(final List<MovieTrailer> movies) {
            if (isOnline() && movies != null) {
                mMovieTrailerAdapter = new MovieTrailerAdapter(movies, new MovieTrailerAdapter.ListItemClickListener() {
                    @Override
                    public void onListItemClick(MovieTrailer movieTrailer) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(NetworkUtils.buildYoutubeUrl(movieTrailer.getTrailer_key()));
                        startActivity(intent);
                        MovieTrailer share_link = movies.get(0);
                        First_trailer_link = share_link.getTrailer_key();

                    }
                });
                mRecyclerView.setAdapter(mMovieTrailerAdapter);

                mMovieTrailerAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(DetailActivity.this, "Trailers cant be fetched #offline", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //AsyncTask For Movie Review--------------------------------------------------------------------

    public class MovieReviewAsyncTask extends AsyncTask<URL, Void, List<MovieReview>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MovieReview> doInBackground(URL... urls) {
            List<MovieReview> movieReviewsm = null;
            if (isOnline()) {
                List<MovieReview> movieReviews = NetworkUtils.fetchMovieReviewData(urls[0]);
                Log.i("result", String.valueOf(movieReviews));
                movieReviewsm = movieReviews;
                return movieReviewsm;
            }

            return movieReviewsm;
        }

        @Override
        protected void onPostExecute(List<MovieReview> movieReviews) {
            if (isOnline() && movieReviews != null) {
                mMovieReviewAdapter = new MovieReviewAdapter(movieReviews);
                mRecyclerViewReview.setAdapter(mMovieReviewAdapter);
                mMovieReviewAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(DetailActivity.this, "Reviews cant be fetched #offline", Toast.LENGTH_SHORT).show();
            }


        }
    }


    //menu for sharing the first link of the movie (extra implementation !!)
    //share link only works when once any trailer is played after that share links succcessfully works
    //as the fetching of the first trailer link is set onclickListner
    //Any suggestions how to reslove this?

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                Toast.makeText(this, "Sharing is caring", Toast.LENGTH_SHORT).show();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.valueOf(NetworkUtils.buildYoutubeUrl(First_trailer_link)));
                Log.i("share", String.valueOf(NetworkUtils.buildYoutubeUrl(First_trailer_link)));
                startActivity(Intent.createChooser(sharingIntent, "Share your Favorite Movie Trailer !!"));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // Function for checking the Network connectivity-----------------------------------------------
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void liked(LikeButton likeButton) {

    }

    @Override
    public void unLiked(LikeButton likeButton) {

    }
}
