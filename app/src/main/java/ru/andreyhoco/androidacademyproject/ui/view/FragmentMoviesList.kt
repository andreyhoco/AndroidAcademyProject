package ru.andreyhoco.androidacademyproject.ui.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.andreyhoco.TheMovieApp
import ru.andreyhoco.androidacademyproject.BuildConfig
import ru.andreyhoco.androidacademyproject.R
import ru.andreyhoco.androidacademyproject.persistence.entities.MovieActorCrossRef
import ru.andreyhoco.androidacademyproject.ui.viewModels.MoviesListViewModel
import ru.andreyhoco.androidacademyproject.ui.viewModels.MoviesListViewModelFactory
import ru.andreyhoco.androidacademyproject.ui.adapters.MoviesAdapter
import ru.andreyhoco.androidacademyproject.ui.adapters.OnMovieItemClicked
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.UiState
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.diffUtils.MovieDiffCallback
import timber.log.Timber

class FragmentMoviesList : Fragment(), OnMovieItemClicked {
    private var loadingView: ProgressBar? = null
    private var movieRecyclerView: RecyclerView? = null

    private var fragmentListener: FragmentMoviesListListener? = null

    private var moviesListViewModel: MoviesListViewModel? = null

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

        initViews(view)
        val moviesAdapter = createAdapter()
        setUpMovieList(moviesAdapter)

        moviesListViewModel = ViewModelProvider(
            this,
            MoviesListViewModelFactory(
                (requireActivity().application as TheMovieApp).appDi.movieRepository
            )
        ).get(MoviesListViewModel::class.java)
        setUpViewModel(moviesAdapter)
    }

    override fun onDetach() {
        super.onDetach()

        fragmentListener = null
        clearViews()
    }

    override fun onClick(movieId: Long) {
        fragmentListener?.onListItemClicked(movieId)
    }

    private fun createAdapter(): MoviesAdapter {
        return MoviesAdapter(requireContext(), this, emptyList())
    }

    private fun setUpMovieList(adapter: MoviesAdapter) {
        movieRecyclerView?.adapter = adapter
        val numberOfColumns = resources.getInteger(R.integer.grid_column_count)
        movieRecyclerView?.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
    }

    private fun setLoading(loading: Boolean) {
        loadingView?.isVisible = loading
    }

    private fun handleUiState(state: UiState<List<Movie>>, moviesAdapter: MoviesAdapter) {
        when (state) {
            is UiState.Loading -> {
                setLoading(true)
            }
            is UiState.DataDisplay -> {
                setLoading(false)
                val movies = state.value
                onMoviesChanged(movies, moviesAdapter)
            }
            is UiState.DisplayError -> {
                val errorDescription = when (state) {
                    is UiState.DisplayError.ServerError -> {
                        resources.getString(R.string.server_error)
                    }
                    else -> {
                        resources.getString(R.string.network_error)
                    }
                }
                Toast.makeText(requireContext(), errorDescription, Toast.LENGTH_LONG).show()
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

    private fun setUpViewModel(moviesAdapter: MoviesAdapter) {
        moviesListViewModel?.fragmentState?.observe(
            this@FragmentMoviesList.viewLifecycleOwner
        ) { state ->
            handleUiState(state, moviesAdapter)
        }
    }
}

interface FragmentMoviesListListener {
    fun onListItemClicked(movieId: Long)
}