package ru.andreyhoco.androidacademyproject.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.andreyhoco.androidacademyproject.R
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.MovieShortDesc
import timber.log.Timber
import java.util.*

class MoviesAdapter(
    private val context: Context,
    private val clickListener: OnMovieItemClicked,
    var movies: List<MovieShortDesc>,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val posterImage = view.findViewById<ImageView>(R.id.movie_item_poster)
        private val titleText = view.findViewById<AppCompatTextView>(R.id.movie_item_title)
        private val genreLine = view.findViewById<AppCompatTextView>(R.id.movie_item_genre)
        private val reviewsLine = view.findViewById<AppCompatTextView>(R.id.movie_item_reviews)
        private val yearOfReleaseLine = view.findViewById<AppCompatTextView>(R.id.movie_release_year)
//        private val isFavoriteButton = view.findViewById<CheckBox>(R.id.movie_item_is_favorite)
//        private val rating = TODO

        fun bind(movieShortDesc: MovieShortDesc) {
            coroutineScope.launch(Dispatchers.IO) {
                val precomputedTitle = PrecomputedTextCompat.create(
                    movieShortDesc.title,
                    titleText.textMetricsParamsCompat
                )

                val precomputedGenres = PrecomputedTextCompat.create(
                    movieShortDesc.genres.joinToString { it.name },
                    genreLine.textMetricsParamsCompat
                )
                val precomputedReviews = PrecomputedTextCompat.create(
                    context.getString(
                        R.string.reviews_with_placeholder,
                        movieShortDesc.numberOfRatings
                    ),
                    reviewsLine.textMetricsParamsCompat
                )
                val precomputedDuration = PrecomputedTextCompat.create(
                    movieShortDesc.releaseYear.toString(),
                    yearOfReleaseLine.textMetricsParamsCompat
                )

                withContext(Dispatchers.Main) {
                    titleText.setPrecomputedText(precomputedTitle)
                    genreLine.setPrecomputedText(precomputedGenres)
                    yearOfReleaseLine.setPrecomputedText(precomputedDuration)
                    reviewsLine.setPrecomputedText(precomputedReviews)
                }
            }

            posterImage.loadImage(movieShortDesc.poster)
        }
    }

    override fun getItemCount(): Int = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val calendar = Calendar.getInstance()
        val currTime = calendar.timeInMillis

        val inflatedViewHolder = ViewHolder(inflater.inflate(R.layout.movie_item, parent, false))

        Timber.tag("OPT text").d("Time to inflate ViewHolder: ${Calendar.getInstance().timeInMillis - currTime} ms")
        return inflatedViewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(movies[position].id)
        }
    }

    override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
        return true
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