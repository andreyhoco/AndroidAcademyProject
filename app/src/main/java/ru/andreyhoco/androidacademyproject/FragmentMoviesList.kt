package ru.andreyhoco.androidacademyproject

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.andreyhoco.androidacademyproject.viewModels.MoviesListViewModel
import ru.andreyhoco.androidacademyproject.viewModels.MoviesListViewModelFactory
import ru.andreyhoco.androidacademyproject.adapters.MoviesAdapter
import ru.andreyhoco.androidacademyproject.adapters.OnMovieItemClicked
import ru.andreyhoco.androidacademyproject.data.Movie

class FragmentMoviesList : Fragment(), OnMovieItemClicked {
    private var loadingView: ProgressBar? = null
    private var errorText: TextView? = null
    private var tryAgainBtn: TextView? = null
    private var movieRecyclerView: RecyclerView? = null

    private var fragmentListener: FragmentMoviesListListener? = null

    private val moviesListViewModel: MoviesListViewModel by viewModels {
        MoviesListViewModelFactory(
            MovieRepository(),
            lifecycleScope
        )
    }

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
        setUpListeners()
        setUpViewModel()
    }

    override fun onDetach() {
        super.onDetach()

        fragmentListener = null
        clearViews()
    }

    override fun onClick(movieId: Int) {
        fragmentListener?.onListItemClicked(movieId)
    }

    private fun setUpMovieList(list: List<Movie>) {
        movieRecyclerView?.adapter = MoviesAdapter(requireContext(), this, list)
        val numberOfColumns = resources.getInteger(R.integer.grid_column_count)
        movieRecyclerView?.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
    }

    private fun showError(errorDescription: String) {
        errorText?.text = errorDescription
        tryAgainBtn?.isVisible = true
        errorText?.isVisible = true
    }

    private fun hideError() {
        tryAgainBtn?.isVisible = false
        errorText?.isVisible = false
    }

    private fun setLoading(loading: Boolean) {
        loadingView?.isVisible = loading
    }

    private fun handleUiState(state: UiState<List<Movie>>) {
        when (state) {
            is UiState.Loading -> {
                setLoading(true)
            }
            is UiState.DataDisplay -> {
                setLoading(false)
                val movies = state.value
                setUpMovieList(movies)
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
        movieRecyclerView = view.findViewById(R.id.movies_list)
        loadingView = view.findViewById(R.id.loading_circle)
        errorText = view.findViewById(R.id.error_description)
        tryAgainBtn = view.findViewById(R.id.try_again_button)
    }

    private fun clearViews() {
        movieRecyclerView = null
        loadingView = null
        errorText = null
        tryAgainBtn = null
    }

    private fun setUpListeners() {
        tryAgainBtn?.setOnClickListener {
            hideError()
            moviesListViewModel.loadMovies()
        }
    }

    private fun setUpViewModel() {
        moviesListViewModel.fragmentState.observe(
            this@FragmentMoviesList.viewLifecycleOwner,
            this::handleUiState
        )
    }
}

interface FragmentMoviesListListener {
    fun onListItemClicked(movieId: Int)
}