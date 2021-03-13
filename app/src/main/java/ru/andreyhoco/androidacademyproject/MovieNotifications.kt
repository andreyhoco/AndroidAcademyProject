package ru.andreyhoco.androidacademyproject

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.androidacademyproject.ui.view.MainActivity

class MovieNotifications(
    private val appContext: Context
) {
    companion object {
        private const val MOVIES_CHANNEL = "movies_channel"
        private const val MOVIE_INTENT_CODE = 1
        private const val MOVIE_TAG = "movie"
    }

    private val notificationManagerCompat = NotificationManagerCompat.from(appContext)

    fun initChannel() {
        if (notificationManagerCompat.getNotificationChannel(MOVIES_CHANNEL) == null) {

            val interestingMoviesChannel = NotificationChannelCompat.Builder(
                    MOVIES_CHANNEL,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT
                )
                .setName(appContext.getString(R.string.movie_channel_name))
                .setDescription(appContext.getString(R.string.movie_channel_description))
                .build()

            notificationManagerCompat.createNotificationChannel(interestingMoviesChannel)
        }
    }

    fun showMovieNotification(movie: Movie) {
        val contentUri ="ru.andreyhoco://androidacademyproject/movie/${movie.id}".toUri()

        val pendingIntent =PendingIntent.getActivity(
            appContext,
            MOVIE_INTENT_CODE,
            Intent(appContext, MainActivity::class.java)
                .setAction(Intent.ACTION_VIEW)
                .setData(contentUri),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val movieNotificationBuilder = NotificationCompat.Builder(
            appContext,
            MOVIES_CHANNEL
        )
            .setContentTitle(appContext.getString(R.string.movie_notification_title, movie.title))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setSmallIcon(R.drawable.notification_small_icon)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.InboxStyle()
                .addLine(
                    appContext.getString(
                        R.string.movie_notification_genre_line,
                        movie.genres.joinToString { it.name }
                    )
                )
                .addLine(movie.overview)
            )
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())

        notificationManagerCompat.notify(MOVIE_TAG, movie.id.toInt(), movieNotificationBuilder.build())
    }
}