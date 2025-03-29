package adapter

import android.content.Context
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
import com.example.pcxlogin.ApiRes1
import com.example.pcxlogin.ApiResponse
import com.example.pcxlogin.CartClient
import com.example.pcxlogin.CartItem
import com.example.pcxlogin.DialogBuyNow
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
        val cartButton: ImageButton = itemView.findViewById(R.id.btnAddToCart)
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

        val formattedPrice = String.format("%,.2f", product.price)

        holder.nameText.text = product.name
        holder.descriptionText.text = product.description
        holder.priceText.text = "₱$formattedPrice"

        Glide.with(holder.imageView.context)
            .load(product.imageUrl)
            .centerCrop()
            .into(holder.imageView)

        val context = holder.itemView.context

        holder.buyNowButton.setOnClickListener {
            val dialog = DialogBuyNow(
                context = context,
                productName = product.name,
                productPrice = "₱$formattedPrice",
                productImageUrl = product.imageUrl,
                stock = product.stocks,
                productId = product.id,
                customerName = "John Doe"
            ) { quantity, totalPrice ->
                Toast.makeText(context, "Bought $quantity for ₱$totalPrice", Toast.LENGTH_SHORT).show()
                product.stocks -= quantity
                notifyItemChanged(holder.adapterPosition)
            }
            dialog.show()
        }

        holder.cartButton.setOnClickListener {
            val userId = getUserIdFromSession(context)
            CartClient.instance.addToCart(
                userId = userId,
                productId = product.id,
                productName = product.name,
                productPrice = product.price,
                productImageUrl = product.imageUrl,
                quantity = 1,
                productStock = product.stocks
            ).enqueue(object : Callback<ApiRes1> {
                override fun onResponse(call: Call<ApiRes1>, response: Response<ApiRes1>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "${product.name} added to cart!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to add ${product.name} to cart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiRes1>, t: Throwable) {
                    Toast.makeText(context, "Failed to add ${product.name} to cart", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val isFavorite = favoriteStates[position] ?: false
        holder.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite_border
        )

        holder.favoriteButton.setOnClickListener {
            val newState = !(favoriteStates[position] ?: false)
            favoriteStates[position] = newState
            holder.favoriteButton.setImageResource(
                if (newState) R.drawable.favorite_filled else R.drawable.favorite_border
            )

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

            val userId = getUserIdFromSession(context)
            val productId = product.id

            if (newState) {
                val request = AddFavoriteRequest(userId, productId, product.name, product.imageUrl, product.price)
                FavoritesClient.favoritesApi.addFavorite(request).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}

fun getUserIdFromSession(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    return sharedPreferences.getInt("user_id", -1)
}
