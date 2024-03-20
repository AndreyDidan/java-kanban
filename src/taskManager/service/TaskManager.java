package taskManager.service;
import taskManager.model.Task;
import taskManager.model.SubTask;
import taskManager.model.StateTask;
import taskManager.model.Epic;
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

    private int id = 0;

    private int generateNewId() {
        return id++;
    }

    //Добавление задач
    public Task addTask(Task newTask) {
        newTask.setId(generateNewId());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateNewId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public SubTask addSubTask(SubTask newSubTask) {

        if (epics.containsKey(newSubTask.getIdEpic())) {
            newSubTask.setId(generateNewId());

            Epic epic = epics.get(newSubTask.getIdEpic());
            epic.addSubTask(newSubTask.getId());

            subTasks.put(newSubTask.getId(), newSubTask);
            changeEpicState(epics.get(newSubTask.getIdEpic()));
        }
        return newSubTask;
    }

    //Метод изменения статуса эпика
    private void changeEpicState(Epic epic) {

        boolean isNew = false;
        boolean isInProgress = false;
        boolean isDone = false;

        for (Integer id : epic.getSubTasksInEpic()) {
            switch (subTasks.get(id).getStateTask()) {
                case NEW:
                    isNew = true;
                    break;
                case IN_PROGRESS:
                    isInProgress = true;
                    break;
                case DONE:
                    isDone = true;
                    break;
            }
        }

        if (epic.getSubTasksInEpic().isEmpty() || (!isInProgress && !isDone)) {
            epic.setStateTask(StateTask.NEW);
        } else if (!isNew && !isInProgress) {
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
        if (subTasks.containsKey(updateSubTask.getId())) {
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
            subTasks.remove(id);
        }

        Epic epic = epics.get(subTasks.get(id).getIdEpic());
        epic.deleteSubTask(id);
        changeEpicState(epics.get(epic.getId()));
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (Integer subTaskId : epics.get(id).getSubTasksInEpic()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(id);
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
            subTasksInEpic.add(subTasks.get(subtaskId));
        }
        return subTasksInEpic;
    }

    // получение списка всех задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
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
