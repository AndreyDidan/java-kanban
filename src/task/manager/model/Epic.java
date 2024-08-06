package task.manager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasksInEpic = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, StateTask stateTask) {
        super(id, name, description, stateTask);
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.setStartTime(startTime);
        this.setDuration(duration);
    }

    public Epic(int id, String name, String description, StateTask stateTask, LocalDateTime startTime, Duration duration) {
        super(id, name, description, stateTask, startTime, duration);
        this.setStartTime(startTime);
        this.setDuration(duration);
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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}