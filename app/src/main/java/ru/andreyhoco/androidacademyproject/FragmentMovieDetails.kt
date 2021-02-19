package ru.andreyhoco.androidacademyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import ru.andreyhoco.androidacademyproject.viewModels.MovieDetailsViewModel
import ru.andreyhoco.androidacademyproject.viewModels.MovieDetailsViewModelFactory
import ru.andreyhoco.androidacademyproject.adapters.ActorsAdapter
import ru.andreyhoco.androidacademyproject.adapters.loadImage
import ru.andreyhoco.androidacademyproject.data.Actor
import ru.andreyhoco.androidacademyproject.data.Movie
import ru.andreyhoco.androidacademyproject.data.loadMovies

class FragmentMovieDetails : Fragment() {

    var backButton: TextView? = null
    var backdropImage: ImageView? = null
    var pgText: TextView? = null
    var titleText: TextView? = null
    var genreLine: TextView? = null
    var reviewsLine: TextView? = null
    var storylineText: TextView? = null
    var castTitle: TextView? = null
    //        var ratingBar = TODO
    var castRecyclerView: RecyclerView? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movieId = this.arguments?.getInt("movie") ?: 0

        backButton = view.findViewById(R.id.movie_details_back_button)
        backdropImage = view.findViewById(R.id.movie_poster)
        pgText = view.findViewById(R.id.movie_pg)
        titleText = view.findViewById(R.id.movie_title)
        genreLine = view.findViewById(R.id.tag_line)
        reviewsLine = view.findViewById(R.id.reviews)
        storylineText = view.findViewById(R.id.storyline_description)
        castTitle = view.findViewById(R.id.cast_title)
//          ratingBar = TODO
        castRecyclerView = view.findViewById(R.id.actor_list)

//        backButton.setOnClickListener{
//            fragmentManager?.popBackStack()
//        }

        lifecycleScope.launch(Dispatchers.Main) {
            val moviesList = loadMovies(requireContext())
            val movie = moviesList[movieId]

            val movieDetailsViewModel: MovieDetailsViewModel by viewModels {
                MovieDetailsViewModelFactory(movie)
            }

            val movieLiveData = movieDetailsViewModel.movieLiveData
            movieLiveData.observe(this@FragmentMovieDetails.viewLifecycleOwner) {
                setupViews(movie)

                castRecyclerView?.let {
                    if (movie.actors.isEmpty()) {
                        it.visibility = View.GONE
                    } else {
                        it.visibility = View.VISIBLE
                        val adapter = createActorAdapter(movie.actors)
                        setupActorList(it, adapter)
                    }
                }
            }
        }
    }

    private fun setupViews(movie: Movie) {
        val context = requireContext()

        backdropImage?.loadImage(movie.backdrop)
        pgText?.text = context.getString(
            R.string.pg_with_placeholder,
            movie.minimumAge
        )
        titleText?.text = movie.title
        genreLine?.text = movie.genres.joinToString { it.name }
        reviewsLine?.text = context.getString(
            R.string.reviews_with_placeholder,
            movie.numberOfRatings
        )
        storylineText?.text = movie.overview
        castTitle?.visibility = if (movie.actors.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
//          ratingBar = TODO
    }

    private fun createActorAdapter(list: List<Actor>): ActorsAdapter {
        return ActorsAdapter(requireContext(), list)
    }

    private fun setupActorList(view: RecyclerView, adapter: ActorsAdapter) {
        view.adapter = adapter
        view.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false
        )
    }

    companion object {
        fun newInstance(movieId: Int): FragmentMovieDetails {
            val args = Bundle()
            args.putInt("movie", movieId)
            val fragment = FragmentMovieDetails()
            fragment.arguments = args
            return fragment
        }
    }
}