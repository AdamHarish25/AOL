
public class Persegi implements BangunDatar {
	private int sisi;

	public Persegi() {
	}

	public Persegi(int sisi) {
		this.sisi = sisi;
	}

	public int getSisi() {
		return sisi;
	}

	public void setSisi(int sisi) {
		this.sisi = sisi;
	}

	@Override
	public float computeArea() {
		return this.sisi * this.sisi;
	}

	@Override
	public float computeAround() {
		return 4 * this.sisi;
	}

}
