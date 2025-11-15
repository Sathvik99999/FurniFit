package com.furnifit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String ACTION_CART_CHANGED = "com.furnifit.CART_CHANGED";

    private Product product;
    private CartRepository cartRepository = CartRepository.getInstance();

    private ImageView productImageView;
    private TextView productNameTextView, productDescriptionTextView, quantityTextView;
    private Button addToCartButton, viewInArButton;
    private LinearLayout quantitySelector;
    private ImageButton increaseButton, decreaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        product = (Product) getIntent().getSerializableExtra("PRODUCT");

        productImageView = findViewById(R.id.detail_product_image);
        productNameTextView = findViewById(R.id.detail_product_name);
        productDescriptionTextView = findViewById(R.id.detail_product_description);
        quantityTextView = findViewById(R.id.detail_quantity);
        addToCartButton = findViewById(R.id.detail_btn_add_to_cart);
        viewInArButton = findViewById(R.id.detail_btn_view_in_ar);
        quantitySelector = findViewById(R.id.detail_quantity_selector);
        increaseButton = findViewById(R.id.detail_btn_increase);
        decreaseButton = findViewById(R.id.detail_btn_decrease);

// --- THIS IS THE KEY CHANGE FOR THE IMAGE ---
        // Get the storage path from the product object (e.g., "images/eren.png")
        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            // Get a reference to the image in Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);

            // Ask for the download URL and then use Glide to load it
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(this) /* context */
                        .load(uri) /* the download URL */
                        .placeholder(product.getImageResId()) /* local placeholder */
                        .error(product.getImageResId()) /* fallback image */
                        .into(productImageView); /* target ImageView */
            }).addOnFailureListener(e -> {
                // If it fails, load the local fallback image
                productImageView.setImageResource(product.getImageResId());
            });
        } else {
            // Fallback to local resource if no path is provided
            productImageView.setImageResource(product.getImageResId());
        }
        // ---------------------------------------------        productNameTextView.setText(product.getName());
        productDescriptionTextView.setText(product.getDescription());

        updateCartControls();

        viewInArButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ArActivity.class);
            intent.putExtra("MODEL_NAME", product.getModelName()); // Pass the model name
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Notify other parts of the app that the cart might have changed
        Intent intent = new Intent(ACTION_CART_CHANGED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void updateCartControls() {
        CartItem cartItem = cartRepository.findItem(product);

        if (cartItem == null) {
            addToCartButton.setVisibility(View.VISIBLE);
            quantitySelector.setVisibility(View.GONE);

            addToCartButton.setOnClickListener(v -> {
                cartRepository.addProduct(product);
                updateCartControls();
                Toast.makeText(this, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
            });
        } else {
            addToCartButton.setVisibility(View.GONE);
            quantitySelector.setVisibility(View.VISIBLE);
            quantityTextView.setText(String.valueOf(cartItem.getQuantity()));

            increaseButton.setOnClickListener(v -> {
                cartRepository.increaseQuantity(cartItem);
                updateCartControls();
            });

            decreaseButton.setOnClickListener(v -> {
                cartRepository.decreaseQuantity(cartItem);
                updateCartControls();
            });
        }
    }
}
