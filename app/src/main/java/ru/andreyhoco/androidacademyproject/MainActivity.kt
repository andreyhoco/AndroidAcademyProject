package ru.andreyhoco.androidacademyproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), FragmentMoviesListListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragmentMoviesList = FragmentMoviesList()
            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, fragmentMoviesList)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onListItemClicked(position: Int) {
        val fragmentMovieDetails = FragmentMovieDetails.newInstance(position)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragmentMovieDetails)
            addToBackStack(null)
            commit()
        }
    }
}