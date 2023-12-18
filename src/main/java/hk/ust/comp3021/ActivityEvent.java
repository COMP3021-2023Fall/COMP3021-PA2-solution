package hk.ust.comp3021;

import hk.ust.comp3021.constants.ManagementActionType;

public class ActivityEvent implements Event {
    private final ManagementActionType type;
    private final Activity activity;

    public ActivityEvent(ManagementActionType type, Activity activity) {
        this.type = type;
        this.activity = activity;
    }

    public ManagementActionType getType() {
        return type;
    }

    public Activity getActivity() {
        return activity;
    }
}
