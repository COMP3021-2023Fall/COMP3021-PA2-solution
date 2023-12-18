package hk.ust.comp3021;

import hk.ust.comp3021.constants.*;
import hk.ust.comp3021.action.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MainSystem {
    private final List<Student> students;
    private final List<Activity> activities;
    //TODO: Part 2 Task 2
    private final List<EventHandler> listeners;

    public MainSystem() {
        this.students = new ArrayList<>();
        this.activities = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    // TODO Part 1 Task 2
    public void concurrentRegistration(List<RegistrationAction> actions) {
        AtomicInteger numOfFinishedAction = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        for (int i = 0; i < actions.size(); i++) {
            RegistrationAction currentAction = actions.get(i);
            executor.execute(() -> {
                Activity currentActivity = currentAction.getActivity();
                Student currentStudent = currentAction.getStudent();
                RegistrationActionType currentActionType = currentAction.getAction();
                synchronized (this) {
                    try {
                        while (actions.indexOf(currentAction) != numOfFinishedAction.get()) {
                            wait();
                        }
                        switch (currentActionType) {
                            case ENROLL:
                                boolean results = currentActivity.enroll(currentStudent);
                                if (!results && currentActivity.isFull()) {
                                    RegistrationAction.getWaitList().putIfAbsent(currentActivity, new ArrayList<>());
                                    if (!RegistrationAction.getWaitList().get(currentActivity).contains(currentStudent))
                                        RegistrationAction.getWaitList().get(currentActivity).add(currentStudent);
                                }
                                break;
                            case DROP:
                                if (RegistrationAction.getWaitList().containsKey(currentActivity)) {
                                    RegistrationAction.getWaitList().get(currentActivity).remove(currentStudent);
                                }
                                currentActivity.drop(currentStudent);
                                if (!currentActivity.isFull()){
                                    if (RegistrationAction.getWaitList().containsKey(currentActivity)) {
                                        ArrayList<Student> students = RegistrationAction.getWaitList().get(currentActivity);
                                        if (!students.isEmpty()) {
                                            Student student = students.remove(0);
                                            currentActivity.enroll(student);
                                        }
                                    }
                                }
                                break;
                        }
//                        System.out.println(currentActionType + " " + currentStudent.getStudentID() + " " + currentActivity.getActivityID());
                        currentAction.setCompleted(true);
                        numOfFinishedAction.getAndIncrement();
                        this.notifyAll();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
        while (true) {
            if (executor.isTerminated()) {
                break;
            }
        }
    }


    //TODO: Part 2 Task 2
    public void addListener(EventHandler listener) {
        listeners.add(listener);
    }

    //TODO: Part 2 Task 2
    public void dispatchEvent(Event event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }

    //TODO: Part 2 Task 2
    public void studentGetUpdate(ManagementAction action) {
        switch (action.getAction()) {
            case ACTIVITY_FINISHED:
                action.getActivity().changeState(ActivityState.FINISHED);
                break;
            case ACTIVITY_POSTPONED:
                break;
            case ACTIVITY_CANCELLED:
                action.getActivity().changeState(ActivityState.CANCELLED);
                break;
        }
        dispatchEvent(new ActivityEvent(action.getAction(), action.getActivity()));
    }

    // TODO: Part 3 Task 1
    public Student getStudent(String studentID) {
        return students.stream().filter(s -> s.getStudentID().equals(studentID)).findFirst().orElse(null);
    }

    // TODO: Part 3 Task 1
    public Activity getActivity(String activityID) {
        return activities.stream().filter(a -> a.getActivityID().equals(activityID)).findFirst().orElse(null);
    }

    //TODO: Part 3 Task 2
    public List<Student>  searchStudentByIntegerConditionLambda(QueryAction<Integer> query) {
        QueryActionType action = query.getAction();
        return switch (action) {
            case YEAR_IS ->
                    students.stream().filter(student -> student.getYearOfStudy() == query.getCondition()).collect(Collectors.toList());
            case REMAINING_DURATION_LARGER_THAN ->
                    students.stream().filter(student -> student.getTotalDurationRequired() - student.getPassedDuration() > query.getCondition()).collect(Collectors.toList());
            case REMAINING_DURATION_SMALLER_THAN ->
                    students.stream().filter(student -> student.getTotalDurationRequired() - student.getPassedDuration() < query.getCondition()).collect(Collectors.toList());
            default -> null;
        };
    }

    //TODO Part 3 Task 2
    public List<Activity> searchActivityByStringConditionLambda(QueryAction<String> query){
        return switch (query.getAction()) {
            case ID_CONTAINS ->
                    activities.stream().filter(activity -> activity.getActivityID().contains(query.getCondition())).collect(Collectors.toList());
            case SERVICE_UNIT_IS ->
                    activities.stream().filter(activity -> activity.getServiceUnit().equals(query.getCondition())).collect(Collectors.toList());
            case PREREQUISITE_CONTAINS ->
                    activities.stream().filter(activity -> activity.getTargetStudentDepartments().contains(query.getCondition())).collect(Collectors.toList());
            default -> null;
        };
    }

    //TODO Part 3 Task 2
    public List<Activity> sortActivityByBooleanConditionByLambda(QueryAction<Boolean> query) {
        switch (query.getAction()) {
            case DURATION_CAPACITY:
                // If condition is true, sort in ascending order, else sort in descending order
                if (query.getCondition())
                    return activities.stream().sorted(Comparator.comparing(Activity::getDuration).thenComparing(Activity::getCapacity)).collect(Collectors.toList());
                else
                    return activities.stream().sorted(Comparator.comparing(Activity::getDuration).thenComparing(Activity::getCapacity).reversed()).collect(Collectors.toList());
            case CAPACITY_DURATION:
                if (query.getCondition())
                    return activities.stream().sorted(Comparator.comparing(Activity::getCapacity).thenComparing(Activity::getDuration)).collect(Collectors.toList());
                else
                    return activities.stream().sorted(Comparator.comparing(Activity::getCapacity).thenComparing(Activity::getDuration).reversed()).collect(Collectors.toList());
            default:
                return null;
        }
    }

    public void parseStudents(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String studentID = parts[0];
                int yearOfStudy = Integer.parseInt(parts[1]);
                String department = parts[2];
                int totalDurationRequired = Integer.parseInt(parts[3]);
                Student student = new Student(studentID, yearOfStudy, department, totalDurationRequired);
                students.add(student);
            }
        }
    }

    public void parseActivities(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String activityID = parts[0];
                String serviceUnit = parts[1];
                int duration = Integer.parseInt(parts[2]);
                int capacity = Integer.parseInt(parts[3]);
                List<String> preferences;
                if (parts[4].length() > 2){
                    preferences = Arrays.asList(parts[4].substring(1, parts[4].length() - 1).split(" "));
                }
                else {
                    preferences = new ArrayList<>();
                }
                Activity activity = new Activity(activityID, serviceUnit, preferences, duration, capacity);
                activities.add(activity);
            }
        }
    }

    public void processRegistration(String fileName) throws IOException {
        List<RegistrationAction> actions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String studentID = parts[0];
                Student student = getStudent(studentID);
                String activityCode = parts[1];
                Activity activity = getActivity(activityCode);
                RegistrationActionType actType = RegistrationActionType.valueOf(parts[2]);
                actions.add(new RegistrationAction(student, activity, actType));
            }
        }
        concurrentRegistration(actions);
    }

    // FIXME: Helper functions for testing, remove before release
    public void processRegistrationNoWaitList(String fileName) throws IOException {
        List<RegistrationAction> actions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String studentID = parts[0];
                Student student = getStudent(studentID);
                String activityCode = parts[1];
                Activity activity = getActivity(activityCode);
                RegistrationActionType actType = RegistrationActionType.valueOf(parts[2]);
                actions.add(new RegistrationAction(student, activity, actType));
            }
        }
        actions.forEach(action -> {
            switch (action.getAction()) {
                case ENROLL:
                    boolean results = action.getActivity().enroll(action.getStudent());
                    break;
                case DROP:
                    action.getActivity().drop(action.getStudent());
                    break;
            }
        });
    }

    // FIXME: Helper functions for testing, remove before release
    public void processRegistrationNoParallel(String fileName) throws IOException {
        List<RegistrationAction> actions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String studentID = parts[0];
                Student student = getStudent(studentID);
                String activityCode = parts[1];
                Activity activity = getActivity(activityCode);
                RegistrationActionType actType = RegistrationActionType.valueOf(parts[2]);
                actions.add(new RegistrationAction(student, activity, actType));
            }
        }
        HashMap<Activity, ArrayList<Student>> waitlist = new HashMap<>();
        actions.forEach(action -> {
            switch (action.getAction()) {
                case ENROLL:
                    boolean results = action.getActivity().enroll(action.getStudent());
                    if (!results && action.getActivity().isFull()) {
                        waitlist.putIfAbsent(action.getActivity(), new ArrayList<>());
                        waitlist.get(action.getActivity()).add(action.getStudent());
                    }
                    break;
                case DROP:
                    if (waitlist.containsKey(action.getActivity())) {
                        waitlist.get(action.getActivity()).remove(action.getStudent());
                    }
                    action.getActivity().drop(action.getStudent());
                    if (!action.getActivity().isFull()){
                        if (waitlist.containsKey(action.getActivity())) {
                            ArrayList<Student> students = waitlist.get(action.getActivity());
                            if (!students.isEmpty()) {
                                Student student = students.remove(0);
                                action.getActivity().enroll(student);
                            }
                        }
                    }
                    break;
            }
        });
    }

    // FIXME: Helper functions for testing, remove before release
    public void processRegistrationWithoutDrop(String fileName) throws IOException {
        List<RegistrationAction> actions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String studentID = parts[0];
                Student student = getStudent(studentID);
                String activityCode = parts[1];
                Activity activity = getActivity(activityCode);
                RegistrationActionType actType = RegistrationActionType.valueOf(parts[2]);
                actions.add(new RegistrationAction(student, activity, actType));
            }
        }
        actions.forEach(action -> {
            if (Objects.requireNonNull(action.getAction()) == RegistrationActionType.ENROLL) {
                boolean results = action.getActivity().enroll(action.getStudent());
            }
        });
    }

    // FIXME: Helper functions for testing, remove before release
    public void processRegistrationWithoutMaxCapacity(String fileName) throws IOException {
        List<RegistrationAction> actions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String studentID = parts[0];
                Student student = getStudent(studentID);
                String activityCode = parts[1];
                Activity activity = getActivity(activityCode);
                RegistrationActionType actType = RegistrationActionType.valueOf(parts[2]);
                actions.add(new RegistrationAction(student, activity, actType));
            }
        }
        actions.forEach(action -> {
            if (Objects.requireNonNull(action.getAction()) == RegistrationActionType.ENROLL) {
                if(action.getActivity().getTargetStudentDepartments().isEmpty() || action.getActivity().getTargetStudentDepartments().contains(action.getStudent().getDepartment())){
                    if (!action.getActivity().getRegisteredStudentsList().contains(action.getStudent())) {
                        action.getActivity().getRegisteredStudentsList().add(action.getStudent());
                    }
                }
            } else if (Objects.requireNonNull(action.getAction()) == RegistrationActionType.DROP) {
                action.getActivity().getRegisteredStudentsList().remove(action.getStudent());
            }
        });
    }

    public void processManagement(String fileName) throws IOException {
        List<ManagementAction> actions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String activityCode = parts[0];
                Activity activity = getActivity(activityCode);
                ManagementActionType actType = ManagementActionType.valueOf(parts[1]);
                actions.add(new ManagementAction(activity, actType));
            }
        }
        // You need to have your implementation done to allow the following code to work
        this.students.forEach(this::addListener);
        actions.forEach(this::studentGetUpdate);
    }

    // TODO: Part 3 Task 3
    public void processQuery(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                QueryActionType actType = QueryActionType.valueOf(parts[0]);
                String rawCondition = parts[1];
                // TODO: Start from here
                if (new ArrayList<>(Arrays.asList(QueryActionType.YEAR_IS, QueryActionType.REMAINING_DURATION_LARGER_THAN, QueryActionType.REMAINING_DURATION_SMALLER_THAN)).contains(actType)) {
                    int condition = Integer.parseInt(rawCondition);
                    QueryAction<Integer> query = new QueryAction<>(actType, condition);
                    List<Student> result = searchStudentByIntegerConditionLambda(query);
                    System.out.println(result.stream().map(Student::getStudentID).collect(Collectors.joining(", ")));
                } else if (new ArrayList<>(Arrays.asList(QueryActionType.ID_CONTAINS, QueryActionType.SERVICE_UNIT_IS, QueryActionType.PREREQUISITE_CONTAINS)).contains(actType)) {
                    QueryAction<String> query = new QueryAction<>(actType, rawCondition);
                    List<Activity> result = searchActivityByStringConditionLambda(query);
                    System.out.println(result.stream().map(Activity::getActivityID).collect(Collectors.joining(", ")));
                } else if (new ArrayList<>(Arrays.asList(QueryActionType.DURATION_CAPACITY, QueryActionType.CAPACITY_DURATION)).contains(actType)) {
                    boolean condition = Boolean.parseBoolean(rawCondition);
                    QueryAction<Boolean> query = new QueryAction<>(actType, condition);
                    List<Activity> result = sortActivityByBooleanConditionByLambda(query);
                    System.out.println(result.stream().map(Activity::getActivityID).collect(Collectors.joining(", ")));
                }
            }
        }
    }

    // FIXME: Helper functions for testing, remove before release
    public List<Student> getStudents() {
        return students;
    }

    // FIXME: Helper functions for testing, remove before release
    public List<Activity> getActivities() {
        return activities;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("=============== System Start ===============");
        MainSystem system = new MainSystem();
        system.parseStudents("input/student.txt");
        system.parseActivities("input/activity.txt");
        // Part 1: Parallel Registration
        System.out.println("=============== Part 1 ===============");
        system.processRegistration("input/registrationActions.txt");
        System.out.println("Part 1 Finished");
        // Part 2: Sequential Event Management
        System.out.println("=============== Part 2 ===============");
        system.processManagement("input/managementActions.txt");
        // Part 3: Functional Information Retrieval
        System.out.println("=============== Part 3 ===============");
        system.processQuery("input/queryActions.txt");
    }
}
