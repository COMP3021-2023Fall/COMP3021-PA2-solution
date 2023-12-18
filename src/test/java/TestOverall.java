import hk.ust.comp3021.Activity;
import hk.ust.comp3021.MainSystem;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class TestOverall {
    public static void writeActivityEnrollment(String fileName, List<Activity> activities) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Activity activity : activities) {
                bw.write(activity.getActivityID() + ", " +
                        activity.getServiceUnit() + ", " +
                        activity.getDuration() + ", " +
                        activity.getCapacity() + ", " +
                        "[" + String.join(" ", activity.getTargetStudentDepartments())+ "]" + ", [" +
                        String.join(" ", activity.getRegisteredStudentsID()) + "]"
                );
                bw.newLine();
            }
        }
    }

    @Test
    public void TestRunner() throws IOException {
        MainSystem system = new MainSystem();
        system.parseStudents("input/student.txt");
        system.parseActivities("input/activity.txt");
        system.processRegistration("input/registrationActions.txt");
        writeActivityEnrollment("output/testRegistration.txt", system.getActivities());
        // Part 2: Management
        System.out.println("=============== Part2: Management ===============");
        PrintStream out = System.out;
        System.setOut(new PrintStream("output/testManagement.txt"));
        system.processManagement("input/managementActions.txt");
        System.setOut(out);
        // Part 3: Query
        System.out.println("=============== Part3: Query ===============");
        PrintStream out2 = System.out;
        System.setOut(new PrintStream("output/testQuery.txt"));
        system.processQuery("input/queryActions.txt");
        System.setOut(out2);
    }

}
