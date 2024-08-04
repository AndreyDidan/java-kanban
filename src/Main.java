import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import task.manager.service.FileBackedTaskManager;
import task.manager.service.Managers;
import task.manager.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        SubTask newSubTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 2, LocalDateTime.now().plus(Duration.ofHours(2)),
                Duration.ofMinutes(10));
        taskManager.addSubTask(newSubTask2);
        Task newTask3 = new Task("Задача 3", "Описание3", LocalDateTime.now().plus(Duration.ofHours(1)),
                Duration.ofMinutes(10));
        taskManager.addTask(newTask3);

        taskManager.getTaskId(1);
        taskManager.getEpicId(2);
        taskManager.getSubTaskId(3);

        List<Task> history = taskManager.getHistory();
        System.out.println(history);


        TaskManager taskManagerLoad = FileBackedTaskManager.loadFromFile(file);
        Task newTask4 = new Task("Задача 4", "Описание4");
        taskManagerLoad.addTask(newTask4);

        Task newTask5 = new Task("Задача 5", "Описание 5");
        taskManagerLoad.addTask(newTask5);

        Epic newEpic6 = new Epic("Эпик 6", "Описаание эпика 6");
        taskManagerLoad.addEpic(newEpic6);
        SubTask newSubTask7 = new SubTask("Подзадача 7", "Описание подзадачи 7", 7);
        taskManagerLoad.addSubTask(newSubTask7);
        Task newTask8 = new Task("Задача 8", "Описание8", LocalDateTime.now(), Duration.ofMinutes(10));
        taskManagerLoad.addTask(newTask8);
        Epic newEpic9 = new Epic("Эпик 9", "Описаание эпика 9");
        taskManagerLoad.addEpic(newEpic9);
        SubTask newSubTask10 = new SubTask("Подзадача 10", "Описание подзадачи 10", 10,
                LocalDateTime.now().plus(Duration.ofHours(5)), Duration.ofMinutes(30));
        taskManagerLoad.addSubTask(newSubTask10);
        SubTask newSubTask11 = new SubTask("Подзадача 11", "Описание подзадачи 11", 10,
                LocalDateTime.now().plus(Duration.ofHours(6)), Duration.ofMinutes(30));
        taskManagerLoad.addSubTask(newSubTask11);
        taskManagerLoad.getTaskId(1);
        taskManagerLoad.getEpicId(2);
        taskManagerLoad.getSubTaskId(3);
        taskManagerLoad.getTaskId(4);

        List<Task> history1 = taskManagerLoad.getHistory();
        System.out.println(history1);
        System.out.println(taskManagerLoad.getPrioritizedTask());

        ArrayList<SubTask> podzad = taskManagerLoad.getAllSubtasks();
        System.out.println(podzad);
        taskManagerLoad.deleteSubTask(3);
        System.out.println(taskManagerLoad.getAllSubtasks());
    }
}