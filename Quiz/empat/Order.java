
import java.util.ArrayList;
import java.util.List;

public abstract class Order {
	private String orderID;
	private Shipping shipping;
	private List<Item> items = new ArrayList<>();
	private Coordinate origin;
	private Coordinate destination;

	public Order() {
		this.origin = new Coordinate(0, 0);
		this.destination = new Coordinate(0, 0);
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public List<Item> getItems() {
		return items;
	}

	public Item getItem(int idx) {
		return items.get(idx);
	}

	public int getTotalPrice() {
		int totalPrice = 0;
		int shipPrice = shipping.getShippingPrice(this);

		for (Item item : items) {
			totalPrice = totalPrice + (item.getPrice() * item.getQty());
		}

		return totalPrice + shipPrice;
	}

	public double getLonOrigin() {
		return origin.getLongitude();
	}

	public void setLonOrigin(double lonOrigin) {
		this.origin.setLongitude(lonOrigin);
	}

	public double getLatOrigin() {
		return origin.getLatitude();
	}

	public void setLatOrigin(double latOrigin) {
		this.origin.setLatitude(latOrigin);
	}

	public double getLonDestination() {
		return destination.getLongitude();
	}

	public void setLonDestination(double lonDestination) {
		this.destination.setLongitude(lonDestination);
	}

	public double getLatDestination() {
		return destination.getLatitude();
	}

	public void setLatDestination(double latDestination) {
		this.destination.setLatitude(latDestination);
	}

	public Shipping getShipping() {
		return shipping;
	}

	public void setShipping(Shipping shipping) {
		this.shipping = shipping;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
}
