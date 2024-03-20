import taskManager.model.Task;
import taskManager.model.SubTask;
import taskManager.model.StateTask;
import taskManager.model.Epic;
import taskManager.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task = new Task("Первая задача", "Описание1", StateTask.NEW);
        taskManager.addTask(task);
        Task task1 = new Task("Вторая задача", "Описание2", StateTask.NEW);
        taskManager.addTask(task1);
        Epic epic = new Epic("Первый эпик", "Описание эпика1");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Первая подзадача", "Описание подзадачи", StateTask.NEW, 2);
        taskManager.addSubTask(subTask);
        Epic epic1 = new Epic("Второй эпик", "Описание эпика2");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Первая подзадача эпика 2", "Описание подзадачи", StateTask.NEW, 4);
        SubTask subTask2 = new SubTask("Вторая подзадача эпика 2", "Описание подзадачи", StateTask.NEW, 4);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        System.out.println("Все созданные списки: ");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("Меняем статусы: ");

        Task task2 = new Task(0,"Первая задача", "Описание1", StateTask.IN_PROGRESS);
        Task task3 = new Task(1,"Вторая задача", "Описание125", StateTask.DONE);
        SubTask subTask3 = new SubTask("Первая подзадача", "Описание подзадачи", StateTask.IN_PROGRESS, 2, 3);
        SubTask subTask4 = new SubTask("ервая подзадача эпика 2", "Описание подзадачи", StateTask.DONE, 4, 5);
        SubTask subTask5 = new SubTask("ервая подзадача эпика 2", "Описание подзадачи", StateTask.DONE, 4, 6);
        taskManager.updateTask(task2);
        taskManager.updateTask(task3);
        taskManager.updateSubTask(subTask3);
        taskManager.updateSubTask(subTask4);
        taskManager.updateSubTask(subTask5);
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("Удаляем Таски: ");

        taskManager.deleteTask(1);
        taskManager.deleteSubTask(6);
        taskManager.deleteEpic(2);
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
    }
}