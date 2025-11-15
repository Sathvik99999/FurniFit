package com.furnifit;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static ProductRepository instance;
    private List<Product> productList = new ArrayList<>();

    private ProductRepository() {
        //  THIS IS THE SINGLE PLACE TO MANAGE YOUR PRODUCTS
        //  To add a new product, just copy one of these lines and change the details.
        //  Make sure your .glb file is in the `app/src/main/assets` folder.

        productList.add(new Product("Modern Sofa", "$899.99", "A comfortable three-seater sofa designed for modern living rooms. Perfect for AR placement.", 4.7f, 214, R.drawable.ic_launcher_background, "models/sofa.glb", "images/sofa_thumb.png"));
        productList.add(new Product("Wooden Coffee Table", "$249.99", "A solid oak coffee table with a minimalistic design. Great to preview for size and fit in AR.", 4.5f, 156, R.drawable.ic_launcher_background, "models/coffee_table.glb", "images/coffee_table_thumb.png"));
        productList.add(new Product("Ergonomic Office Chair", "$329.99", "A fully adjustable ergonomic chair. Use AR to see how it fits your workspace.", 4.6f, 178, R.drawable.ic_launcher_background, "models/office_chair.glb", "images/office_chair_thumb.png"));
        productList.add(new Product("Bookshelf – 5 Tier", "$199.99", "A tall, space-efficient bookshelf. AR helps you check height clearance and wall fit.", 4.4f, 142, R.drawable.ic_launcher_background, "models/bookshelf.glb", "images/bookshelf_thumb.png"));
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public List<Product> getProducts() {
        return productList;
    }
}
