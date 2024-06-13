import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import task.manager.service.FileBackedTaskManager;
import task.manager.service.Managers;
import task.manager.service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.io.File;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("resources/file.csv");
        TaskManager taskManager = Managers.getFileBackedTaskManager(file);
        Task newTask0 = new Task("Задача 0", "Описание0");
        taskManager.addTask(newTask0);



        Epic newEpic1 = new Epic("Эпик 1", "Описаание эпика 1");
        taskManager.addEpic(newEpic1);
        SubTask newSubTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 1);
        taskManager.addSubTask(newSubTask2);
        Task newTask3 = new Task("Задача 3", "Описание3");
        taskManager.addTask(newTask3);

        taskManager.getTaskId(0);
        taskManager.getEpicId(1);
        taskManager.getSubTaskId(2);

        List<Task> history = taskManager.getHistory();
        System.out.println(history);


        TaskManager taskManagerLoad = FileBackedTaskManager.loadFromFile(file);
        Task newTask4 = new Task("Задача 4", "Описание4");
        taskManagerLoad.addTask(newTask4);

        Task newTask5 = new Task("Задача 5", "Описание 5");
        taskManagerLoad.addTask(newTask5);

        Epic newEpic6 = new Epic("Эпик 6", "Описаание эпика 6");
        taskManagerLoad.addEpic(newEpic6);
        SubTask newSubTask7 = new SubTask("Подзадача 7", "Описание подзадачи 7", 6);
        taskManagerLoad.addSubTask(newSubTask7);


        taskManagerLoad.getTaskId(0);
        taskManagerLoad.getEpicId(1);
        taskManagerLoad.getSubTaskId(2);
        taskManagerLoad.getTaskId(3);

        List<Task> history1 = taskManagerLoad.getHistory();
        System.out.println(history1);
    }
}