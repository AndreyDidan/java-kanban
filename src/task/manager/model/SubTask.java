package task.manager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private Integer idEpic;
    //private LocalDateTime endTime;

    public SubTask(String name, String description, Integer idEpic) {
        super(name, description);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, StateTask stateTask, Integer idEpic, Integer id) {
        super(id, name, description, stateTask);
        this.idEpic = idEpic;
    }

    public SubTask(String name, String description, Integer idEpic, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.idEpic = idEpic;
        this.setStartTime(startTime);
        this.setDuration(duration);
        super.getEndTime();
    }

    public SubTask(String name, String description, StateTask stateTask, Integer idEpic, Integer id, LocalDateTime startTime,
                   Duration duration) {
        super(id, name, description, stateTask, startTime, duration);
        this.idEpic = idEpic;
        this.setStartTime(startTime);
        this.setDuration(duration);
        super.getEndTime();
    }

    public void setIdEpic(Integer idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return idEpic == subTask.idEpic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), idEpic);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "idEpic=" + idEpic +
                "} " + super.toString();
    }
}