package ru.andreyhoco.androidacademyproject.ui.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.work.WorkManager
import ru.andreyhoco.androidacademyproject.R
import ru.andreyhoco.androidacademyproject.background.WorkRepository
import timber.log.Timber

class MainActivity : AppCompatActivity(), FragmentMoviesListListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragmentMoviesList = FragmentMoviesList()
            openFragment(fragmentMoviesList, false)
        }

        Timber.plant(Timber.DebugTree())

       setupWorks(applicationContext)
    }

    override fun onListItemClicked(movieId: Long) {
        val fragmentMovieDetails = FragmentMovieDetails.newInstance(movieId)
        openFragment(fragmentMovieDetails, true)
    }

    private fun setupWorks(appContext: Context) {
        val workRepository = WorkRepository()
        val workManager = WorkManager.getInstance(appContext)
        workManager.enqueue(workRepository.periodicMoviesUpdateRequest)
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

