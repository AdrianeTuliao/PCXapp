package adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pcxlogin.AddFavorites.FavoritesClient
import com.example.pcxlogin.AddFavorites.AddFavoriteRequest
import com.example.pcxlogin.AddFavorites.FavoriteItemLocal
import com.example.pcxlogin.ApiResponse
import com.example.pcxlogin.DialogBuyNow
import com.example.pcxlogin.FetchFavorites.FavoritesFragment
import com.example.pcxlogin.Product
import com.example.pcxlogin.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val favoriteStates = mutableMapOf<Int, Boolean>()

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.productName)
        val descriptionText: TextView = itemView.findViewById(R.id.productDescription)
        val priceText: TextView = itemView.findViewById(R.id.productPrice)
        val imageView: ImageView = itemView.findViewById(R.id.productImage)
        val buyNowButton: Button = itemView.findViewById(R.id.btnBuyNow)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.buyNowButton.isEnabled = product.stocks > 0
        holder.buyNowButton.text = if (product.stocks > 0) "Buy Now" else "Out of Stock"

        val priceValue = product.price
        val formattedPrice = String.format("%,.2f", priceValue)

        holder.nameText.text = product.name
        holder.descriptionText.text = product.description
        holder.priceText.text = "₱$formattedPrice"

        Glide.with(holder.imageView.context)
            .load(product.imageUrl)
            .centerCrop()
            .into(holder.imageView)

        holder.buyNowButton.setOnClickListener {
            val context = holder.itemView.context

            val dialog = DialogBuyNow(
                context = context,
                productName = product.name,
                productPrice = "₱$formattedPrice",
                productImageUrl = product.imageUrl,
                stock = product.stocks,
                productId = product.id,
                customerName = "John Doe"
            ) { quantity, totalPrice ->
                Toast.makeText(context, "Bought $quantity for ₱$totalPrice", Toast.LENGTH_SHORT)
                    .show()
                product.stocks -= quantity
                notifyItemChanged(holder.adapterPosition)
            }

            dialog.show()
        }

        // --- FAVORITE BUTTON LOGIC ---
        val isFavorite = favoriteStates[position] ?: false

        holder.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite_border
        )

        holder.favoriteButton.setOnClickListener {
            val newState = !(favoriteStates[position] ?: false)
            favoriteStates[position] = newState

            Log.d("FavoriteButton", "Clicked! New state: $newState, Product: ${product.name}")

            holder.favoriteButton.setImageResource(
                if (newState) R.drawable.favorite_filled else R.drawable.favorite_border
            )

            // Animate button
            holder.favoriteButton.animate()
                .scaleX(1.5f).scaleY(1.5f)
                .rotationBy(20f)
                .setDuration(150)
                .withEndAction {
                    holder.favoriteButton.animate()
                        .scaleX(0.8f).scaleY(0.8f)
                        .rotationBy(-40f)
                        .setDuration(150)
                        .withEndAction {
                            holder.favoriteButton.animate()
                                .scaleX(1f).scaleY(1f)
                                .rotationBy(20f)
                                .setDuration(150)
                        }
                }

            val productName = product.name
            val productPrice = product.price
            val productImage = product.imageUrl
            val userId = 1
            val productId = product.id

            if (newState) {
                FavoritesFragment.favoriteItem.add(
                    FavoriteItemLocal(productName, productPrice, productImage)
                )

                val request = AddFavoriteRequest(userId, productId, productName, productImage, productPrice)

                Log.d("FavoriteAPI", "Sending favorite request: $request")

                FavoritesClient.favoritesApi.addFavorite(request).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        Log.d("FavoriteAPI", "Response code: ${response.code()}")
                        Log.d("FavoriteAPI", "Response body: ${response.body()}")
                        Log.d("FavoriteAPI", "Raw Response: ${response.raw()}")
                        Log.d("FavoriteAPI", "Body: ${response.body()}")
                        Log.d("FavoriteAPI", "ErrorBody: ${response.errorBody()?.string()}")

                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(
                                holder.itemView.context,
                                "Added to favorites!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.e("FavoriteAPI", "Failed response: ${response.errorBody()?.string()}")
                            Toast.makeText(
                                holder.itemView.context,
                                "Failed to add favorite.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Log.e("FavoriteAPI", "Error: ${t.message}", t)
                        Toast.makeText(
                            holder.itemView.context,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            } else {
                FavoritesFragment.favoriteItem.removeIf { it.name == productName }
                Log.d("FavoriteButton", "Removed from local favorites: $productName")
            }
        }
    }
}
