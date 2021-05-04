package ru.andreyhoco.androidacademyproject.ui.adapters

import android.content.Context
import android.text.PrecomputedText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.andreyhoco.androidacademyproject.R
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import timber.log.Timber
import java.util.*

class MoviesAdapter(
    private val context: Context,
    private val clickListener: OnMovieItemClicked,
    var movies: List<Movie>,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val posterImage = view.findViewById<ImageView>(R.id.movie_item_poster)
        private val pgText = view.findViewById<AppCompatTextView>(R.id.movie_item_pg)
        private val titleText = view.findViewById<AppCompatTextView>(R.id.movie_item_title)
        private val genreLine = view.findViewById<AppCompatTextView>(R.id.movie_item_genre)
        private val reviewsLine = view.findViewById<AppCompatTextView>(R.id.movie_item_reviews)
        private val durationLine = view.findViewById<AppCompatTextView>(R.id.movie_item_duration)
        private val isFavoriteButton = view.findViewById<CheckBox>(R.id.movie_item_is_favorite)
//        private val rating = TODO

        fun bind(movie: Movie) {
            Timber.plant(Timber.DebugTree())
            val calendar = Calendar.getInstance()
            val currTime = calendar.timeInMillis

            coroutineScope.launch(Dispatchers.IO) {
                val precomputedTitle = PrecomputedTextCompat.create(
                    movie.title,
                    titleText.textMetricsParamsCompat
                )
                val precomputedPgText = PrecomputedTextCompat.create(
                    context.getString(
                        R.string.pg_with_placeholder,
                        movie.minimumAge
                    ),
                    pgText.textMetricsParamsCompat
                )
                val precomputedGenres = PrecomputedTextCompat.create(
                    movie.genres.joinToString { it.name },
                    genreLine.textMetricsParamsCompat
                )
                val precomputedReviews = PrecomputedTextCompat.create(
                    context.getString(
                        R.string.reviews_with_placeholder,
                        movie.numberOfRatings
                    ),
                    reviewsLine.textMetricsParamsCompat
                )
                val precomputedDuration = PrecomputedTextCompat.create(
                    if (movie.runtime == 0) {
                        ""
                    } else {
                        context.getString(
                            R.string.film_duration,
                            movie.runtime
                        )
                    },
                    durationLine.textMetricsParamsCompat
                )

                withContext(Dispatchers.Main) {
                    titleText.setPrecomputedText(precomputedTitle)
                    pgText.setPrecomputedText(precomputedPgText)
                    genreLine.setPrecomputedText(precomputedGenres)
                    durationLine.setPrecomputedText(precomputedDuration)
                    reviewsLine.setPrecomputedText(precomputedReviews)
                }
            }

            isFavoriteButton.isChecked = false
//            posterImage.loadImage(movie.poster)
            posterImage.setImageResource(R.drawable.ic_baseline_movie_24)
            Timber.tag("OPT text").d("Time to joinToString: ${Calendar.getInstance().timeInMillis - currTime} ms")
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
            clickListener.onClick(movies[position].id)
        }
    }


}

interface OnMovieItemClicked {
    fun onClick(movieId: Long)
}

fun ImageView.loadImage(imageUrl: String) {
    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.drawable.ic_baseline_movie_24)
        .centerCrop()
        .transform(RoundedCorners(15))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}