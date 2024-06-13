package task.manager;

import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import task.manager.service.Managers;
import task.manager.service.TaskManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        Task newTask = new Task("Задача 3", "Описание3");
        taskManager.addTask(newTask);
        Task newTask1 = new Task("Задача 31", "Описание31");
        taskManager.addTask(newTask1);
        Task newTask2 = new Task("Задача 32", "Описание32");
        taskManager.addTask(newTask2);
        Epic newEpic = new Epic("Эпик 1", "Описаание эпика 1");
        taskManager.addEpic(newEpic);
        Epic newEpic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.addEpic(newEpic2);
        Epic newEpic3 = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(newEpic3);
        SubTask newSubTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", 3);
        taskManager.addSubTask(newSubTask1);
        SubTask newSubTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 3);
        taskManager.addSubTask(newSubTask2);
        SubTask newSubTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 4);
        taskManager.addSubTask(newSubTask3);

        taskManager.getTaskId(0);
        taskManager.getTaskId(1);
        taskManager.getTaskId(2);
        taskManager.getTaskId(1);
        taskManager.getEpicId(3);
        taskManager.getEpicId(4);
        taskManager.getEpicId(5);
        taskManager.getSubTaskId(6);
        taskManager.getSubTaskId(7);
        taskManager.getSubTaskId(8);
        taskManager.getSubTaskId(6);

        taskManager.deleteAllSubTask();
        taskManager.deleteAllEpics();
        taskManager.deleteAllTasks();
        //taskManager.deleteSubTask(7);


        List<Task> history = taskManager.getHistory();
        System.out.println(history);

    }
}