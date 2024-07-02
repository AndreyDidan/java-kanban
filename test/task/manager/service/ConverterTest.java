package task.manager.service;

import org.junit.jupiter.api.Test;
import task.manager.model.Epic;
import task.manager.model.StateTask;
import task.manager.model.SubTask;
import task.manager.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {
    @Test
    void fromString() {
        // subtask test
        String subTaskString = "8,SUBTASK,Подзадача 7,NEW,Описание подзадачи 7,7,null,null";
        SubTask expectedSubTask = new SubTask("Подзадача 7", "Описание подзадачи 7", StateTask.NEW,
                7, 8);
        expectedSubTask.setId(8);

        assertEquals(expectedSubTask, Converter.fromString(subTaskString));

        String subTaskStringWithDurationAndStartTime = "8,SUBTASK,Подзадача 7,NEW,Описание подзадачи 7,7," +
                "2024-07-02T02:29:39.803947700,PT5M";
        expectedSubTask = new SubTask("Подзадача 7", "Описание подзадачи 7", StateTask.NEW, 7,
                8, LocalDateTime.parse("2024-07-02T02:29:39.803947700"), Duration.ofMinutes(5));
        expectedSubTask.setId(8);

        assertEquals(expectedSubTask, Converter.fromString(subTaskStringWithDurationAndStartTime));

        // task test
        String taskString = "1,TASK,Задача 0,NEW,Описание0,null,null,null";
        Task expectedTask = new Task(1, "Задача 0", "Описание0", StateTask.NEW);
        expectedTask.setId(1);

        assertEquals(expectedTask, Converter.fromString(taskString));

        String taskStringWithTime = "1,TASK,Задача 3,NEW,Описание3,null,2024-07-02T01:29:39.814946800,PT10M";
        expectedTask = new Task(1, "name1", "descript1", StateTask.NEW,
                LocalDateTime.parse("2024-07-02T01:29:39.814946800"), Duration.ofMinutes(10));
        expectedTask.setId(1);

        assertEquals(expectedTask, Converter.fromString(taskStringWithTime));

        // epic test
        String epicString = "7,EPIC,Эпик 6,NEW,Описаание эпика 6,null,null,null";
        Epic expectedEpic = new Epic("Эпик 6", "Описаание эпика 6");
        expectedEpic.setId(7);

        assertEquals(expectedEpic, Converter.fromString(epicString));
    }
}