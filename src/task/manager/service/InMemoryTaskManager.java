package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.model.StateTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected int id;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.id = 1;
    }


    private int generateNewId() {
        return id++;
    }

    //Добавление задач
    @Override
    public Task addTask(Task newTask) {
        newTask.setId(generateNewId());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateNewId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
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
    protected void changeEpicState(Epic epic) {

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
    @Override
    public void updateTask(Task updateTask) {
        if (tasks.containsKey(updateTask.getId())) {
            tasks.put(updateTask.getId(), updateTask);
        }
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        if (subTasks.containsKey(updateSubTask.getId())) {
            subTasks.put(updateSubTask.getId(), updateSubTask);
            changeEpicState(epics.get(updateSubTask.getIdEpic()));
        }
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        if (epics.containsKey(updateEpic.getId())) {
            epics.get(updateEpic.getId()).setName(updateEpic.getName());
            epics.get(updateEpic.getId()).setDescription(updateEpic.getDescription());
        }
    }

    //Удаление по идентификатору
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        if (epics.containsKey(id)) {
            subTasks.remove(id);
        }
        Epic epic = epics.get(subTasks.get(id).getIdEpic());
        epic.deleteSubTask(id);
        changeEpicState(epics.get(epic.getId()));
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (Integer subTaskId : epics.get(id).getSubTasksInEpic()) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    // удаление всех задач
    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        for (Integer subTaskId : subTasks.keySet()) {
            historyManager.remove(subTaskId);
        }
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubTask() {
        for (Epic epic : epics.values()) {
            epic.deleteAllSubTask();
            changeEpicState(epic);
        }
        for (Integer subTaskId : subTasks.keySet()) {
            historyManager.remove(subTaskId);
        }
        subTasks.clear();
    }

    // Получение задач по идентификатору
    @Override
    public Task getTaskId(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicId(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskId(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    // получение списка задач n-го эпика
    @Override
    public ArrayList<SubTask> getSubTasksInEpic(int idEpic) {
        ArrayList<SubTask> subTasksInEpic = new ArrayList<>();
        for (Integer subtaskId : epics.get(idEpic).getSubTasksInEpic()) {
            subTasksInEpic.add(subTasks.get(subtaskId));
        }
        return subTasksInEpic;
    }

    // получение списка всех задач
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}