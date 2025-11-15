package com.furnifit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartFragment extends Fragment implements CartAdapter.OnCartChangedListener {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private CartRepository cartRepository = CartRepository.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        totalPriceTextView = view.findViewById(R.id.total_price);
        checkoutButton = view.findViewById(R.id.btn_checkout);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartRepository.getCartItems(), this);
        cartRecyclerView.setAdapter(cartAdapter);

        updateTotalPrice();

        checkoutButton.setOnClickListener(v -> {
            if (cartRepository.getCartItems().isEmpty()) {
                Toast.makeText(getContext(), "Please add something to your cart.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Thanks for purchasing! Please continue to shop with us.", Toast.LENGTH_SHORT).show();
                cartRepository.clearCart();
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }
        });

        return view;
    }

    @Override
    public void onCartChanged() {
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        totalPriceTextView.setText(String.format("Total: $%.2f", cartRepository.getTotalPrice()));
    }
}
