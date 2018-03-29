
package com.example.android.movieapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.movieapp.MainActivity;
import com.squareup.picasso.Picasso;

import com.example.android.movieapp.R;

import java.util.List;

/**
 * Created by PATEL on 2/11/2018.
 */

public class RecyclerMovie extends RecyclerView.Adapter<RecyclerMovie.MyViewHolder> {


    private List<Movie> mMovieList;
    //Implementing on click listner
    final private ListItemClickListener mOnClickListener;

    //Interface

    public interface ListItemClickListener {

        void onListItemClick(Movie movie);
    }


    public RecyclerMovie(MainActivity mainActivity, List<Movie> movieList, ListItemClickListener listener) {
        mMovieList = movieList;
        mOnClickListener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Movie movie = mMovieList.get(position);
        holder.itemView.setTag(movie.getId());
        Context context = holder.img_movie.getContext();
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getImage())
                .into(holder.img_movie);

        holder.bind(mMovieList.get(position), mOnClickListener);


    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView img_movie;


        public MyViewHolder(View itemView) {
            super(itemView);
            img_movie = itemView.findViewById(R.id.imageView);


            itemView.setOnClickListener(this);

        }

        public void bind(final Movie movie, final ListItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onListItemClick(movie);
                }
            });
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie posterClick = mMovieList.get(adapterPosition);
            mOnClickListener.onListItemClick(posterClick);
        }
    }
}
