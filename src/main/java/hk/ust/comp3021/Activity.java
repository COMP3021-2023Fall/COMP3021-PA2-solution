package hk.ust.comp3021;

import hk.ust.comp3021.constants.ActivityState;

import java.util.ArrayList;
import java.util.List;

public class Activity {
    private final String activityID;
    private final String ServiceUnit;
    private final List<String> targetStudentDepartments;
    private final int duration;
    private final int capacity;
    private final ArrayList<Student> registeredStudents;
    private ActivityState state;

    public Activity(String activityID, String ServiceUnit, List<String> targetStudentDepartments,
                    int duration, int capacity) {
        this.activityID = activityID;
        this.ServiceUnit = ServiceUnit;
        this.targetStudentDepartments = targetStudentDepartments;
        this.duration = duration;
        this.capacity = capacity;
        this.registeredStudents = new ArrayList<>();
        this.state = ActivityState.OPEN;
    }

    // TODO: Part 1 Task 1
    public synchronized boolean enroll(Student s){
        if (registeredStudents.contains(s)) {
            return true;
        }
        if (!targetStudentDepartments.isEmpty() && !targetStudentDepartments.contains(s.getDepartment())) {
            return false;
        }
        if (registeredStudents.size() < capacity) {
            registeredStudents.add(s);
            s.registerActivity(this);
            return true;
        }
        return false;
    }

    // TODO: Part 1 Task 1
    public synchronized void drop(Student s){
        if (registeredStudents.contains(s)) {
            registeredStudents.remove(s);
            s.dropActivity(this);
        }
    }

    public String getActivityID() {
        return activityID;
    }

    public String getServiceUnit() {
        return ServiceUnit;
    }

    public List<String> getTargetStudentDepartments() {
        return targetStudentDepartments;
    }

    public int getDuration() {
        return duration;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull(){
        return registeredStudents.size() == capacity;
    }

    // FIXME: Helper functions for testing, remove before release
    public List<String> getRegisteredStudentsID() {
        return registeredStudents.stream().map(Student::getStudentID).toList();
    }

    // FIXME: Helper functions for testing, remove before release
    public List<Student> getRegisteredStudentsList() {
        return registeredStudents;
    }

    public ActivityState getState(){
        return this.state;
    }

    public void changeState(ActivityState s){
        // Only changeable from OPEN to CLOSED
        if (this.state == ActivityState.OPEN){
            this.state = s;
        }
    }

    // FIXME: Helper functions for testing, remove before release
    public String toString(){
        return String.format("Activity ID: %s, Service Unit: %s, Duration: %d, Capacity: %d, State: %s, Target Student Departments: %s",
                activityID, ServiceUnit, duration, capacity, state.toString(), targetStudentDepartments.toString());
    }
}
