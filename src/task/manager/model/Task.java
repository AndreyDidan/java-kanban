package task.manager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private StateTask stateTask;

    private LocalDateTime startTime;
    private Duration duration;
    private LocalDateTime endTime;

    public Task(int id, String name, String description, StateTask stateTask) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stateTask = stateTask;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        stateTask = StateTask.NEW;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        stateTask = StateTask.NEW;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration.toMinutes(), ChronoUnit.MINUTES);
    }

    public Task(int id, String name, String description, StateTask stateTask, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stateTask = stateTask;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration.toMinutes(), ChronoUnit.MINUTES);
    }

    public Integer getIdEpic() {
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StateTask getStateTask() {
        return stateTask;
    }

    public void setStateTask(StateTask stateTask) {
        this.stateTask = stateTask;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stateTask=" + stateTask +
                '}';
    }
}