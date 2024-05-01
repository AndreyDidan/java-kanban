package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.StateTask;
import task.manager.model.SubTask;
import task.manager.model.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addTaskAndGenerateId() {

        Task expected = new Task(0, "Задача 1", "Описание", StateTask.NEW);
        Task newTask = new Task("Задача 1", "Описание");

        taskManager.addTask(newTask);

        Task actual = taskManager.getTaskId(0);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void addSubTaskAndGenerateId() {

        Epic newEpic = new Epic("Эпик1", "Описание эпика");
        SubTask expected = new SubTask("Задача 2", "Описание", StateTask.NEW, 0, 1);
        SubTask newSubTask = new SubTask("Задача 2", "Описание", 0);

        taskManager.addEpic(newEpic);
        taskManager.addSubTask(newSubTask);

        SubTask actual = taskManager.getSubTaskId(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getTask_shouldReturnTask() {

        Task expected = new Task(0, "Задача 4", "Описание4", StateTask.NEW);
        Task newTask = new Task("Задача 4", "Описание4");
        taskManager.addTask(newTask);

        Task actual = taskManager.getTaskId(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getSubTask_shouldReturnSubTask() {

        Epic newEpic = new Epic("Эпик22", "Описание эпика2");
        SubTask expected = new SubTask("Задача 33", "Описание33", StateTask.NEW, 0, 1);
        SubTask newSubTask = new SubTask("Задача 33", "Описание33", 0);
        taskManager.addEpic(newEpic);
        taskManager.addSubTask(newSubTask);

        SubTask actual = taskManager.getSubTaskId(1);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getEpic_shouldReturnEpic() {

        Epic newEpic = new Epic("Эпик44", "Описание эпика44");
        Epic expected1 = new Epic("Эпик44", "Описание эпика44");
        taskManager.addEpic(newEpic);
        Epic newEpic2 = new Epic("Эпик55", "Описание эпика55");
        taskManager.addEpic(newEpic2);
        SubTask newSubTask = new SubTask("Задача 66", "Описание66", 1);
        taskManager.addSubTask(newSubTask);


        Epic actual1 = taskManager.getEpicId(0);
        Epic actual2 = taskManager.getEpicId(1);

        Assertions.assertEquals(expected1, actual1);
        Assertions.assertEquals(newEpic2, actual2);
    }

    @Test
    void getTaskId_shouldSaveToHistory() {

        Task expected = new Task(1, "Задача 31", "Описание31", StateTask.NEW);
        Task newTask = new Task("Задача 3", "Описание3");
        Task newTask1 = new Task("Задача 31", "Описание31");
        taskManager.addTask(newTask);
        taskManager.addTask(newTask1);

        taskManager.getTaskId(0);
        taskManager.getTaskId(1);
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(expected, history.get(1));
    }

    @Test
    void getSubTaskId_shouldSaveToHistory() {

        Epic newEpic = new Epic("Эпик22", "Описание эпика2");
        SubTask expected = new SubTask("Задача 33", "Описание33", StateTask.NEW, 0, 1);
        SubTask newSubTask = new SubTask("Задача 33", "Описание33", 0);
        taskManager.addEpic(newEpic);
        taskManager.addSubTask(newSubTask);

        taskManager.getEpicId(0);
        taskManager.getSubTaskId(1);
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(expected, history.get(1));
    }

    @Test
    void addTaskAndUpdadeTask() {
        Task task1 = new Task("Задача 5", "Описание5");
        taskManager.addTask(task1);
        Task task2 = new Task(0, "Задача 5", "Описание5", StateTask.IN_PROGRESS);
        taskManager.updateTask(task2);

        Task expected = taskManager.getTaskId(0);

        Assertions.assertEquals(expected, task2);
    }

    @Test
    void addSubTaskAndUpdateSubTask(){
        Epic epic1 = new Epic("Первый эпик", "Описание эпика1");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Первая подзадача эпика", "Описание подзадачи", 0);
        taskManager.addSubTask(subTask1);
        SubTask subTask3 = new SubTask("Первая подзадача", "Описание подзадачи", StateTask.IN_PROGRESS, 0, 1);
        taskManager.updateSubTask(subTask3);

        SubTask expected = taskManager.getSubTaskId(1);

        Assertions.assertEquals(expected, subTask3);
    }

    @Test
    void addTaskAndUpdateTaskInHistoryManager(){

        Task newTask = new Task("Задача6", "Описание6");
        taskManager.addTask(newTask);
        taskManager.getTaskId(0);
        Task newTask2 = new Task(0, "Задача 7", "Описание15", StateTask.IN_PROGRESS);
        taskManager.updateTask(newTask2);
        taskManager.getTaskId(0);

        List<Task> history = taskManager.getHistory();
        int coin = history.size();

        Assertions.assertEquals(1, coin);
        Assertions.assertEquals(newTask2, history.get(0));
    }

    @Test
    void addEpicAndGenerateId() {

        Epic epic = new Epic("Задача 8", "Описание8");
        taskManager.addEpic(epic);

        Epic actual = taskManager.getEpicId(0);

        Assertions.assertNotNull(actual);
    }

    @Test
    void addSubTaskInSubTask() {

        Epic newEpic = new Epic("Эпик3", "Описание эпика");
        SubTask subTask1 = new SubTask("Подзадача3", "Описание3", StateTask.NEW, 0, 1);
        SubTask subTask2 = new SubTask("Подзадача 4", "Описание4", 1);

        taskManager.addEpic(newEpic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        SubTask actual = taskManager.getSubTaskId(1);
        Assertions.assertEquals(subTask1, actual);
    }

    @Test
    void addTwoTaskAndDeleteOneTask () {
        Task expected = null;
        Task newTask11 = new Task("Задача 11", "Описание11");
        Task newTask12 = new Task("Задача 12", "Описание12");
        taskManager.addTask(newTask11);
        taskManager.addTask(newTask12);

        taskManager.deleteTask(0);

        Task actual = taskManager.getTaskId(1);
        Assertions.assertNotNull(actual);
    }

    @Test
    void addTwoSubTaskAndDeleteOneSubTask () {
        SubTask expected = null;
        Epic newEpic11 = new Epic("Эпик1", "Описание эпика");
        taskManager.addEpic(newEpic11);
        SubTask newSubTask11 = new SubTask("Подзадача 11", "Описание111",0);
        taskManager.addSubTask(newSubTask11);
        SubTask newSubTask12 = new SubTask("Подзадача 112", "Описание112", 0);
        taskManager.addSubTask(newSubTask12);

        taskManager.deleteSubTask(1);
        SubTask actual = taskManager.getSubTaskId(1);

        Assertions.assertNotEquals(expected, actual);
    }

    @Test
    void addTaskAndRemoveAllTaskInHistoryManager(){

        Task newTask = new Task("Задача6", "Описание6");
        taskManager.addTask(newTask);
        taskManager.getTaskId(0);
        Task newTask2 = new Task(0, "Задача 7", "Описание15", StateTask.IN_PROGRESS);
        taskManager.addTask(newTask2);
        taskManager.getTaskId(1);

        taskManager.deleteAllTasks();
        List<Task> history = taskManager.getHistory();
        int coin = 0;

        Assertions.assertEquals(coin, history.size());
    }

    @Test
    void addEpicAndRemoveAllEpicInHistoryManager(){

        Epic newEpic = new Epic("Эпик2", "Описание эпика2");
        taskManager.addEpic(newEpic);
        taskManager.getEpicId(0);
        Epic newEpic2 = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(newEpic2);
        taskManager.getEpicId(1);
        SubTask newSubTask1 = new SubTask("Подзадача 0", "Описание подзадачи 0", 0);
        taskManager.addSubTask(newSubTask1);
        taskManager.getSubTaskId(2);

        taskManager.deleteAllEpics();
        List<Task> history = taskManager.getHistory();
        int coin = 0;

        Assertions.assertEquals(coin, history.size());
    }

    @Test
    void addSubTaskAndRemoveAllEpicInHistoryManager(){

        Epic newEpic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 01", "Описание подзадачи 01", 0);
        taskManager.addSubTask(newSubTask);
        taskManager.getSubTaskId(1);
        SubTask newSubTask1 = new SubTask("Подзадача 02", "Описание подзадачи 02", 0);
        taskManager.addSubTask(newSubTask1);
        taskManager.getSubTaskId(2);

        taskManager.deleteAllSubTask();
        List<Task> history = taskManager.getHistory();
        int coin = 0;

        Assertions.assertEquals(coin, history.size());
    }
}