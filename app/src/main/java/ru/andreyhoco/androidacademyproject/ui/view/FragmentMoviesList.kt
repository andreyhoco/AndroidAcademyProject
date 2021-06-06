package ru.andreyhoco.androidacademyproject.ui.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.andreyhoco.TheMovieApp
import ru.andreyhoco.androidacademyproject.R
import ru.andreyhoco.androidacademyproject.ui.viewModels.MoviesListViewModel
import ru.andreyhoco.androidacademyproject.ui.viewModels.MoviesListViewModelFactory
import ru.andreyhoco.androidacademyproject.ui.adapters.MoviesAdapter
import ru.andreyhoco.androidacademyproject.ui.adapters.OnMovieItemClicked
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.UiState
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.diffUtils.MovieDiffCallback

class FragmentMoviesList : Fragment(), OnMovieItemClicked {
    private var loadingView: ProgressBar? = null
    private var movieRecyclerView: RecyclerView? = null

    private var fragmentListener: FragmentMoviesListListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentMoviesListListener) {
            fragmentListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()

        initViews(view)
        val moviesAdapter = createAdapter(context)
        setUpMoviesList(moviesAdapter, context)

        val repo = (requireActivity().application as TheMovieApp).appDi.movieRepository

        lifecycleScope.launch(Dispatchers.IO) {
            repo.testAll()
        }

        val moviesListViewModel: MoviesListViewModel = ViewModelProvider(
            this,
            MoviesListViewModelFactory(
                (requireActivity().application as TheMovieApp).appDi.movieRepository
            )
        ).get(MoviesListViewModel::class.java)

        setUpViewModel(moviesListViewModel, moviesAdapter, context)
    }

    override fun onDetach() {
        super.onDetach()

        fragmentListener = null
        clearViews()
    }

    override fun onClick(movieId: Long) {
        fragmentListener?.onListItemClicked(movieId)
    }

    private fun createAdapter(context: Context): MoviesAdapter {
        return MoviesAdapter(context, this, emptyList(), lifecycleScope)
    }

    private fun setUpMoviesList(adapter: MoviesAdapter, context: Context) {
        movieRecyclerView?.adapter = adapter
        val numberOfColumns = resources.getInteger(R.integer.grid_column_count)
        movieRecyclerView?.layoutManager = GridLayoutManager(context, numberOfColumns)
    }

    private fun setLoading(loading: Boolean) {
        loadingView?.isVisible = loading
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

    private fun onMoviesChanged(movies: List<Movie>, moviesAdapter: MoviesAdapter) {
        val movieDiffCallback = MovieDiffCallback(moviesAdapter.movies, movies)

        val diff = DiffUtil.calculateDiff(movieDiffCallback)
        diff.dispatchUpdatesTo(moviesAdapter)
        moviesAdapter.movies = movies
    }

    private fun initViews(view: View) {
        movieRecyclerView = view.findViewById(R.id.movies_list)
        loadingView = view.findViewById(R.id.loading_circle)
    }

    private fun clearViews() {
        movieRecyclerView = null
        loadingView = null
    }

    private fun setUpViewModel(
        viewModel: MoviesListViewModel,
        moviesAdapter: MoviesAdapter,
        context: Context
    ) {
        lifecycleScope.launch {
            viewModel.fragmentState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach { state ->
                    handleUiState(state, context)
                }.launchIn(this)

            viewModel.moviesFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach { movies ->
                    onMoviesChanged(movies, moviesAdapter)
                }.launchIn(this)
        }
    }
}

interface FragmentMoviesListListener {
    fun onListItemClicked(movieId: Long)
}