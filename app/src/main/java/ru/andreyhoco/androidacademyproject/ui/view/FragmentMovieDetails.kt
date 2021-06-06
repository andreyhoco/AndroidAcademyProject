package ru.andreyhoco.androidacademyproject.ui.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.andreyhoco.TheMovieApp
import ru.andreyhoco.androidacademyproject.R
import ru.andreyhoco.androidacademyproject.ui.viewModels.MovieDetailsViewModel
import ru.andreyhoco.androidacademyproject.ui.viewModels.MovieDetailsViewModelFactory
import ru.andreyhoco.androidacademyproject.ui.adapters.ActorsAdapter
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Actor
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.UiState
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.diffUtils.ActorDiffCallback
import timber.log.Timber

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = this.arguments?.getLong("movie") ?:
            throw IllegalArgumentException("Arguments missing id")
        val context = requireContext()

        initViews(view)
        setUpListeners()
        val actorsAdapter = createActorsAdapter(context)
        setupActorList(actorsAdapter, context)

        val movieDetailsViewModel: MovieDetailsViewModel = ViewModelProvider(
            this,
            MovieDetailsViewModelFactory(
                (requireActivity().application as TheMovieApp)
                    .appDi
                    .movieRepository,
                movieId
            )
        ).get(MovieDetailsViewModel::class.java)

        setUpViewModel(movieDetailsViewModel, actorsAdapter, context)
    }

    override fun onDetach() {
        super.onDetach()
        clearViews()
    }

    private fun showData(movie: Movie, actorsAdapter: ActorsAdapter, context: Context) {
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
            onActorsListChanged(actorsAdapter, movie.actors)
            showCast()
        }
    }

    private fun setupActorList(adapter: ActorsAdapter, context: Context) {
        castRecyclerView?.adapter = adapter
        castRecyclerView?.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false
        )

        Timber.tag("ACTORS").d("Setup actors list")
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

    private fun onActorsListChanged(actorsAdapter: ActorsAdapter, newActors: List<Actor>) {
        val actorsDiffCallback = ActorDiffCallback(actorsAdapter.actors, newActors)

        val actorsDiff = DiffUtil.calculateDiff(actorsDiffCallback)
        actorsDiff.dispatchUpdatesTo(actorsAdapter)
        actorsAdapter.actors = newActors
    }

    private fun handleUiState(
        state: UiState,
        context: Context
    ) {
        when (state) {
            is UiState.Loading -> {
                setLoading(true)
            }
            is UiState.DisplayData -> {
                setLoading(false)
            }
            is UiState.DisplayError -> {
                setLoading(false)
                val errorDescription = when (state) {
                    is UiState.DisplayError.ServerError -> {
                        resources.getString(R.string.server_error)
                    }
                    is UiState.DisplayError.NetworkError -> {
                        resources.getString(R.string.network_error)
                    }
                }
                Toast.makeText(context, errorDescription, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createActorsAdapter(context: Context): ActorsAdapter {
        return ActorsAdapter(context, emptyList())
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
    }

    private fun setUpListeners() {
        backButton?.setOnClickListener{
            activity?.onBackPressed()
        }
    }

    private fun setUpViewModel(
        viewModel: MovieDetailsViewModel,
        actorsAdapter: ActorsAdapter,
        context: Context
    ) {
        lifecycleScope.launch {
            viewModel.fragmentState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach { state ->
                    handleUiState(state, context)
                }.launchIn(this)

            viewModel.movieFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach { movie ->
                    showData(movie, actorsAdapter, context)
                }.launchIn(this)
        }
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
    }

    companion object {
        fun newInstance(movieId: Long): FragmentMovieDetails {
            val args = Bundle()
            args.putLong("movie", movieId)
            val fragment = FragmentMovieDetails()
            fragment.arguments = args
            return fragment
        }
    }
}