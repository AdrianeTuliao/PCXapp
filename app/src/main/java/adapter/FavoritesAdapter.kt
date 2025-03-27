package com.example.pcxlogin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pcxlogin.FetchFavorites.FavoriteItemResponse

class FavoritesAdapter(private val favoriteItems: List<FavoriteItemResponse>) :
    RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFavoriteProduct: ImageView = itemView.findViewById(R.id.imgFavoriteProduct)
        val tvFavoriteName: TextView = itemView.findViewById(R.id.tvFavoriteName)
        val tvFavoritePrice: TextView = itemView.findViewById(R.id.tvFavoritePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = favoriteItems[position]

        Log.d("FavoritesDebug", "Binding item: ${item.productName}, Image URL: ${item.imageUrl}")

        holder.tvFavoriteName.text = item.productName
        holder.tvFavoritePrice.text = "₱${item.price}"

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_eye_closed) // Show this image if loading fails
            .into(holder.imgFavoriteProduct)
    }



    override fun getItemCount(): Int = favoriteItems.size
}
