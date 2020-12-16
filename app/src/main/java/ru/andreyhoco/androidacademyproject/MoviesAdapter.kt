package ru.andreyhoco.androidacademyproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoviesAdapter(
    private val context: Context,
    private val clickListener: OnMovieItemClicked,
    private val movies: List<Movie>
) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val posterImage = view.findViewById<ImageView>(R.id.movie_item_poster)
        private val pgText = view.findViewById<TextView>(R.id.movie_item_pg)
        private val titleText = view.findViewById<TextView>(R.id.movie_item_title)
        private val genreLine = view.findViewById<TextView>(R.id.movie_item_genre)
        private val reviewsLine = view.findViewById<TextView>(R.id.movie_item_reviews)
        private val durationLine = view.findViewById<TextView>(R.id.movie_item_duration)
        private val isFavoriteButton = view.findViewById<CheckBox>(R.id.movie_item_is_favorite)
//        private val rating = TODO

        fun bind(movie: Movie) {
            posterImage.setImageResource(movie.microPosterImageId)
            pgText.text = context.getString(
                R.string.pg_with_placeholder,
                movie.pg
            )
            titleText.text = movie.title
            genreLine.text = movie.genre.joinToString()
            reviewsLine.text = context.getString(
                R.string.reviews_with_placeholder,
                movie.numberOfReviews
            )
            durationLine.text = context.getString(
                R.string.film_duration,
                movie.durationInMinutes
            )
            isFavoriteButton.isChecked = movie.isFavorite
        }

        fun setIsFavoriteClickListener(listener: (View) -> Unit) {
            isFavoriteButton.setOnClickListener(listener)
        }

    }

    override fun getItemCount(): Int = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.movie_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(position)
        }

    }
}

interface OnMovieItemClicked {
    fun onClick(position: Int)
}