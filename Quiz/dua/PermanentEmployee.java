
public abstract class PermanentEmployee extends Employee {
    protected float kpi;

    public PermanentEmployee(String nip, String nama, String alamat, String email, int gaji) {
        super(nip, nama, alamat, email, gaji);
    }

    public PermanentEmployee() {
        super();
    }

    public void setKPI(float kpi) {
        if (kpi > 4)
            kpi = 4;
        else if (kpi < 0)
            kpi = 0;
        this.kpi = kpi;
    }

    public float getKPI() {
        return kpi;
    }

    @Override
    public void medicalBenefitInfo() {
        System.out.println("Rawat inap : 1.000.000/malam");
        System.out.println("ICU : 2.000.000/malam");
        System.out.println("Medical Checkup : 5.000.000/tahun");
        // and other benefit
    }
}
