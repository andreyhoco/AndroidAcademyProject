package ru.andreyhoco.androidacademyproject.persistence

object TheMovieAppDbContract {
    const val DATABASE_NAME = "TheMovieApp.db"

    object Movies {
        const val TABLE_NAME = "Movies"

        const val COLUMN_NAME_ID = "movie_id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_OVERVIEW = "overview"
        const val COLUMN_NAME_POSTER = "poster"
        const val COLUMN_NAME_BACKDROP = "backdrop"
        const val COLUMN_NAME_RATINGS = "ratings"
        const val COLUMN_NAME_NUM_OF_RATINGS = "number_of_ratings"
        const val COLUMN_NAME_IS_ADULT = "is_adult"
        const val COLUMN_NAME_RUNTIME = "runtime"
    }

    object Actors {
        const val TABLE_NAME = "Actors"

        const val COLUMN_NAME_ID = "actor_id"
        const val COLUMN_NAME_ACTOR_NAME = "name"
        const val COLUMN_NAME_PICTURE = "picture"
    }

    object Genres {
        const val TABLE_NAME = "Genres"

        const val COLUMN_NAME_ID = "genre_id"
        const val COLUMN_NAME_GENRE_NAME = "name"
    }
}