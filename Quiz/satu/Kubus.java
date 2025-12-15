
public class Kubus extends BangunRuang {
	private int rusuk;

	public Kubus(int rusuk) {
		this.rusuk = rusuk;
	}

	public int getRusuk() {
		return rusuk;
	}

	public void setRusuk(int rusuk) {
		this.rusuk = rusuk;
	}

	@Override
	public float computeSurfaceArea() {
		return 6 * this.rusuk * this.rusuk;
	}

	@Override
	public float computeVolume() {
		return this.rusuk * this.rusuk * this.rusuk;
	}

}
