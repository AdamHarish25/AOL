package tiga;

public class ViewProductList {
	public void view(ProductList productList) {
		int totalData = productList.getProductList().size();
		System.out.println("ProductId - Name - Price");
		for (int i = 0; i < totalData; i++) {
			Product product = productList.getProduct(i);
			System.out.printf("%s - %s - %d\n", product.getProductID(), product.getName(), product.getPrice());
		}
	}
}
