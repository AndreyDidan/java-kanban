package task.manager.service;

import exception.ManagerSaveException;
import task.manager.model.*;

import java.io.*;
import java.util.*;
import java.io.IOException;
import java.io.File;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile();
        return manager;
    }

    // Сохранение в файл
    private void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, UTF_8))) {
            String titul = "id,type,name,status,description,epic,startTime,duration\n";
            writer.write(titul);
            for (HashMap.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(Converter.toString(entry.getValue()));
                writer.newLine();
            }
            for (HashMap.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(Converter.toString(entry.getValue()));
                writer.newLine();
            }
            for (HashMap.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                writer.append(Converter.toString(entry.getValue()));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    public void loadFromFile() {
        int maxId = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                final Task task = Converter.fromString(line);
                final int loadId = task.getId();

                if (maxId < loadId) {
                    maxId = loadId;
                }

                if (task.getType() == TaskType.TASK) {
                    tasks.put(loadId, task);
                } else if (task.getType() == TaskType.EPIC) {
                    epics.put(loadId, (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    subTasks.put(loadId, (SubTask) task);
                    Epic epic = epics.get(task.getIdEpic());
                    epic.addSubTask(task.getId());
                    changeEpicState(epics.get(task.getIdEpic()));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        super.id = maxId + 1;
    }

    @Override
    public Task addTask(Task newTask) {
        super.addTask(newTask);
        save();
        return newTask;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
        return newEpic;
    }

    @Override
    public SubTask addSubTask(SubTask newSubTask) {
        super.addSubTask(newSubTask);
        save();
        return newSubTask;
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        super.updateSubTask(updateSubTask);
        save();
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }
}