package ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.diffUtils

import androidx.recyclerview.widget.DiffUtil
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Actor

class ActorDiffCallback(
    private val oldActors: List<Actor>,
    private val newActors: List<Actor>
) : DiffUtil.Callback(){

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldActors[oldItemPosition].id == newActors[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldActors[oldItemPosition] == newActors[newItemPosition]
    }

    override fun getOldListSize(): Int = oldActors.size

    override fun getNewListSize(): Int = newActors.size
}