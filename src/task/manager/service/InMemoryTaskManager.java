package task.manager.service;

import exception.NotFoundException;
import exception.ValidationException;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.model.StateTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int id;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, SubTask> subTasks;
    protected HashMap<Integer, Epic> epics;
    protected HistoryManager historyManager;

    TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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

    @Override
    public TreeSet<Task> getPrioritizedTask() {
        return new TreeSet<>(prioritizedTasks);
    }

    //Добавление задач
    @Override
    public Task addTask(Task newTask) {
        newTask.setId(generateNewId());
        tasks.put(newTask.getId(), newTask);
        if (newTask.getStartTime() != null) {
            if (isTaskTimeValid(newTask)) {
                if (!isCheckTaskTime(newTask)) {
                    prioritizedTasks.add(newTask);
                } else throw new ValidationException("Задача пересекается по времени");
            }
        }
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
            if (newSubTask.getStartTime() != null) {
                if (isTaskTimeValid(newSubTask)) {
                    if (!isCheckTaskTime(newSubTask)) {
                        prioritizedTasks.add(newSubTask);
                    } else throw new ValidationException("Подзадача пересекается по времени");
                }
            }
            updateTimeAndDurationEpic(epic);
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
        } else {
            throw new NotFoundException("Задача не найдена, Task id=" + updateTask.getId());
        }
        if (updateTask.getStartTime() != null) {
            if (isTaskTimeValid(updateTask)) {
                if (!isCheckTaskTime(updateTask)) {
                    prioritizedTasks.add(updateTask);
                }
            }
        }
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        if (subTasks.containsKey(updateSubTask.getId())) {
            subTasks.put(updateSubTask.getId(), updateSubTask);
            changeEpicState(epics.get(updateSubTask.getIdEpic()));
        } else {
            throw new NotFoundException("Подзадача не найдена, SubTask id=" + updateSubTask.getId());
        }
        if (updateSubTask.getStartTime() != null) {
            if (isTaskTimeValid(updateSubTask)) {
                if (!isCheckTaskTime(updateSubTask)) {
                    prioritizedTasks.add(updateSubTask);
                }
            }
            updateTimeAndDurationEpic(epics.get(updateSubTask.getIdEpic()));
        }
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        if (epics.containsKey(updateEpic.getId())) {
            epics.get(updateEpic.getId()).setName(updateEpic.getName());
            epics.get(updateEpic.getId()).setDescription(updateEpic.getDescription());
        }
    }

    protected void updateTimeAndDurationEpic(Epic epic) {
        List<SubTask> subTasksInEpic = getSubTasksInEpic(epic.getId());
        for (int i = 0; i < subTasksInEpic.size(); i++) {
            if (subTasksInEpic.get(i).getDuration() != null) {
                if (!subTasksInEpic.isEmpty()) {

                    Duration epicDuration = subTasksInEpic.stream()
                            .map(Task::getDuration)
                            .reduce(Duration.ZERO, Duration::plus);

                    epic.setDuration(epicDuration);

                    Optional<SubTask> earliestSubTask = subTasksInEpic.stream()
                            .min(Comparator.comparing(SubTask::getStartTime));

                    if (earliestSubTask.isPresent()) {
                        LocalDateTime earliestSubTaskStartTime = earliestSubTask.get().getStartTime();
                        epic.setStartTime(earliestSubTaskStartTime);
                    }

                    Optional<SubTask> latestSubTask = subTasksInEpic.stream()
                            .max(Comparator.comparing(SubTask::getEndTime));

                    if (latestSubTask.isPresent()) {
                        LocalDateTime latestSubTaskEndTime = latestSubTask.get().getEndTime();
                        epic.setEndTime(latestSubTaskEndTime);
                    }

                    /*Duration epicDuration = Duration.between(epic.getEndTime(), epic.getEndTime());
                    epic.setDuration(epicDuration);*/

                } else {
                    epic.setStartTime(null);
                    epic.setDuration(null);
                    epic.setEndTime(null);
                }
            }
        }
    }

    // проверка на пересечение времени задач
    private boolean isCheckTaskTime(Task newTask) {
        LocalDateTime newStartInstant = newTask.getStartTime();
        LocalDateTime newEndInstant = newTask.getEndTime();
        boolean tasksCollide =  prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(task -> (newStartInstant.isBefore(task.getStartTime())
                        && newEndInstant.isAfter(task.getStartTime())) || (task.getStartTime().isBefore(newStartInstant)
                        && task.getEndTime().isAfter(newStartInstant)));
        if (tasksCollide) throw new ValidationException("Время задач пересекается!");
        return tasksCollide;
    }

    //метод для валидации времени в таске
    private boolean isTaskTimeValid(Task task) {
        return task.getDuration() != null && task.getStartTime() != null && task.getDuration().toMinutes() != 0;
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