package ru.andreyhoco.androidacademyproject.ui.view

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import ru.andreyhoco.androidacademyproject.MovieNotifications
import ru.andreyhoco.androidacademyproject.R
import ru.andreyhoco.androidacademyproject.background.WorkRepository
import timber.log.Timber

class MainActivity : AppCompatActivity(), FragmentMoviesListListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        if (savedInstanceState == null) {
            val fragmentMoviesList = FragmentMoviesList()
            openFragment(fragmentMoviesList, false)

            if (intent != null) {
                handleOpenMovieIntent(intent)
            }
            setupWorks(applicationContext)
        }
    }

    override fun onListItemClicked(movieId: Long) {
        val fragmentMovieDetails = FragmentMovieDetails.newInstance(movieId)
        openFragment(fragmentMovieDetails, true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleOpenMovieIntent(intent)
        }
    }

    private fun setupWorks(appContext: Context) {
        Timber.tag("WORK_BUG_FIX").d("Setup unique work")

        val workRepository = WorkRepository()
        val workManager = WorkManager.getInstance(appContext)

        workManager.cancelUniqueWork(WorkRepository.PERIODIC_MOVIES_UPDATE)
        Timber.tag("WORK_BUG_FIX").d("Info ${workManager.getWorkInfosForUniqueWork(WorkRepository.PERIODIC_MOVIES_UPDATE)}")
        workManager.enqueueUniquePeriodicWork(
            WorkRepository.PERIODIC_MOVIES_UPDATE,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRepository.periodicMoviesUpdateRequest
        )
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

    private fun handleOpenMovieIntent(intent : Intent) {
        when (intent.action) {
            ACTION_VIEW -> {
                val movieId = intent.data?.lastPathSegment?.toLongOrNull()
                if (movieId != null) {
                    val fragmentMovieDetails = FragmentMovieDetails.newInstance(movieId)
                    openFragment(fragmentMovieDetails, true)
                }
            }
        }
    }
}

