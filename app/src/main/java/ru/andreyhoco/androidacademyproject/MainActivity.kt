package ru.andreyhoco.androidacademyproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.andreyhoco.androidacademyproject.data.Movie

class MainActivity : AppCompatActivity(), FragmentMoviesListListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragmentMoviesList = FragmentMoviesList()
            openFragment(fragmentMoviesList, false)
        }
    }

    override fun onListItemClicked(movieId: Int) {
        val fragmentMovieDetails = FragmentMovieDetails.newInstance(movieId)
        openFragment(fragmentMovieDetails, true)
    }

    private fun openFragment(fragment: Fragment, addToBackStack: Boolean) {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, fragment)
            if (addToBackStack) {
                addToBackStack(null)
            }
            commit()
        }
    }
}

