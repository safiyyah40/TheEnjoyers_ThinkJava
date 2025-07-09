package com.example.theenjoyers_thinkjava

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LeaderboardAdapter(private var rankList: List<LeaderboardEntry>) :
    RecyclerView.Adapter<LeaderboardAdapter.RankViewHolder>() {

    class RankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankNumber: TextView = itemView.findViewById(R.id.tv_rank_number)
        val avatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val score: TextView = itemView.findViewById(R.id.tv_score)
        val card: CardView = itemView.findViewById(R.id.card_rank_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_rank, parent, false)
        return RankViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        val entry = rankList[position]

        holder.rankNumber.text = entry.rank.toString()
        holder.name.text = entry.name
        holder.score.text = "${entry.score} pts"

        Glide.with(holder.itemView.context)
            .load(entry.avatarUrl.ifEmpty { null }) // Handle jika URL kosong
            .placeholder(R.drawable.ic_default_avatar)
            .error(R.drawable.ic_default_avatar)
            .fallback(R.drawable.ic_default_avatar)
            .circleCrop()
            .into(holder.avatar)

        if (entry.isCurrentUser) {
            holder.card.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.light_green_highlight)
            )
        } else {
            holder.card.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.white)
            )
        }
    }

    override fun getItemCount() = rankList.size

    fun updateData(newRankList: List<LeaderboardEntry>) {
        this.rankList = newRankList
        notifyDataSetChanged()
    }
}