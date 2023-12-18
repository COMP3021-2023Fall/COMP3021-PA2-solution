import hk.ust.comp3021.Activity;
import hk.ust.comp3021.MainSystem;
import hk.ust.comp3021.Student;
import hk.ust.comp3021.action.QueryAction;
import hk.ust.comp3021.constants.QueryActionType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPart3 {
    @Test
    public void testQuery1() throws IOException {
        MainSystem mainSystem = new MainSystem();
        mainSystem.parseActivities("input/hidden/testPart3Activity.txt");
        mainSystem.parseStudents("input/hidden/testPart3Student.txt");

        QueryAction<Integer> query1 = new QueryAction<>(QueryActionType.YEAR_IS, 1);
        QueryAction<Integer> query2 = new QueryAction<>(QueryActionType.REMAINING_DURATION_LARGER_THAN, 10);
        QueryAction<Integer> query3 = new QueryAction<>(QueryActionType.REMAINING_DURATION_SMALLER_THAN, 11);

        Student student1 = mainSystem.getStudent("s1003");
        Student student2 = mainSystem.getStudent("s1010");
//        Student student1 = mainSystem.getStudent.apply("s1003");
//        Student student2 = mainSystem.getStudent.apply("s1010");

        student1.increasePassedDuration(6);
        student2.increasePassedDuration(6);

        assertEquals(7, mainSystem.searchStudentByIntegerConditionLambda(query1).size());
        assertEquals(5, mainSystem.searchStudentByIntegerConditionLambda(query2).size());
        assertEquals(15, mainSystem.searchStudentByIntegerConditionLambda(query3).size());
    }

    @Test
    public void testQuery2() throws IOException {
        MainSystem mainSystem = new MainSystem();
        mainSystem.parseActivities("input/hidden/testPart3Activity.txt");
        mainSystem.parseStudents("input/hidden/testPart3Student.txt");
        QueryAction<String> query4 = new QueryAction<>(QueryActionType.ID_CONTAINS, "SEMINAR");
        QueryAction<String> query5 = new QueryAction<>(QueryActionType.ID_CONTAINS, "105");
        QueryAction<String> query6 = new QueryAction<>(QueryActionType.SERVICE_UNIT_IS, "UGDO");
        QueryAction<String> query7 = new QueryAction<>(QueryActionType.PREREQUISITE_CONTAINS, "IPP");

        assertEquals(2, mainSystem.searchActivityByStringConditionLambda(query4).size());
        assertEquals(3, mainSystem.searchActivityByStringConditionLambda(query5).size());
        assertEquals(3, mainSystem.searchActivityByStringConditionLambda(query6).size());
        assertEquals(3, mainSystem.searchActivityByStringConditionLambda(query7).size());
    }

    @Test
    public void testQuery3() throws IOException {
        MainSystem mainSystem = new MainSystem();
        mainSystem.parseActivities("input/hidden/testPart3Activity.txt");
        mainSystem.parseStudents("input/hidden/testPart3Student.txt");

        QueryAction<Boolean> query7 = new QueryAction<>(QueryActionType.DURATION_CAPACITY, true);
        QueryAction<Boolean> query8 = new QueryAction<>(QueryActionType.DURATION_CAPACITY, false);
        QueryAction<Boolean> query9 = new QueryAction<>(QueryActionType.CAPACITY_DURATION, true);
        QueryAction<Boolean> query10 = new QueryAction<>(QueryActionType.CAPACITY_DURATION, false);

        List<String> query7Result = mainSystem.sortActivityByBooleanConditionByLambda(query7).stream().map(Activity::getActivityID).toList();
        List<String> query8Result = mainSystem.sortActivityByBooleanConditionByLambda(query8).stream().map(Activity::getActivityID).toList();
        List<String> query9Result = mainSystem.sortActivityByBooleanConditionByLambda(query9).stream().map(Activity::getActivityID).toList();
        List<String> query10Result = mainSystem.sortActivityByBooleanConditionByLambda(query10).stream().map(Activity::getActivityID).toList();

        assertEquals(List.of("STUDY_CONSULTATION_105", "VOLUNTEER_TUTORIAL_105", "CAREER_CONSULTATION_104", "VOLUNTEER_WORKSHOP_103", "STUDY_SEMINAR_102", "EXCHANGE_TUTORIAL_105", "EXCHANGE_CONSULTATION_103", "CAREER_SEMINAR_101", "STUDY_TUTORIAL_101", "EXCHANGE_TUTORIAL_104"), query7Result);
        assertEquals(List.of("EXCHANGE_TUTORIAL_104", "STUDY_TUTORIAL_101", "CAREER_SEMINAR_101", "EXCHANGE_CONSULTATION_103", "EXCHANGE_TUTORIAL_105", "STUDY_SEMINAR_102", "VOLUNTEER_WORKSHOP_103", "CAREER_CONSULTATION_104", "VOLUNTEER_TUTORIAL_105", "STUDY_CONSULTATION_105"), query8Result);
        assertEquals(List.of("STUDY_CONSULTATION_105", "VOLUNTEER_TUTORIAL_105", "EXCHANGE_TUTORIAL_105", "CAREER_CONSULTATION_104", "STUDY_TUTORIAL_101", "VOLUNTEER_WORKSHOP_103", "EXCHANGE_CONSULTATION_103", "STUDY_SEMINAR_102", "CAREER_SEMINAR_101", "EXCHANGE_TUTORIAL_104"), query9Result);
        assertEquals(List.of("EXCHANGE_TUTORIAL_104", "CAREER_SEMINAR_101", "STUDY_SEMINAR_102", "EXCHANGE_CONSULTATION_103", "VOLUNTEER_WORKSHOP_103", "STUDY_TUTORIAL_101", "CAREER_CONSULTATION_104", "EXCHANGE_TUTORIAL_105", "VOLUNTEER_TUTORIAL_105", "STUDY_CONSULTATION_105"), query10Result);
    }
}
