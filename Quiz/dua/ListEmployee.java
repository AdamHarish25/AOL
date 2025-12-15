import java.util.ArrayList;
import java.util.List;

public class ListEmployee {
	private List<Employee> list = new ArrayList<>();
	private static final int MAX_EMPLOYEES = 100;

	public List<Employee> getEmployeeList() {
		return list;
	}

	public void addEmployee(Employee emp) throws Exception {
		if (list.size() >= MAX_EMPLOYEES) {
			throw new Exception("Employee list has exceeded the limit");
		}
		list.add(emp);
	}

	public void viewEmployeeList() {
		for (Employee emp : list) {
			System.out.printf("%s - %s\n", emp.getNip(), emp.getNama());
		}
	}
}