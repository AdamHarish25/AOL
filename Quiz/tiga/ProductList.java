package tiga;

import java.util.ArrayList;
import java.util.List;

public class ProductList {
	private List<Product> productList = new ArrayList<>();
	private static final int MAX_PRODUCT_LIMIT = 100;

	public void addProduct(Product product) {
		if (productList.size() >= MAX_PRODUCT_LIMIT) {
			throw new IllegalStateException("Product list has exceeded the limit");
		}
		productList.add(product);
	}

	public List<Product> getProductList() {
		return productList;
	}

	public Product getProduct(int idx) {
		return productList.get(idx);
	}
}
