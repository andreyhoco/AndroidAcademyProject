package ru.andreyhoco.androidacademyproject

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


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
        val movieList = view.findViewById<RecyclerView>(R.id.movies_list)
        val movies = getMovies()
        val moviesAdapter = MoviesAdapter(requireContext(), this, movies)
        movieList.adapter = moviesAdapter

        val numberOfColumns = if (
            activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE
        ) {
            3
        } else {
            2
        }

        movieList.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
    }

    override fun onDetach() {
        super.onDetach()
        fragmentListener = null
    }


    override fun onClick(position: Int) {
        fragmentListener?.onListItemClicked(position)
    }
}

interface FragmentMoviesListListener {
    fun onListItemClicked(position: Int)
}