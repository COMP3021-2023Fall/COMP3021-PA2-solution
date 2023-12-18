package hk.ust.comp3021.action;

import hk.ust.comp3021.constants.*;

/**
 * TODO: Part 3 Task 1
 * @param <T>
 */
public class QueryAction<T> {
    private final T condition;
    private final QueryActionType action;

    public QueryAction(QueryActionType action, T condition) {
        this.action = action;
        this.condition = condition;
    }

    public T getCondition() {
        return condition;
    }

    public QueryActionType getAction() {
        return action;
    }
}
