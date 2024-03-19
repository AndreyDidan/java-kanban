import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();


    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }


    //Добавление задач
    public Task addTask(Task newTask) {
        newTask.setId(IdGenerator.generateNewId());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public Epic addEpic(Epic newEpic) {
        newEpic.setId(IdGenerator.generateNewId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public SubTask addSubTask(SubTask newSubTask) {

        if (epics.containsKey(newSubTask.getIdEpic())) {
            newSubTask.setId(IdGenerator.generateNewId());

            Epic epic = epics.get(newSubTask.getIdEpic());
            epic.addSubTask(newSubTask.getId());

            subTasks.put(newSubTask.getId(), newSubTask);
            changeEpicState(epics.get(newSubTask.getIdEpic()));
        }
        return newSubTask;
    }

    //Метод изменения статуса эпика
    private void changeEpicState(Epic epic) {

        boolean isNEW = false;
        boolean isIN_PROGRESS = false;
        boolean isDONE = false;

        for (Integer id : epic.getSubTasksInEpic()) {
            switch (subTasks.get(id).getStateTask()) {
                case NEW:
                    isNEW = true;
                    break;
                case IN_PROGRESS:
                    isIN_PROGRESS = true;
                    break;
                case DONE:
                    isDONE = true;
                    break;
            }
        }

        if (epic.getSubTasksInEpic().isEmpty() || (!isIN_PROGRESS && !isDONE)) {
            epic.setStateTask(StateTask.NEW);
        } else if (!isNEW && !isIN_PROGRESS) {
            epic.setStateTask(StateTask.DONE);
        } else {
            epic.setStateTask(StateTask.IN_PROGRESS);
        }
    }

    // обновление задач
    public void updateTask(Task updateTask) {
        if (tasks.containsKey(updateTask.getId())) {
            tasks.put(updateTask.getId(), updateTask);
        }
    }

    public void updateSubTask(SubTask updateSubTask) {
        if ((subTasks.containsKey(updateSubTask.getId())) &&
                (subTasks.get(updateSubTask.getId()).getIdEpic() == updateSubTask.getIdEpic())) {
            subTasks.put(updateSubTask.getId(), updateSubTask);
            changeEpicState(epics.get(updateSubTask.getIdEpic()));
        }
    }

    public void updateEpic(Epic updateEpic) {
        if (epics.containsKey(updateEpic.getId())) {
            epics.get(updateEpic.getId()).setName(updateEpic.getName());
            epics.get(updateEpic.getId()).setDescription(updateEpic.getDescription());
        }
    }

    //Удаление по идентификатору
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubTask(int id) {
        if (epics.containsKey(id)) {
            if (subTasks.containsKey(id)) {
                subTasks.remove(id);
            }


            Epic epic = epics.get(subTasks.get(id).getIdEpic());
            epic.deleteSubTask(id);
            changeEpicState(epics.get(epic.getId()));
        }
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (Integer subTaskId : epics.get(id).getSubTasksInEpic())
                subTasks.remove(subTaskId);
            epics.remove(Integer.valueOf(id));
        }
    }

    // удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    public void deleteAllSubTask() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubTask();
            changeEpicState(epic);
        }
        subTasks.clear();
    }

    // Получение задач по идентификатору
    public Task getTaskId(int id) {
        return tasks.get(id);
    }

    public Epic getEpicId(int id) {
        return epics.get(id);
    }

    public SubTask getSubTaskId(int id) {
        return subTasks.get(id);
    }

    // получение списка задач n-го эпика
    public ArrayList<SubTask> getSubTasksInEpic(int idEpic) {
        ArrayList<SubTask> subTasksInEpic = new ArrayList<>();
        for (Integer subtaskId : epics.get(idEpic).getSubTasksInEpic()) {
            if(subTasks.containsKey(subtaskId)) {
                subTasksInEpic.add(subTasks.get(subtaskId));
            }
        }
        return subTasksInEpic;
    }

    // получение списка всех задач
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(tasks.get(task.getId()));
        }
        return allTasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            allEpics.add(epics.get(epic.getId()));
        }
        return allEpics;
    }

    public ArrayList<SubTask> getAllSubtasks() {
        ArrayList<SubTask> allSubtasks = new ArrayList<>();
        for (SubTask subtask : subTasks.values()) {
            allSubtasks.add(subTasks.get(subtask.getId()));
        }
        return allSubtasks;
    }
}