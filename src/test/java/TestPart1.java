import hk.ust.comp3021.MainSystem;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestPart1 {
    @Test
    public void testRegistration() throws IOException{
        MainSystem system = new MainSystem();
        system.parseStudents("input/hidden/testPart1Student.txt");
        system.parseActivities("input/hidden/testPart1Activity.txt");
        system.processRegistration("input/hidden/testPart1RegistrationActions1.txt");

        assertTrue(system.getActivities().get(1).getRegisteredStudentsID().contains("s1002"));
        assertTrue(system.getActivities().get(1).getRegisteredStudentsID().contains("s1003"));
        assertFalse(system.getActivities().get(1).getRegisteredStudentsID().contains("s1004"));
        assertTrue(system.getActivities().get(0).getRegisteredStudentsID().contains("s1001"));
        assertTrue(system.getActivities().get(0).getRegisteredStudentsID().contains("s1003"));
        assertFalse(system.getActivities().get(0).getRegisteredStudentsID().contains("s1002"));
        assertFalse(system.getActivities().get(0).getRegisteredStudentsID().contains("s1004"));
    }

    @Test
    public void testDrop() throws IOException {
        MainSystem system = new MainSystem();
        system.parseStudents("input/hidden/testPart1Student.txt");
        system.parseActivities("input/hidden/testPart1Activity.txt");
        system.processRegistration("input/hidden/testPart1RegistrationActions2.txt");

        assertEquals(0, system.getActivities().get(1).getRegisteredStudentsID().size());
    }

    @Test
    public void testWaitList() throws IOException {
        MainSystem system = new MainSystem();
        system.parseStudents("input/hidden/testPart1Student.txt");
        system.parseActivities("input/hidden/testPart1Activity.txt");
        system.processRegistration("input/hidden/testPart1RegistrationActions3.txt");

        assertTrue(system.getActivities().get(1).getRegisteredStudentsID().contains("s1001"));
        assertFalse(system.getActivities().get(1).getRegisteredStudentsID().contains("s1002"));
        assertFalse(system.getActivities().get(1).getRegisteredStudentsID().contains("s1003"));
        assertTrue(system.getActivities().get(1).getRegisteredStudentsID().contains("s1004"));
        assertFalse(system.getActivities().get(2).getRegisteredStudentsID().contains("s1001"));
        assertTrue(system.getActivities().get(2).getRegisteredStudentsID().contains("s1003"));
        assertFalse(system.getActivities().get(2).getRegisteredStudentsID().contains("s1002"));
        assertFalse(system.getActivities().get(2).getRegisteredStudentsID().contains("s1004"));
    }
}
