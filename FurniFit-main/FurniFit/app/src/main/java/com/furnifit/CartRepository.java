package com.furnifit;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private static CartRepository instance;
    private List<CartItem> cartItems = new ArrayList<>();

    private CartRepository() {}

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void addProduct(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(product));
    }

    public void removeProduct(CartItem cartItem) {
        cartItems.remove(cartItem);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            String priceString = item.getProduct().getPrice().replace("$", "");
            try {
                total += Double.parseDouble(priceString) * item.getQuantity();
            } catch (NumberFormatException e) {
                // Handle parsing error
            }
        }
        return total;
    }

    // New methods for the home screen quantity selector
    public CartItem findItem(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getName().equals(product.getName())) {
                return item;
            }
        }
        return null;
    }

    public void increaseQuantity(CartItem cartItem) {
        cartItem.setQuantity(cartItem.getQuantity() + 1);
    }

    public void decreaseQuantity(CartItem cartItem) {
        if (cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
        } else {
            // If quantity becomes 0, remove the item from the cart
            removeProduct(cartItem);
        }
    }
}
