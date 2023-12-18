package hk.ust.comp3021;

import java.util.*;

public class Student implements EventHandler {
    private final String StudentID;
    private final int YearOfStudy;
    private final String department;
    private final int totalDurationRequired;
    private int passedDuration;
    private final List<Activity> registeredActivities;
    private final List<Activity> finishedActivities;

    public Student(String studentID, int yearOfStudy, String department, int totalDurationRequired) {
        this.StudentID = studentID;
        this.YearOfStudy = yearOfStudy;
        this.registeredActivities = new ArrayList<>();
        this.finishedActivities = new ArrayList<>();
        this.department = department;
        this.totalDurationRequired = totalDurationRequired;
        this.passedDuration = 0;
    }

    public String getStudentID() {
        return StudentID;
    }

    public int getYearOfStudy() {
        return YearOfStudy;
    }

    public String getDepartment() {
        return department;
    }

    public int getTotalDurationRequired() {
        return totalDurationRequired;
    }

    public int getPassedDuration() {
        return passedDuration;
    }

    /**The check of repeated registration would be in Activity Class, not this class
     * So this method only do simple registration
    * */
    public void registerActivity(Activity a){
        registeredActivities.add(a);
    }

    public void dropActivity(Activity a){
        registeredActivities.remove(a);
    }

    // TODO: Part 2 Task 1
    @Override
    public void onEvent(Event event) {
        if (event instanceof ActivityEvent activityEvent) {
            Activity activity = activityEvent.getActivity();
            switch (activityEvent.getType()) {
                case ACTIVITY_CANCELLED:
                    if(registeredActivities.contains(activity)) {
                        registeredActivities.remove(activity);
                        System.out.println("Student " + StudentID + " is notified that Activity " + activity.getActivityID() + " has cancelled.");
                    }
                    break;
                case ACTIVITY_FINISHED:
                    if(registeredActivities.contains(activity)) {
                        registeredActivities.remove(activity);
                        finishedActivities.add(activity);
                        passedDuration += activity.getDuration();
                        System.out.println("Student " + StudentID + " is notified that Activity " + activity.getActivityID() + " has finished.");
                    }
                    break;
                case ACTIVITY_POSTPONED:
                    if(registeredActivities.contains(activity)) {
                        System.out.println("Student " + StudentID + " is notified that Activity " + activity.getActivityID() + " has postponed.");
                    }
                    break;
            }
        }
    }

    // FIXME: Helper functions for testing, remove before release
    public String toString(){
        return String.format("Student ID: %s, Year of Study: %d, Department: %s, Total Duration Required: %d, Passed Duration: %d",
                StudentID, YearOfStudy, department, totalDurationRequired, passedDuration);
    }

    // FIXME: Helper functions for testing, remove before release
    public List<Activity> getRegisteredActivities() {
        return registeredActivities;
    }

    // FIXME: Helper functions for testing, remove before release
    public void increasePassedDuration(int duration){
        passedDuration += duration;
    }

    // FIXME: Helper functions for testing, remove before release
    public List<Activity> getFinishedActivities() {
        return finishedActivities;
    }
}
