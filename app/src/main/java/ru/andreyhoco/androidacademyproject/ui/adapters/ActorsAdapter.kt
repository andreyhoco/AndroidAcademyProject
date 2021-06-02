package ru.andreyhoco.androidacademyproject.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.andreyhoco.androidacademyproject.R
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Actor

class ActorsAdapter(
    private val context: Context,
    var actors: List<Actor>
) : RecyclerView.Adapter<ActorsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val actorImage = view.findViewById<ImageView>(R.id.actor_item_image)
        private val actorName = view.findViewById<TextView>(R.id.actor_item_name)

        fun bind(actor: Actor) {
            if (actor.picture == "") {
                actorImage.setImageResource(R.drawable.ic_baseline_account_circle_24)
            } else {
                actorImage.loadActorImage(actor.picture)
            }
            actorName.text = actor.name
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

fun ImageView.loadActorImage(imageUrl: String) {
    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.drawable.ic_baseline_account_circle_24)
        .centerCrop()
        .transform(RoundedCorners(15))
        .into(this)
}