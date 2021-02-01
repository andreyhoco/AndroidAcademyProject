package ru.andreyhoco.androidacademyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import ru.andreyhoco.androidacademyproject.viewModels.MovieDetailsViewModel
import ru.andreyhoco.androidacademyproject.viewModels.MovieDetailsViewModelFactory
import ru.andreyhoco.androidacademyproject.adapters.ActorsAdapter
import ru.andreyhoco.androidacademyproject.data.Actor
import ru.andreyhoco.androidacademyproject.data.Movie

class FragmentMovieDetails : Fragment() {
    private var backButton: TextView? = null
    private var backdropImage: ImageView? = null
    private var pgText: TextView? = null
    private var titleText: TextView? = null
    private var genreLine: TextView? = null
    private var reviewsLine: TextView? = null
    private var storylineText: TextView? = null
    private var castTitle: TextView? = null
    //        private var ratingBar = TODO
    private var castRecyclerView: RecyclerView? = null
    private var loadingView: ProgressBar? = null
    private var backgroundPlaceholder: View? = null
    private var errorText: TextView? = null
    private var tryAgainBtn: TextView? = null

    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels {
        MovieDetailsViewModelFactory(
            MovieRepository(),
            lifecycleScope
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = this.arguments?.getInt("movie") ?:
            throw IllegalArgumentException("Arguments missing id")

        initViews(view)
        setUpListeners(movieId)
        setUpViewModel(movieId)
    }

    override fun onDetach() {
        super.onDetach()

        clearViews()
    }

    private fun showData(movie: Movie) {
        val context = requireContext()

        backdropImage?.let {
            Glide.with(it)
                .load(movie.backdrop)
                .into(it)
        }

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
//          ratingBar = TODO

        if (movie.actors.isEmpty()) {
            hideCast()
        } else {
            showCast()
            setupActorList(movie.actors)
        }
    }

    private fun setupActorList(actors: List<Actor>) {
        castRecyclerView?.adapter = ActorsAdapter(requireContext(), actors)
        castRecyclerView?.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false
        )
    }

    private fun showError(errorDescription: String) {
        errorText?.text = errorDescription
        tryAgainBtn?.isVisible = true
        errorText?.isVisible = true
        backgroundPlaceholder?.isVisible = true
    }

    private fun hideError() {
        tryAgainBtn?.isVisible = false
        errorText?.isVisible = false
        backgroundPlaceholder?.isVisible = false
    }

    private fun showCast() {
        castTitle?.isVisible = true
        castRecyclerView?.isVisible = true
    }

    private fun hideCast() {
        castTitle?.isVisible = false
        castRecyclerView?.isVisible = false
    }

    private fun setLoading(loading: Boolean) {
        loadingView?.isVisible = loading
        backgroundPlaceholder?.isVisible = loading
    }

    private fun handleUiState(state: UiState<Movie>) {
        when (state) {
            is UiState.Loading -> {
                setLoading(true)
            }
            is UiState.DataDisplay -> {
                val movie = state.value
                setLoading(false)
                showData(movie)
            }
            is UiState.DisplayError -> {
                setLoading(false)
                when (state) {
                    is UiState.DisplayError.ServerError -> {
                        showError(resources.getString(R.string.server_error))
                    }
                    else -> {
                        showError(resources.getString(R.string.network_error))
                    }
                }
            }
        }
    }

    private fun initViews(view: View) {
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
        loadingView = view.findViewById(R.id.loading_circle)
        backgroundPlaceholder = view.findViewById(R.id.loading_placeholder)
        errorText = view.findViewById(R.id.error_description)
        tryAgainBtn = view.findViewById(R.id.try_again_button)
    }

    private fun setUpListeners(movieId: Int) {
        backButton?.setOnClickListener{
            activity?.onBackPressed()
        }

        tryAgainBtn?.setOnClickListener {
            hideError()
            movieDetailsViewModel.loadMovie(movieId)
        }
    }

    private fun setUpViewModel(movieId: Int) {
        movieDetailsViewModel.loadMovie(movieId)
        movieDetailsViewModel.fragmentState.observe(
            this@FragmentMovieDetails.viewLifecycleOwner,
            this::handleUiState
        )
    }

    private fun clearViews() {
        backButton = null
        backdropImage = null
        pgText = null
        titleText = null
        genreLine = null
        reviewsLine = null
        storylineText = null
        castTitle = null
//          ratingBar = TODO
        castRecyclerView = null
        loadingView = null
        backgroundPlaceholder = null
        errorText = null
        tryAgainBtn = null
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