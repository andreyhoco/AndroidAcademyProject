package ru.andreyhoco.androidacademyproject

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.andreyhoco.androidacademyproject.viewModels.MoviesListViewModel
import ru.andreyhoco.androidacademyproject.viewModels.MoviesListViewModelFactory
import ru.andreyhoco.androidacademyproject.adapters.MoviesAdapter
import ru.andreyhoco.androidacademyproject.adapters.OnMovieItemClicked
import ru.andreyhoco.androidacademyproject.data.Movie
import ru.andreyhoco.androidacademyproject.data.loadMovies

class FragmentMoviesList : Fragment(), OnMovieItemClicked {

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
        val movieRecyclerView = view.findViewById<RecyclerView>(R.id.movies_list)

        lifecycleScope.launch(Dispatchers.Main) {
            val moviesList = loadMovies(requireContext())
            val moviesListViewModel: MoviesListViewModel by viewModels {
                MoviesListViewModelFactory(
                    moviesList
                )
            }

            val moviesListLiveData: LiveData<List<Movie>> = moviesListViewModel.moviesListLiveData
            moviesListLiveData.observe(this@FragmentMoviesList.viewLifecycleOwner) {
                val adapter = createMovieAdapter(it)
                setupMovieList(movieRecyclerView, adapter)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentListener = null
    }


    override fun onClick(movieId: Int) {
        fragmentListener?.onListItemClicked(movieId)
    }

    private fun createMovieAdapter(list: List<Movie>): MoviesAdapter {
        return MoviesAdapter(requireContext(), this, list)
    }

    private fun setupMovieList(view: RecyclerView, adapter: MoviesAdapter) {
        view.adapter = adapter
        val numberOfColumns = resources.getInteger(R.integer.grid_column_count)
        view.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
    }
}

interface FragmentMoviesListListener {
    fun onListItemClicked(movieId: Int)
}