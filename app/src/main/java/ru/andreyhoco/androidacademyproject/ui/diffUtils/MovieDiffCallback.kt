package ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.diffUtils

import androidx.recyclerview.widget.DiffUtil
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.MovieShortDesc

class MovieDiffCallback(
    private val oldList: List<MovieShortDesc>,
    private val newList: List<MovieShortDesc>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}