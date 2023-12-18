import hk.ust.comp3021.Activity;
import hk.ust.comp3021.MainSystem;
import hk.ust.comp3021.Student;
import hk.ust.comp3021.action.ManagementAction;
import hk.ust.comp3021.constants.ActivityState;
import hk.ust.comp3021.constants.ManagementActionType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPart2 {

    public static PrintStream out;

    @BeforeAll
    public static void redirect() throws FileNotFoundException {
        out = System.out;
        System.setOut(new PrintStream("output/testPart2.txt"));
    }

    @AfterAll
    public static void back() {
        System.setOut(out);
    }

    @Test
    public void TestDispatcher() {
        Activity activity1 = new Activity("A1", "CEI", new ArrayList<>(Arrays.asList("CSE", "ECE")), 3, 5);
        Activity activity2 = new Activity("A2", "CEI", new ArrayList<>(Arrays.asList("CHEM", "CSE")), 3, 5);
        Activity activity3 = new Activity("A3", "CEI", new ArrayList<>(Arrays.asList("CHEM", "ECE")), 3, 5);

        Student student1 = new Student("s1", 1, "CSE", 12);
        Student student2 = new Student("s2", 2, "CHEM", 10);

        student1.registerActivity(activity1);
        student1.registerActivity(activity2);
        student2.registerActivity(activity2);
        student2.registerActivity(activity3);
        activity1.addStudent(student1);
        activity2.addStudent(student1);
        activity2.addStudent(student2);
        activity3.addStudent(student2);


        MainSystem activityManager = new MainSystem();
        activityManager.addActivity(activity1);
        activityManager.addActivity(activity2);
        activityManager.addActivity(activity3);
        activityManager.addListener(student1);
        activityManager.addListener(student2);

        System.out.println("===================Test 1===================");
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_POSTPONED));
        activityManager.studentGetUpdate(new ManagementAction(activity2, ManagementActionType.ACTIVITY_FINISHED));
        activityManager.studentGetUpdate(new ManagementAction(activity3, ManagementActionType.ACTIVITY_CANCELLED));
    }

    @Test
    public void testFinished() {
        Activity activity1 = new Activity("A1", "CEI", new ArrayList<>(), 3, 5);
        Activity activity2 = new Activity("A2", "CEI", new ArrayList<>(), 3, 5);

        Student student1 = new Student("s1", 1, "CSE", 12);
        Student student2 = new Student("s2", 2, "CHEM", 10);

        student1.registerActivity(activity1);
        student1.registerActivity(activity2);
        student2.registerActivity(activity2);
        student2.registerActivity(activity1);
        activity1.addStudent(student1);
        activity1.addStudent(student2);
        activity2.addStudent(student2);
        activity2.addStudent(student1);


        MainSystem activityManager = new MainSystem();
        activityManager.addActivity(activity1);
        activityManager.addActivity(activity2);
        activityManager.addListener(student1);
        activityManager.addListener(student2);

        System.out.println("===================Test 2===================");
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_FINISHED));
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_FINISHED));
        activityManager.studentGetUpdate(new ManagementAction(activity2, ManagementActionType.ACTIVITY_FINISHED));
        activityManager.studentGetUpdate(new ManagementAction(activity2, ManagementActionType.ACTIVITY_POSTPONED));
        activityManager.studentGetUpdate(new ManagementAction(activity2, ManagementActionType.ACTIVITY_CANCELLED));

        assertEquals(ActivityState.FINISHED, activity1.getState());
        assertEquals(ActivityState.FINISHED, activity2.getState());
        assertEquals(6, student1.getPassedDuration());
        assertEquals(4, student2.getTotalDurationRequired() - student2.getPassedDuration());
    }

    @Test
    public void testCancelled() {
        Activity activity1 = new Activity("A1", "CEI", new ArrayList<>(), 3, 5);
        Activity activity2 = new Activity("A2", "CEI", new ArrayList<>(), 3, 5);

        Student student1 = new Student("s1", 1, "CSE", 12);
        Student student2 = new Student("s2", 2, "CHEM", 10);

        student1.registerActivity(activity1);
        student1.registerActivity(activity2);
        student2.registerActivity(activity2);
        student2.registerActivity(activity1);
        activity1.addStudent(student1);
        activity1.addStudent(student2);
        activity2.addStudent(student2);
        activity2.addStudent(student1);

        MainSystem activityManager = new MainSystem();
        activityManager.addActivity(activity1);
        activityManager.addActivity(activity2);
        activityManager.addListener(student1);
        activityManager.addListener(student2);

        System.out.println("===================Test 3===================");
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_CANCELLED));
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_CANCELLED));
        activityManager.studentGetUpdate(new ManagementAction(activity2, ManagementActionType.ACTIVITY_CANCELLED));
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_FINISHED));
        activityManager.studentGetUpdate(new ManagementAction(activity2, ManagementActionType.ACTIVITY_POSTPONED));

        assertEquals(ActivityState.CANCELLED, activity1.getState());
        assertEquals(ActivityState.CANCELLED, activity2.getState());
        assertEquals(0, student1.getPassedDuration());
        assertEquals(10, student2.getTotalDurationRequired() - student2.getPassedDuration());
    }

    @Test
    public void testPostponed() {
        Activity activity1 = new Activity("A1", "CEI", new ArrayList<>(), 3, 5);
        Activity activity2 = new Activity("A2", "CEI", new ArrayList<>(), 3, 5);

        Student student1 = new Student("s1", 1, "CSE", 12);
        Student student2 = new Student("s2", 2, "CHEM", 10);

        student1.registerActivity(activity1);
        student1.registerActivity(activity2);
        student2.registerActivity(activity2);
        student2.registerActivity(activity1);
        activity1.addStudent(student1);
        activity1.addStudent(student2);
        activity2.addStudent(student2);
        activity2.addStudent(student1);

        MainSystem activityManager = new MainSystem();
        activityManager.addListener(student1);
        activityManager.addListener(student2);
        activityManager.addActivity(activity1);
        activityManager.addActivity(activity2);

        System.out.println("===================Test 4===================");
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_POSTPONED));
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_POSTPONED));
        activityManager.studentGetUpdate(new ManagementAction(activity1, ManagementActionType.ACTIVITY_FINISHED));
        activityManager.studentGetUpdate(new ManagementAction(activity2, ManagementActionType.ACTIVITY_POSTPONED));
        activityManager.studentGetUpdate(new ManagementAction(activity2, ManagementActionType.ACTIVITY_CANCELLED));

        assertEquals(ActivityState.FINISHED, activity1.getState()) ;
        assertEquals(ActivityState.CANCELLED, activity2.getState());
        assertEquals(3, student1.getPassedDuration());
        assertEquals(7, student2.getTotalDurationRequired() - student2.getPassedDuration());
    }
}
