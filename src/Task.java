import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private StateTask stateTask;


    public Task(int id, String name, String description, StateTask stateTask) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stateTask = stateTask;
    }

    public Task(String name, String description, StateTask stateTask) {
        this.name = name;
        this.description = description;
        this.stateTask = stateTask;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(stateTask, task.stateTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, stateTask);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stateTask='" + stateTask + '\'' +
                '}';
    }
}
