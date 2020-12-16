package ru.andreyhoco.androidacademyproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActorAdapter(val context: Context,val actors: List<Actor>) : RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val actorImage = view.findViewById<ImageView>(R.id.actor_item_image)
        private val actorName = view.findViewById<TextView>(R.id.actor_item_name)

        fun bind(actor: Actor) {
            actorImage.setImageResource(actor.imageId)
            actorName.text = context.getString(
                R.string.actor_name,
                "${actor.firstName} ${actor.lastName}"
            )
        }
    }

    override fun getItemCount() = actors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.actor_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(actors[position])
    }
}