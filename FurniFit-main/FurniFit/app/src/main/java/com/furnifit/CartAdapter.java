package com.furnifit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private CartRepository cartRepository = CartRepository.getInstance();
    private OnCartChangedListener listener;
    private Context context; // Add context field

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public CartAdapter(List<CartItem> cartItems, OnCartChangedListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext(); // Initialize context
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        holder.cartItemName.setText(product.getName());
        holder.cartItemPrice.setText(product.getPrice());
        holder.cartItemQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // --- THIS IS THE KEY CHANGE FOR THE IMAGE ---
        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .placeholder(product.getImageResId())
                        .error(product.getImageResId())
                        .into(holder.cartItemImage);
            }).addOnFailureListener(e -> {
                holder.cartItemImage.setImageResource(product.getImageResId());
            });
        } else {
            holder.cartItemImage.setImageResource(product.getImageResId());
        }
        // ---------------------------------------------

        holder.btnIncrease.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onCartChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                notifyItemChanged(position);
                listener.onCartChanged();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            cartRepository.removeProduct(cartItem);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImage;
        TextView cartItemName, cartItemPrice, cartItemQuantity;
        ImageButton btnIncrease, btnDecrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.cart_item_image);
            cartItemName = itemView.findViewById(R.id.cart_item_name);
            cartItemPrice = itemView.findViewById(R.id.cart_item_price);
            cartItemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
