public class CleaningService extends Employee {
	private int jamLembur;

	@Override
	public int getBonus() {
		return 0;
	}

	@Override
	public int getGaji() {
		return gaji + getUpahLembur();
	}

	public void setJamLembur(int jamLembur) {
		this.jamLembur = jamLembur;
	}

	public int getJamLembur() {
		return jamLembur;
	}

	public int getUpahLembur() {
		return jamLembur * gaji / 100;
	}

	@Override
	public void medicalBenefitInfo() {
		System.out.println("tidak ada medical benefit");
	}
}