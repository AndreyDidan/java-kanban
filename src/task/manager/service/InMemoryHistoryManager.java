package task.manager.service;

import task.manager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> history = new ArrayList<>();
    static final int HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == HISTORY_SIZE) {
                history.removeFirst();
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}