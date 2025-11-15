package com.furnifit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private CartRepository cartRepository = CartRepository.getInstance();

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());

        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .placeholder(product.getImageResId())
                        .error(product.getImageResId())
                        .into(holder.productImage);
            }).addOnFailureListener(e -> {
                holder.productImage.setImageResource(product.getImageResId());
            });
        } else {
            holder.productImage.setImageResource(product.getImageResId());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("PRODUCT", product);
            context.startActivity(intent);
        });

        holder.viewInArButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArActivity.class);
            intent.putExtra("MODEL_NAME", product.getModelName());
            context.startActivity(intent);
        });

        updateCartControls(holder, product);
    }

    private void updateCartControls(ProductViewHolder holder, Product product) {
        CartItem cartItem = cartRepository.findItem(product);

        if (cartItem == null) {
            holder.addToCartButton.setVisibility(View.VISIBLE);
            holder.quantitySelector.setVisibility(View.GONE);

            holder.addToCartButton.setOnClickListener(v -> {
                cartRepository.addProduct(product);
                notifyItemChanged(holder.getAdapterPosition());
                Toast.makeText(context, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.addToCartButton.setVisibility(View.GONE);
            holder.quantitySelector.setVisibility(View.VISIBLE);
            holder.quantityText.setText(String.valueOf(cartItem.getQuantity()));

            holder.increaseButton.setOnClickListener(v -> {
                cartRepository.increaseQuantity(cartItem);
                notifyItemChanged(holder.getAdapterPosition());
            });

            holder.decreaseButton.setOnClickListener(v -> {
                cartRepository.decreaseQuantity(cartItem);
                notifyItemChanged(holder.getAdapterPosition());
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantityText;
        Button viewInArButton, addToCartButton;
        LinearLayout quantitySelector;
        ImageButton increaseButton, decreaseButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            viewInArButton = itemView.findViewById(R.id.btn_view_in_ar);
            addToCartButton = itemView.findViewById(R.id.btn_add_to_cart);
            quantitySelector = itemView.findViewById(R.id.quantity_selector);
            increaseButton = itemView.findViewById(R.id.btn_increase_home);
            decreaseButton = itemView.findViewById(R.id.btn_decrease_home);
            quantityText = itemView.findViewById(R.id.quantity_home);
        }
    }
}
