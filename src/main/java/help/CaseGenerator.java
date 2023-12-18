package help;

import hk.ust.comp3021.*;
import hk.ust.comp3021.action.*;
import hk.ust.comp3021.constants.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaseGenerator {
    public static String[] departments = {"CSE", "ECE", "MAE", "CBE", "ISD", "SHSS", "IPP", "MATH", "PHYS"};
    public static String[] serviceUnits = {"UGDO", "CEI", "DENG"};
    public static String[] activityIDPrefix1 = {"CAREER", "RESEARCH", "STUDY", "INTERNSHIP", "EXCHANGE", "VOLUNTEER"};
    public static String[] activityIDPrefix2 = {"CONSULTATION", "SEMINAR", "WORKSHOP", "TUTORIAL"};
    public static List<String> generatedCourses = new ArrayList<>();

    public static void writeStudents(String fileName, int num, List<Student> students) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            int count = 0;
            Random rn = new Random();
            while ( num - count++ > 0 ) {
                String studentID = "s" + (1000 + count);
                String department = departments[rn.nextInt(departments.length)];
                int yearOfStudy = rn.nextInt(4)+1;
                int totalDurationRequired = rn.nextInt(8, 13);
                Student student = new Student(studentID, yearOfStudy, department, totalDurationRequired);
                students.add(student);

                bw.write(studentID + ", " +
                        yearOfStudy + ", " +
                        department + ", " +
                        String.format("%d", totalDurationRequired)
                );
                bw.newLine();
            }
        }
    }

    public static void writeActivities(String fileName, int num, List<Activity> activities) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            int count = 0;
            Random rn = new Random();
            while ( num - count++ > 0 ) {
                int nCourse = rn.nextInt(activityIDPrefix1.length);
                String courseID1 = activityIDPrefix1[nCourse] + '_' + activityIDPrefix2[rn.nextInt(activityIDPrefix2.length)];
                String courseID = courseID1 + '_' + (100 + rn.nextInt(1, 6));
                generatedCourses.add(courseID);
                String serviceUnit = serviceUnits[rn.nextInt(serviceUnits.length)];

                List<String> targetStudentDepartments = new ArrayList<>();
                boolean useLimit = rn.nextFloat() < 0.3;
//                boolean useLimit = false;
                if (useLimit){
                    int numTarget = rn.nextInt(4, 7);
                    for (int p = 0; p < numTarget; p++) {
                        String randomCourse = departments[rn.nextInt(departments.length)];
                        if (!targetStudentDepartments.contains(randomCourse)){
                            targetStudentDepartments.add(randomCourse);
                        }
                    }
                }

                int duration = rn.nextInt(1, 4);
                int capacity;
                if (courseID.contains("CONSULTATION")) {
                    capacity = 1;
                }
                else if (courseID.contains("WORKSHOP")) {
                    capacity = rn.nextInt(1, 3) * 3;
                }
                else {
                    capacity = rn.nextInt(1, 4) * 4;
                }

                Activity activity = new Activity(courseID, serviceUnit, targetStudentDepartments, duration, capacity);
                activities.add(activity);

                bw.write(courseID + ", " +
                        serviceUnit + ", " +
                        duration + ", " +
                        capacity + ", " +
                        "[" + String.join(" ", targetStudentDepartments)+ "]"
                );
                bw.newLine();
            }
        }
    }

    public static void writeRegistrations(String fileName, int num, List<Student> students, List<Activity> activities) throws IOException {
        List<RegistrationAction> registrationActions = new ArrayList<>();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            int count = 0;
            Random rn = new Random();
            while ( num - count++ > 0 ) {
                Student student = students.get(rn.nextInt(students.size())); // randomly select a student
                Activity activity = activities.get(rn.nextInt(activities.size())); // randomly select an activity

                if (rn.nextFloat() < 0.7 || count == 1){
                    String studentID = student.getStudentID();
                    String activityID = activity.getActivityID();
                    RegistrationActionType actionType = RegistrationActionType.ENROLL;
                    registrationActions.add(new RegistrationAction(student, activity, actionType));
                    bw.write(studentID + ", " +
                            activityID + ", " +
                            actionType.name()
                    );
                } else {
                    RegistrationAction action = registrationActions.get(rn.nextInt(registrationActions.size()));
                    String studentID = action.getStudent().getStudentID();
                    String activityID = action.getActivity().getActivityID();
                    RegistrationActionType actionType = RegistrationActionType.DROP;

                    bw.write(studentID + ", " +
                            activityID + ", " +
                            actionType.name()
                    );
                }


                bw.newLine();
            }
        }
    }

    public static void writeManagements(String fileName, int num, List<Student> students, List<Activity> activities) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            int count = 0;
            Random rn = new Random();
            while ( num - count++ > 0 ) {
                Activity activity = activities.get(rn.nextInt(activities.size())); // randomly select an activity

                ManagementActionType actionType = ManagementActionType.values()[rn.nextInt(ManagementActionType.values().length)];

                bw.write(activity.getActivityID() + ", " +
                        actionType.name()
                );



                bw.newLine();
            }
        }
    }

    public static void writeQuery(String fileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            int count = 0;
            Random rn = new Random();
            QueryActionType[] queryActions = QueryActionType.values();

            bw.write(queryActions[0].name() + ", " + "2");
            bw.newLine();
            bw.write(queryActions[1].name() + ", " + "10");
            bw.newLine();
            bw.write(queryActions[2].name() + ", " + "10");
            bw.newLine();
            bw.write(queryActions[5].name() + ", " + "CONSULTATION");
            bw.newLine();
            bw.write(queryActions[6].name() + ", " + "UGDO");
            bw.newLine();
            bw.write(queryActions[7].name() + ", " + "MAE");
            bw.newLine();
            bw.write(queryActions[3].name() + ", " + "true");
            bw.newLine();
            bw.write(queryActions[3].name() + ", " + "false");
            bw.newLine();
            bw.write(queryActions[4].name() + ", " + "true");
            bw.newLine();
            bw.write(queryActions[4].name() + ", " + "false");
            bw.newLine();
        }
    }

    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<Activity> activities = new ArrayList<>();
        try {
//            CaseGenerator.writeActivities("input/activity.txt", 3, activities);
//            CaseGenerator.writeStudents("input/student.txt", 4, students);
//            CaseGenerator.writeRegistrations("input/registrationActions.txt", 20, students, activities);
            MainSystem system = new MainSystem();
            system.parseStudents("input/student.txt");
            system.parseActivities("input/activity.txt");
            writeManagements("input/managementActions.txt", 10, system.getStudents(), system.getActivities());
            writeQuery("input/queryActions.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

