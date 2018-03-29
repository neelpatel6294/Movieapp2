package com.example.android.movieapp.Network;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.movieapp.Adapter.Movie;
import com.example.android.movieapp.Adapter.MovieReview;
import com.example.android.movieapp.Adapter.MovieTrailer;

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

import static android.content.ContentValues.TAG;

/**
 * Created by PATEL on 2/11/2018.
 */

public class NetworkUtils {

    final static String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIE_REVIEW_URL = "https://api.themoviedb.org/3/movie";
    final static String MOVIE_TRAILER_URL = "https://api.themoviedb.org/3/movie";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch";


    final static String API_KEY = "api_key";

    // Paste your Api key below......

    final static String api_key = "398df9fa06ef9233262ebc5d74a35f65";




    final static String LANGUAGE = "language";
    final static String language = "en-US";
    final static String SORT_BY = "sort_by";
    final static String INCLUDE_ADULT = "include_adult";
    final static String include_adult = "false";
    final static String INCLUDE_VIDEO = "include_video";
    final static String include_video = "false";


    //Fetching the json response--------------------------------------------------------------------

    public static List<Movie> fetchMovieData(URL url) {

        String jsonResponse = null;
        try {
            jsonResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Movie> movies = extractFeaturesFromJson(jsonResponse);
        return movies;

    }

    //Fetching json Response of Movie Trailer-------------------------------------------------------

    public static List<MovieTrailer> fetchMovieTrialerData(URL url) {
        String jsonResponse = null;
        try {
            jsonResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<MovieTrailer> movieTrailer = extractTrailerFeaturesFromJson(jsonResponse);
        return movieTrailer;
    }


    //Fetching json Response of Movie Review--------------------------------------------------------


    public static List<MovieReview> fetchMovieReviewData(URL url) {
        String jsonResponse = null;
        try {
            jsonResponse = getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<MovieReview> movieReviews = extractReviewFeaturesFromJson(jsonResponse);
        return movieReviews;
    }


    //Building URL used to query MOVIEDB

    public static URL buildURl(String sort) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                .appendPath(sort)
                .appendQueryParameter(API_KEY,api_key)
                .build();
        Log.i("NewUrl", String.valueOf(builtUri));
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return url;
    }

    //Method for getting response from Network

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

    //Method for json parsing

    private static List<Movie> extractFeaturesFromJson(String movieJson) {

        if (TextUtils.isEmpty((movieJson))) {
            return null;
        }

        //creating empty array list to add the movies
        List<Movie> movie = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(movieJson);


            JSONArray movieArray = baseJsonResponse.getJSONArray("results");


            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject currentMovie = movieArray.getJSONObject(i);

                String id = currentMovie.getString("id");

                String img_path = currentMovie.getString("poster_path");

                String vote_average = currentMovie.getString("vote_average");

                String release_date = currentMovie.getString("release_date");

                String plot = currentMovie.getString("overview");

                String title = currentMovie.getString("title");


                Movie movie1 = new Movie(id, img_path, title, release_date, vote_average, plot);

                movie.add(movie1);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
    }


    // All Network Functions For Movie Trailer ----------------------------------------------

    //MovieTrailer
    /*
   url to make =https://api.themoviedb.org/3/movie/440021/videos?api_key=053130b8fdf68ca19c58155b4bd37bdd&language=en-US
    */

    // Method for building Trailer url
    public static URL buildTrailerURl(String movieId) {
        Uri builtUri = Uri.parse(MOVIE_TRAILER_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter(API_KEY, api_key)
                .appendQueryParameter(LANGUAGE, language)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return url;
    }


    public static Uri buildYoutubeUrl(String trailer_key) {
        Uri trailer_uri = Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendQueryParameter("v", trailer_key)
                .build();


        return trailer_uri;


    }


    //Method for parsing jsondata

    public static List<MovieTrailer> extractTrailerFeaturesFromJson(String movieTrailerJson) {

        if (TextUtils.isEmpty((movieTrailerJson))) {
            return null;
        }

        //creating empty array list to add the movies
        List<MovieTrailer> movieTrailer = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(movieTrailerJson);


            JSONArray movieArray = baseJsonResponse.getJSONArray("results");


            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject currentMovie = movieArray.getJSONObject(i);

                String trailer_key = currentMovie.getString("key");
                Log.i("key", trailer_key);

                MovieTrailer movieTrailers = new MovieTrailer(trailer_key);

                movieTrailer.add(movieTrailers);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieTrailer;
    }


    //MovieReview Network functions--------------------------------------------


    public static URL buildUrlReview(String movieId) {
        URL urlReview = null;
        try {
            Uri movieReviewQueryUri = Uri.parse(MOVIE_REVIEW_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath("reviews")
                    .appendQueryParameter(API_KEY, api_key)
                    .build();
            urlReview = new URL(movieReviewQueryUri.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + urlReview);

        return urlReview;
    }


    //Method for parsing jsondata

    public static List<MovieReview> extractReviewFeaturesFromJson(String movieTrailerJson) {

        if (TextUtils.isEmpty((movieTrailerJson))) {
            return null;
        }

        //creating empty array list to add the movies
        List<MovieReview> movieReviews = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(movieTrailerJson);


            JSONArray movieArray = baseJsonResponse.getJSONArray("results");


            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject currentMovie = movieArray.getJSONObject(i);

                String authorName = currentMovie.getString("author");
                Log.i("author",authorName);

                String reviewName = currentMovie.getString("content");
                Log.i("review",reviewName);

                MovieReview movieReview = new MovieReview(reviewName, authorName);

                movieReviews.add(movieReview);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieReviews;
    }


}
