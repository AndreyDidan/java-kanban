package task.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;

    @BeforeEach
    void beforeEach() {
        try {
            Path path = Files.createTempFile("tasks", ".csv");
            file = path.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveToEmptyFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("name", "desc");
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", epic.getId());
        fileBackedTaskManager.addSubTask(subTask);

        fileBackedTaskManager.getTaskId(task.getId());
        fileBackedTaskManager.getEpicId(epic.getId());
        fileBackedTaskManager.getSubTaskId(subTask.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("1,TASK,name,NEW,desc,null", lines.get(1));
            assertEquals("2,EPIC,e,NEW,d,null", lines.get(2));
            assertEquals("3,SUBTASK,s,NEW,d,2", lines.get(3));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadFromFile_empty() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(0, fileBackedTaskManager.getAllTasks().size());
        assertEquals(0, fileBackedTaskManager.getAllEpics().size());
        assertEquals(0, fileBackedTaskManager.getAllSubtasks().size());
        assertEquals(0, fileBackedTaskManager.getHistory().size());
    }

    @Test
    void loadFromFile_filled() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,epic\n" +
                    "1,TASK,name,NEW,desc\n" +
                    "2,EPIC,e,IN_PROGRESS,d\n" +
                    "3,SUBTASK,s,IN_PROGRESS,d,2\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, fileBackedTaskManager.getAllTasks().size());
        assertEquals(1, fileBackedTaskManager.getAllEpics().size());
        assertEquals(1, fileBackedTaskManager.getAllSubtasks().size());
    }

    @Test
    void newFileManagerAndloadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("Таска1", "Описание1");
        fileBackedTaskManager.addTask(task);
        Epic epic = new Epic("Эпик2", "Описание2");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("Сабтаска3", "Описание3", epic.getId());
        fileBackedTaskManager.addSubTask(subTask);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);

        assertEquals(fileBackedTaskManager.getTaskId(1), fileBackedTaskManager1.getTaskId(1));
        assertEquals(fileBackedTaskManager.getEpicId(2), fileBackedTaskManager1.getEpicId(2));
        assertEquals(fileBackedTaskManager.getSubTaskId(3), fileBackedTaskManager1.getSubTaskId(3));
    }
}