package com.furnifit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;

    private BroadcastReceiver cartChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (productAdapter != null) {
                productAdapter.notifyDataSetChanged();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get products from the central repository
        productAdapter = new ProductAdapter(getContext(), ProductRepository.getInstance().getProducts());
        productsRecyclerView.setAdapter(productAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(cartChangeReceiver, new IntentFilter(ProductDetailActivity.ACTION_CART_CHANGED));
        // Refresh the adapter when the fragment is resumed
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(cartChangeReceiver);
        super.onPause();
    }
}
