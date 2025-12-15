public class Manager extends PermanentEmployee {
	private static final float KPI_GRADE_A_MIN = 3.8f;
	private static final float KPI_GRADE_B_MIN = 3.3f;
	private static final float KPI_GRADE_C_MIN = 2.8f;

	@Override
	public int getBonus() {
		if (kpi > KPI_GRADE_A_MIN)
			return 3 * gaji;
		else if (kpi > KPI_GRADE_B_MIN)
			return 2 * gaji;
		else if (kpi > KPI_GRADE_C_MIN)
			return 1 * gaji;
		else
			return 0;
	}
}