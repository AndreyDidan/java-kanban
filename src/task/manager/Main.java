package task.manager;

import task.manager.model.Task;
import task.manager.model.SubTask;
import task.manager.model.Epic;

import task.manager.service.Managers;
import task.manager.service.TaskManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task newTask = new Task("Задача 3", "Описание3");
        Task newTask1 = new Task("Задача 31", "Описание31");
        Epic newEpic = new Epic("Эпик1", "Описание Эпика1");
        Epic newEpic2 = new Epic("Эпик2", "Описание эпика 2");
        SubTask newSubTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", 2);
        SubTask newSubTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 2);
        SubTask newSubTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 2);
        taskManager.addTask(newTask);
        taskManager.addTask(newTask1);
        taskManager.addEpic(newEpic);
        taskManager.addEpic(newEpic2);
        taskManager.addSubTask(newSubTask1);
        taskManager.addSubTask(newSubTask2);
        taskManager.addSubTask(newSubTask3);

        taskManager.getTaskId(0);
        taskManager.getTaskId(1);
        taskManager.getTaskId(0);
        taskManager.getEpicId(2);
        taskManager.getSubTaskId(4);
        taskManager.getSubTaskId(5);
        taskManager.getSubTaskId(6);
        taskManager.getSubTaskId(5);
        taskManager.getTaskId(0);
        taskManager.getEpicId(3);
        taskManager.getEpicId(2);

        taskManager.deleteTask(1);
        taskManager.deleteEpic(2);

        List<Task> history = taskManager.getHistory();
        System.out.println(history);


    }
}
