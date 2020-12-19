package ru.andreyhoco.androidacademyproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FragmentMovieDetails : Fragment() {
    private var posterImage: ImageView? = null
    private var pgText: TextView? = null
    private var titleText: TextView? = null
    private var genreLine: TextView? = null
    private var reviewsLine: TextView? = null
    private var storylineText: TextView? = null
    private var castList: RecyclerView? = null
    private var backButton: TextView? = null

    private var movie: Movie? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.movie_details_back_button)
        backButton?.setOnClickListener{
            fragmentManager?.popBackStack()
        }
        val movieId = this.arguments?.getInt("movie")
        movieId?.let {
            movie = getMovies()[it]
        }
        posterImage = view.findViewById<ImageView>(R.id.movie_poster)
        pgText = view.findViewById<TextView>(R.id.movie_pg)
        titleText = view.findViewById<TextView>(R.id.movie_title)
        genreLine = view.findViewById<TextView>(R.id.tag_line)
        reviewsLine = view.findViewById<TextView>(R.id.reviews)
        storylineText = view.findViewById<TextView>(R.id.storyline_description)
//        val ratingBar = TODO
        castList = view.findViewById<RecyclerView>(R.id.actor_list)

        movie?.let {
            posterImage?.setImageResource(it.posterImageId)
            pgText?.text = context?.getString(
                R.string.pg_with_placeholder,
                it.pg
            )
            titleText?.text = it.title
            genreLine?.text = it.genre.joinToString()
            reviewsLine?.text = context?.getString(
                R.string.reviews_with_placeholder,
                it.numberOfReviews
            )
            storylineText?.text = it.storyline

            val actorAdapter = ActorAdapter(requireContext(), getActors())
            castList?.adapter = actorAdapter
            castList?.layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false
            )
        } ?: posterImage?.setImageResource(R.drawable.ic_error)



    }

    override fun onDestroyView() {
        super.onDestroyView()
        backButton = null
        posterImage = null
        pgText = null
        titleText = null
        genreLine = null
        reviewsLine = null
        storylineText = null
//        ratingBar = TODO
        castList = null
    }

    companion object {
        fun newInstance(movieId: Int): FragmentMovieDetails {
            val args: Bundle = Bundle()
            args.putInt("movie", movieId)
            val fragment = FragmentMovieDetails()
            fragment.arguments = args
            return fragment
        }
    }
}