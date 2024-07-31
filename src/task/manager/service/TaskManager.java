package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //Добавление задач
    Task addTask(Task newTask);

    Epic addEpic(Epic newEpic);

    SubTask addSubTask(SubTask newSubTask);

    // обновление задач
    void updateTask(Task updateTask);

    void updateSubTask(SubTask updateSubTask);

    void updateEpic(Epic updateEpic);

    //Удаление по идентификатору
    void deleteTask(Integer id);

    void deleteSubTask(Integer id);

    void deleteEpic(Integer id);

    // удаление всех задач
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTask();

    // Получение задач по идентификатору
    Task getTaskId(Integer id);

    Epic getEpicId(Integer id);

    SubTask getSubTaskId(Integer id);

    // получение списка задач n-го эпика
    ArrayList<SubTask> getSubTasksInEpic(Integer idEpic);

    // получение списка всех задач
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<SubTask> getAllSubtasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTask();

    boolean isCheckTaskTime(Task task);
}