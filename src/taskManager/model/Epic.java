package taskManager.model;

import java.util.ArrayList;
import java.util.Objects;

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

    public void deleteSubTask(int id){
        subTasksInEpic.remove(Integer.valueOf(id));
    }

    public void deleteAllSubTask(){
        subTasksInEpic.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasksInEpic, epic.subTasksInEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksInEpic);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksInEpic=" + subTasksInEpic +
                "} " + super.toString();
    }
}