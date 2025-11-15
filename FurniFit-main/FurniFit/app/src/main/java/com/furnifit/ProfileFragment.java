package com.furnifit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class ProfileFragment extends Fragment {

    private TextView myOrders;
    private TextView shippingAddresses;
    private TextView paymentMethods;
    private TextView logout;
    private TextView profileName;
    private TextView profileEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        myOrders = view.findViewById(R.id.my_orders);
        shippingAddresses = view.findViewById(R.id.shipping_addresses);
        paymentMethods = view.findViewById(R.id.payment_methods);
        logout = view.findViewById(R.id.logout);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);

        loadUserProfile();

        myOrders.setOnClickListener(v -> {
            Toast.makeText(getContext(), "My Orders clicked", Toast.LENGTH_SHORT).show();
        });

        shippingAddresses.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Shipping Addresses clicked", Toast.LENGTH_SHORT).show();
        });

        paymentMethods.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Payment Methods clicked", Toast.LENGTH_SHORT).show();
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    profileName.setText(name);
                    profileEmail.setText(email);
                } else {
                    profileName.setText("User");
                    profileEmail.setText(user.getEmail());
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
