package task.manager.model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasksInEpic = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubTasksInEpic() {
        return subTasksInEpic;
    }

    public void addSubTask(int id) {
        subTasksInEpic.add(Integer.valueOf(id));
    }

    public void deleteSubTask(int id) {
        subTasksInEpic.remove(Integer.valueOf(id));
    }

    public void deleteAllSubTask() {
        subTasksInEpic.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public Integer getIdEpic() {
        return null;
    }
}