package task.manager;

import task.manager.model.Task;

import task.manager.service.Managers;
import task.manager.service.TaskManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task newTask = new Task("Задача 3", "Описание3");
        Task newTask1 = new Task("Задача 31", "Описание31");
        Task newTask2 = new Task("Задача 32", "Описание32");
        taskManager.addTask(newTask);
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        taskManager.getTaskId(0);
        taskManager.getTaskId(1);
        taskManager.getTaskId(2);
        taskManager.getTaskId(1);

        List<Task> history = taskManager.getHistory();
        System.out.println(history);

    }
}