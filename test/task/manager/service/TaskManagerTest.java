package task.manager.service;

import task.manager.model.Epic;
import task.manager.model.StateTask;
import task.manager.model.SubTask;
import task.manager.model.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected abstract T createTaskManager();
    protected T taskManager = createTaskManager();

    @Test
    void getAllTasks_ShouldReturnAllAddedTasks() {
        Task newTask1 = new Task("Задача 1", "Описание 1",
                LocalDateTime.of(2024, 7, 2, 1, 0), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 1_1", "Описание 1_1",
                LocalDateTime.of(2024, 7, 2, 1, 15), Duration.ofMinutes(15));
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        List<Task> allTasks = taskManager.getAllTasks();

        Assertions.assertEquals(newTask1, allTasks.get(0));
        Assertions.assertEquals(newTask2, allTasks.get(1));
    }

    @Test
    void deleteAllTasks_ShouldClearMapFromAllTasks() {
        Task newTask1 = new Task(1, "Задача 2", "Описание 2", StateTask.NEW,
                LocalDateTime.of(2024, 5, 2, 0, 30), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 2_1", "Описание 2_1",
                LocalDateTime.of(2024, 5, 2, 0, 45), Duration.ofMinutes(15));
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        taskManager.deleteAllTasks();
        List<Task> allTasks = taskManager.getAllTasks();

        Assertions.assertEquals(0, allTasks.size());
    }

    @Test
    void getTask_ShouldReturnTask() {
        Task expected = new Task(1, "Задача 3", "Описание 3", StateTask.NEW,
                LocalDateTime.of(2024, 7, 3, 1, 0), Duration.ofMinutes(15));
        Task newTask = new Task("Задача 3", "Описание 3",
                LocalDateTime.of(2024, 7, 3, 1, 15), Duration.ofMinutes(15));
        taskManager.addTask(newTask);

        Task actual = taskManager.getTaskId(1);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getTask_ShouldShouldSaveTaskToHistory() {
        Task newTask = new Task("Задача 4", "Описание 4",
                LocalDateTime.of(2024, 5, 2, 1, 30), Duration.ofMinutes(15));
        taskManager.addTask(newTask);

        taskManager.getTaskId(1);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newTask, history.getFirst());
    }

    @Test
    void addTask_ShouldGenerateIdAndSaveTask() {
        Task expected = new Task(1,"Задача 5", "Описание 5", StateTask.NEW,
                LocalDateTime.of(2024, 5, 2, 1, 45), Duration.ofMinutes(15));
        Task newTask = new Task("Задача 5", "Описание 5",
                LocalDateTime.of(2024, 5, 2, 2, 0), Duration.ofMinutes(15));

        taskManager.addTask(newTask);

        Task actual = taskManager.getTaskId(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateTask_UpdatedTaskShouldHaveSameId() {
        Task expected = new Task(1,"Задача 6_1", "Описание 6_1", StateTask.NEW,
                LocalDateTime.of(2024, 5, 2, 2, 15), Duration.ofMinutes(15));
        Task newTask = new Task("Задача 6", "Описание 6",
                LocalDateTime.of(2024, 5, 2, 2, 30), Duration.ofMinutes(15));
        taskManager.addTask(newTask);
        newTask.setName("Задача 6_1");
        newTask.setDescription("Описание 6_1");

        taskManager.updateTask(newTask);

        Assertions.assertEquals(1, newTask.getId());
        Assertions.assertEquals(expected, newTask);
    }

    @Test
    void deleteTask_ShouldRemoveTaskById() {
        Task newTask1 = new Task("Задача 7", "Описание 7",
                LocalDateTime.of(2024, 5, 2, 2, 45), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 7_1", "Описание 7_1",
                LocalDateTime.of(2024, 5, 2, 3, 0), Duration.ofMinutes(15));
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        taskManager.deleteTask(1);

        List<Task> allTasks = taskManager.getAllTasks();
        Assertions.assertEquals(1, allTasks.size());
        Assertions.assertEquals(newTask2, allTasks.getFirst());
    }

    @Test
    void addTask_ShouldRewriteSetIdWhenAdded() {
        Task newTask1 = new Task(1, "Задача 8", "Описание 8", StateTask.NEW,
                LocalDateTime.of(2024, 5, 2, 3, 15), Duration.ofMinutes(15));
        Task newTask2 = new Task(2, "Задача 9", "Описание 9", StateTask.NEW,
                LocalDateTime.of(2024, 5, 2, 3, 30), Duration.ofMinutes(15));

        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        Assertions.assertEquals(1, newTask1.getId());
        Assertions.assertEquals(2, newTask2.getId());
    }

    @Test
    void deleteAllEpics_ShouldDeleteAllSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        SubTask subTask1 = new SubTask("Подзадача 1_1", "Описание 1_1", 0,
                LocalDateTime.of(2024, 5, 2, 3, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 1_2", "Описание 1_2", 1,
                LocalDateTime.of(2024, 5, 2, 4, 0), Duration.ofMinutes(15));
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteAllEpics();

        List<Epic> allEpics = taskManager.getAllEpics();
        List<SubTask> allSubtasks = taskManager.getAllSubtasks();
        Assertions.assertEquals(0, allEpics.size());
        Assertions.assertEquals(0, allSubtasks.size());
    }

    @Test
    void getEpic_ShouldSaveEpicToHistory() {
        Epic newEpic = new Epic("Эпик 3", "Описание 3");
        taskManager.addEpic(newEpic);

        taskManager.getEpicId(1);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newEpic, history.getFirst());
    }

    @Test
    void addSubtask_SubtasksShouldHaveEpicId() {
        Epic newEpic = new Epic("Эпик 6", "Описание 6");
        taskManager.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 3_1", "Описание 3_1", 1,
                LocalDateTime.of(2024, 5, 2, 4, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(newSubTask);

        Assertions.assertEquals(newEpic.getId(), newSubTask.getIdEpic());
    }

    @Test
    void deleteAllSubTasks_ShouldClearSubTasksArrayInAllEpics() {
        Epic epic1 = new Epic("Эпик 4", "Описание 4");
        Epic epic2 = new Epic("Эпик 5", "Описание 5");
        SubTask subTask1 = new SubTask("Подзадача 2_1", "Описание 2_1", 0,
                LocalDateTime.of(2024, 5, 1, 0, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 2_2", "Описание 2_2", 1,
                LocalDateTime.of(2024, 5, 1, 0, 45), Duration.ofMinutes(15));
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteAllSubTask();

        List<Integer> epic1Array = epic1.getSubTasksInEpic();
        List<Integer> epic2Array = epic2.getSubTasksInEpic();

        assertEquals(0, epic1Array.size());
        assertEquals(0, epic2Array.size());
    }

    @Test
    void getSubtask_ShouldSaveSubtaskToHistory() {
        Epic newEpic = new Epic("Эпик 6", "Описание 6");
        taskManager.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 3_1", "Описание 3_1", 1,
                LocalDateTime.of(2024, 5, 2, 4, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(newSubTask);

        taskManager.getSubTaskId(2);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(newSubTask, history.getFirst());
    }

    @Test
    void deleteSubtask_epicStatusShouldBeChangedWhenSubtasksAreDeleted() {
        Epic epic1 = new Epic("Эпик 9", "Описание 9");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 7", "Описание 7", 1,
                LocalDateTime.of(2024, 5, 2, 5, 0), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 8", "Описание 8", 1,
                LocalDateTime.of(2024, 5, 2, 5, 16), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask("Подзадача 8", "Описание 8", StateTask.DONE, 1, 3);
        subTask2.setStateTask(StateTask.DONE);
        taskManager.updateSubTask(subTask3);

        taskManager.deleteSubTask(subTask1.getId());

        Assertions.assertEquals(StateTask.DONE, epic1.getStateTask());
    }

    @Test
    void epicStatusShouldBeDONEWhenSubtasksStatusesDONE() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 1,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 1,
                LocalDateTime.of(2024, 5, 2, 5, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        SubTask subTask3 = new SubTask("Подзадача 5", "Описание 5", StateTask.DONE, 1, 2);
        SubTask subTask4 = new SubTask("Подзадача 6", "Описание 6", StateTask.DONE, 1, 3);
        taskManager.updateSubTask(subTask3);
        taskManager.updateSubTask(subTask4);

        Assertions.assertEquals(StateTask.DONE, epic1.getStateTask());
    }

    @Test
    void epicStatusShouldBeNEWWhenSubtasksStatusesNEW() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 1,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 1,
                LocalDateTime.of(2024, 5, 2, 5, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        SubTask subTask3 = new SubTask("Подзадача 5", "Описание 5", StateTask.NEW, 1, 2);
        SubTask subTask4 = new SubTask("Подзадача 6", "Описание 6", StateTask.NEW, 1, 3);
        taskManager.updateSubTask(subTask3);
        taskManager.updateSubTask(subTask4);

        Assertions.assertEquals(StateTask.NEW, epic1.getStateTask());
    }

    @Test
    void epicStatusShouldBeIN_PROGRESSWhenSubtasksStatusesNEWandDONE() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 1,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 1,
                LocalDateTime.of(2024, 5, 2, 5, 45), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        SubTask subTask3 = new SubTask("Подзадача 5", "Описание 5", StateTask.NEW, 1, 2);
        SubTask subTask4 = new SubTask("Подзадача 6", "Описание 6", StateTask.DONE, 1, 3);

        taskManager.updateSubTask(subTask3);
        taskManager.updateSubTask(subTask4);

        Assertions.assertEquals(StateTask.IN_PROGRESS, epic1.getStateTask());
    }

    @Test
    void epicStatusShouldBeIN_PROGRESSWhenSubtasksStatusesIN_PROGRESS() {
        Epic epic1 = new Epic("Эпик 8", "Описание 8");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 5", "Описание 5", 1,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 6", "Описание 6", 1,
                LocalDateTime.of(2024, 5, 2, 5, 46), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        SubTask subTask3 = new SubTask("Подзадача 5", "Описание 5", StateTask.IN_PROGRESS, 1, 2,
                LocalDateTime.of(2024, 5, 2, 5, 30), Duration.ofMinutes(15));
        SubTask subTask4 = new SubTask("Подзадача 6", "Описание 6", StateTask.IN_PROGRESS, 1, 3,
                LocalDateTime.of(2024, 5, 2, 5, 46), Duration.ofMinutes(15));
        taskManager.updateSubTask(subTask3);
        taskManager.updateSubTask(subTask4);

        Assertions.assertEquals(StateTask.IN_PROGRESS, epic1.getStateTask());
    }

    @Test
    void deleteSubtask_shouldRemoveSubtaskIdFromEpic() {
        Epic epic1 = new Epic("Эпик 9", "Описание 9");
        SubTask subTask1 = new SubTask("Подзадача 7", "Описание 7", 1,
                LocalDateTime.of(2024, 5, 2, 6, 0), Duration.ofMinutes(15));
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1);

        taskManager.deleteSubTask(2);

        List<Integer> epic1Array = epic1.getSubTasksInEpic();
        assertEquals(0, epic1Array.size());
    }

    @Test
    void deleteTask_ShouldRemoveTaskFromHistory() {
        Task newTask1 = new Task("Задача 10", "Описание 10",
                LocalDateTime.of(2024, 5, 2, 6, 15), Duration.ofMinutes(15));
        Task newTask2 = new Task("Задача 11", "Описание 11",
                LocalDateTime.of(2024, 5, 2, 6, 30), Duration.ofMinutes(15));
        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);
        taskManager.getTaskId(newTask1.getId());
        taskManager.getTaskId(newTask2.getId());

        taskManager.deleteTask(newTask1.getId());

        List<Task> tasksInHistory = taskManager.getHistory();
        Assertions.assertEquals(1, tasksInHistory.size());
        Assertions.assertEquals(newTask2, tasksInHistory.getFirst());
    }

    @Test
    void deleteEpic_ShouldDeleteEpicAndItsSubTasksFromHistory() {
        Epic epic1 = new Epic("Эпик 10", "Описание 10");
        SubTask subTask1 = new SubTask("Подзадача 8", "Описание 8", 1,
                LocalDateTime.of(2024, 5, 2, 7, 15), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 9", "Описание 9", 2,
                LocalDateTime.of(2024, 5, 2, 7, 30), Duration.ofMinutes(15));
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.getEpicId(epic1.getId());
        taskManager.getSubTaskId(subTask1.getId());
        taskManager.getSubTaskId(subTask2.getId());

        taskManager.deleteEpic(epic1.getId());

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void deleteALLEpics_ShouldDeleteAllEpicsAndItsSubTasksFromHistory() {
        Epic epic1 = new Epic("Эпик 11", "Описание 11");
        Epic epic2 = new Epic("Эпик 12", "Описание 12");
        Epic epic3 = new Epic("Эпик 13", "Описание 13");
        SubTask subTask1 = new SubTask("Подзадача 10", "Описание 10", 1,
                LocalDateTime.of(2024, 5, 2, 7, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 11", "Описание 11", 2,
                LocalDateTime.of(2024, 5, 2, 8, 0), Duration.ofMinutes(15));
        SubTask subTask3 = new SubTask("Подзадача 12", "Описание 12", 2,
                LocalDateTime.of(2024, 5, 2, 8, 15), Duration.ofMinutes(15));
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.getEpicId(epic1.getId());
        taskManager.getEpicId(epic2.getId());
        taskManager.getEpicId(epic3.getId());
        taskManager.getSubTaskId(subTask1.getId());
        taskManager.getSubTaskId(subTask2.getId());
        taskManager.getSubTaskId(subTask3.getId());

        taskManager.deleteAllEpics();

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void deleteSubtask_ShouldDeleteSubTaskFromHistory() {
        Epic epic1 = new Epic("Эпик 14", "Описание 14");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 13", "Описание 13", 1,
                LocalDateTime.of(2024, 5, 2, 8, 30), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.getSubTaskId(subTask1.getId());

        taskManager.deleteSubTask(subTask1.getId());

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void deleteAllSubTasks_ShouldDeleteAllSubTasksFromHistory() {
        Epic epic1 = new Epic("Эпик 15", "Описание 15");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 14", "Описание 14", 1,
                LocalDateTime.of(2024, 5, 2, 8, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 15", "Описание 15", 1,
                LocalDateTime.of(2024, 5, 2, 9, 0), Duration.ofMinutes(15));
        SubTask subTask3 = new SubTask("Подзадача 16", "Описание 16", 1,
                LocalDateTime.of(2024, 5, 2, 9, 15), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.getSubTaskId(subTask1.getId());
        taskManager.getSubTaskId(subTask2.getId());
        taskManager.getSubTaskId(subTask3.getId());

        taskManager.deleteAllSubTask();

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    void getSubtasksFromEpic_shouldReturnListOfSubTasksInEpic() {
        Epic epic = new Epic("Эпик 18", "Описание 18");
        SubTask subTask1 = new SubTask("Подзадача 20", "Описание 20", 1,
                LocalDateTime.of(2024, 5, 2, 10, 45), Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Подзадача 21", "Описание 21", 1,
                LocalDateTime.of(2024, 5, 2, 11, 0), Duration.ofMinutes(15));
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        List<SubTask> subTasksInEpic = taskManager.getSubTasksInEpic(epic.getId());
        assertEquals(2, subTasksInEpic.size());
        assertEquals(subTask1, subTasksInEpic.getFirst());
        assertEquals(subTask2, subTasksInEpic.getLast());
    }

    @Test
    void getPrioritizedTasks_shouldSortTasksInChronologicalOrder() {
        Task taskPriority1 = new Task("Задача 12", "Описание 12",
                LocalDateTime.of(2024, 5, 2, 9, 30), Duration.ofMinutes(15));
        Epic epic = new Epic("Эпик 16", "Описание 16");
        SubTask subTaskPriority3 = new SubTask("Подзадача 17", "Описание 17", 2,
                LocalDateTime.of(2024, 5, 2, 10, 0), Duration.ofMinutes(15));
        SubTask subTaskPriority2 = new SubTask("Подзадача 18", "Описание 18", 2,
                LocalDateTime.of(2024, 5, 2, 9, 45), Duration.ofMinutes(15));
        taskManager.addTask(taskPriority1);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTaskPriority3);
        taskManager.addSubTask(subTaskPriority2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTask();

        assertEquals(3, prioritizedTasks.size());
        assertEquals(taskPriority1, prioritizedTasks.getFirst());
        assertEquals(subTaskPriority3, prioritizedTasks.getLast());
    }


    @Test
    void getPrioritizedTasks_shouldNotIncludeTaskWithoutStartTimeAndDuration() {
        Task taskPriority1 = new Task("Задача 13", "Описание 13",
                LocalDateTime.of(2024, 5, 2, 10, 15), Duration.ofMinutes(15));
        Task taskWithoutTime = new Task("Задача 14", "Описание 14");
        Epic epic = new Epic("Эпик 17", "Описание 17");
        SubTask subTaskPriority2 = new SubTask("Подзадача 19", "Описание 19", 3,
                LocalDateTime.of(2024, 5, 2, 10, 30), Duration.ofMinutes(15));

        taskManager.addTask(taskPriority1);
        taskManager.addTask(taskWithoutTime);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTaskPriority2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTask();

        assertEquals(2, prioritizedTasks.size());
        assertEquals(taskPriority1, prioritizedTasks.getFirst());
        assertEquals(subTaskPriority2, prioritizedTasks.getLast());
    }

    @Test
    void addTaskAndGenerateId() {

        Task expected = new Task(1, "Задача 1", "Описание", StateTask.NEW);
        Task newTask = new Task("Задача 1", "Описание");

        taskManager.addTask(newTask);

        Task actual = taskManager.getTaskId(1);
        assertEquals(expected, actual);
    }

    @Test
    void addSubTaskAndGenerateId() {

        Epic newEpic = new Epic("Эпик1", "Описание эпика");
        SubTask expected = new SubTask("Задача 2", "Описание", StateTask.NEW, 1, 2);
        SubTask newSubTask = new SubTask("Задача 2", "Описание", 1);

        taskManager.addEpic(newEpic);
        taskManager.addSubTask(newSubTask);

        SubTask actual = taskManager.getSubTaskId(2);
        assertEquals(expected, actual);
    }

    @Test
    void getTask_shouldReturnTask() {

        Task expected = new Task(1, "Задача 4", "Описание4", StateTask.NEW);
        Task newTask = new Task("Задача 4", "Описание4");
        taskManager.addTask(newTask);

        Task actual = taskManager.getTaskId(1);

        assertEquals(expected, actual);
    }

    @Test
    void getSubTask_shouldReturnSubTask() {

        Epic newEpic = new Epic("Эпик22", "Описание эпика2");
        SubTask expected = new SubTask("Задача 33", "Описание33", StateTask.NEW, 1, 2);
        SubTask newSubTask = new SubTask("Задача 33", "Описание33", 1);
        taskManager.addEpic(newEpic);
        taskManager.addSubTask(newSubTask);

        SubTask actual = taskManager.getSubTaskId(2);

        assertEquals(expected, actual);
    }

    @Test
    void getEpic_shouldReturnEpic() {

        Epic newEpic = new Epic("Эпик44", "Описание эпика44");
        taskManager.addEpic(newEpic);
        Epic newEpic2 = new Epic("Эпик55", "Описание эпика55");
        taskManager.addEpic(newEpic2);
        SubTask newSubTask = new SubTask("Задача 66", "Описание66", 1);
        taskManager.addSubTask(newSubTask);


        Epic actual1 = taskManager.getEpicId(1);
        Epic actual2 = taskManager.getEpicId(2);

        assertEquals(newEpic, actual1);
        assertEquals(newEpic2, actual2);
    }

    @Test
    void getTaskId_shouldSaveToHistory() {

        Task expected = new Task(2, "Задача 31", "Описание31", StateTask.NEW);
        Task newTask = new Task("Задача 3", "Описание3");
        Task newTask1 = new Task("Задача 31", "Описание31");
        taskManager.addTask(newTask);
        taskManager.addTask(newTask1);

        taskManager.getTaskId(1);
        taskManager.getTaskId(2);
        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(newTask1, history.get(1));
    }

    @Test
    void getSubTaskId_shouldSaveToHistory() {

        Epic newEpic = new Epic("Эпик22", "Описание эпика2");
        SubTask expected = new SubTask("Задача 33", "Описание33", StateTask.NEW, 1, 2);
        SubTask newSubTask = new SubTask("Задача 33", "Описание33", 1);
        taskManager.addEpic(newEpic);
        taskManager.addSubTask(newSubTask);

        taskManager.getEpicId(1);
        taskManager.getSubTaskId(2);
        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(expected, history.get(1));
    }

    @Test
    void addTaskAndUpdadeTask() {
        Task task1 = new Task("Задача 5", "Описание5");
        taskManager.addTask(task1);
        Task task2 = new Task(1, "Задача 5", "Описание5", StateTask.IN_PROGRESS);
        taskManager.updateTask(task2);

        Task expected = taskManager.getTaskId(1);

        assertEquals(expected, task2);
    }

    @Test
    void addSubTaskAndUpdateSubTask(){
        Epic epic1 = new Epic("Первый эпик", "Описание эпика1");
        taskManager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Первая подзадача эпика", "Описание подзадачи", 1);
        taskManager.addSubTask(subTask1);
        SubTask subTask3 = new SubTask("Первая подзадача", "Описание подзадачи", StateTask.IN_PROGRESS, 1, 2);
        taskManager.updateSubTask(subTask3);

        SubTask expected = taskManager.getSubTaskId(2);

        assertEquals(expected, subTask3);
    }

    @Test
    void addTaskAndUpdateTaskInHistoryManager(){

        Task newTask = new Task("Задача6", "Описание6");
        taskManager.addTask(newTask);
        taskManager.getTaskId(1);
        Task newTask2 = new Task(1, "Задача 7", "Описание15", StateTask.IN_PROGRESS);
        taskManager.updateTask(newTask2);
        taskManager.getTaskId(1);

        List<Task> history = taskManager.getHistory();
        int coin = history.size();

        assertEquals(1, coin);
        assertEquals(newTask2, history.get(0));
    }

    @Test
    void addEpicAndGenerateId() {

        Epic epic = new Epic("Задача 8", "Описание8");
        taskManager.addEpic(epic);

        Epic actual = taskManager.getEpicId(1);

        Assertions.assertNotNull(actual);
    }

    @Test
    void addSubTaskInSubTask() {

        Epic newEpic = new Epic("Эпик3", "Описание эпика");
        SubTask subTask1 = new SubTask("Подзадача3", "Описание3", StateTask.NEW, 1, 2);
        SubTask subTask2 = new SubTask("Подзадача 4", "Описание4", 2);

        taskManager.addEpic(newEpic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        SubTask actual = taskManager.getSubTaskId(2);
        assertEquals(subTask1, actual);
    }

    @Test
    void addTwoTaskAndDeleteOneTask () {
        Task expected = null;
        Task newTask11 = new Task("Задача 11", "Описание11");
        Task newTask12 = new Task("Задача 12", "Описание12");
        taskManager.addTask(newTask11);
        taskManager.addTask(newTask12);

        taskManager.deleteTask(1);

        Task actual = taskManager.getTaskId(2);
        Assertions.assertNotNull(actual);
    }

    @Test
    void addTwoSubTaskAndDeleteOneSubTask () {
        SubTask expected = null;
        Epic newEpic11 = new Epic("Эпик1", "Описание эпика");
        taskManager.addEpic(newEpic11);
        SubTask newSubTask11 = new SubTask("Подзадача 11", "Описание111",1);
        taskManager.addSubTask(newSubTask11);
        SubTask newSubTask12 = new SubTask("Подзадача 112", "Описание112", 1);
        taskManager.addSubTask(newSubTask12);

        taskManager.deleteSubTask(2);
        SubTask actual = taskManager.getSubTaskId(2);

        Assertions.assertNotEquals(expected, actual);
    }

    @Test
    void addTaskAndRemoveAllTaskInHistoryManager(){

        Task newTask = new Task("Задача6", "Описание6");
        taskManager.addTask(newTask);
        taskManager.getTaskId(1);
        Task newTask2 = new Task(1, "Задача 7", "Описание15", StateTask.IN_PROGRESS);
        taskManager.addTask(newTask2);
        taskManager.getTaskId(2);

        taskManager.deleteAllTasks();
        List<Task> history = taskManager.getHistory();
        int coin = 0;

        assertEquals(coin, history.size());
    }

    @Test
    void addEpicAndRemoveAllEpicInHistoryManager(){

        Epic newEpic = new Epic("Эпик2", "Описание эпика2");
        taskManager.addEpic(newEpic);
        taskManager.getEpicId(1);
        Epic newEpic2 = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(newEpic2);
        taskManager.getEpicId(2);
        SubTask newSubTask1 = new SubTask("Подзадача 0", "Описание подзадачи 0", 1);
        taskManager.addSubTask(newSubTask1);
        taskManager.getSubTaskId(3);

        taskManager.deleteAllEpics();
        List<Task> history = taskManager.getHistory();
        int coin = 0;

        assertEquals(coin, history.size());
    }

    @Test
    void addSubTaskAndRemoveAllEpicInHistoryManager(){

        Epic newEpic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(newEpic);
        SubTask newSubTask = new SubTask("Подзадача 01", "Описание подзадачи 01", 1);
        taskManager.addSubTask(newSubTask);
        taskManager.getSubTaskId(2);
        SubTask newSubTask1 = new SubTask("Подзадача 02", "Описание подзадачи 02", 1);
        taskManager.addSubTask(newSubTask1);
        taskManager.getSubTaskId(3);

        taskManager.deleteAllSubTask();
        List<Task> history = taskManager.getHistory();
        int coin = 0;

        assertEquals(coin, history.size());
    }
}