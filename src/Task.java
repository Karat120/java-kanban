import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int taskId; //Индетификатор задач уникальное для всех типов задач
    private String taskName;
    private String taskDescriptionl;
    private Statuc taskStatus;
    //новое
    protected Duration duration;  // продолжительность в минутах
    protected LocalDateTime startTime;  // время начала

    public Task(String name, String description, Statuc status, LocalDateTime startTime, Duration duration) {
        this.taskName = name;
        this.taskDescriptionl = description;
        this.taskStatus = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    //старое
    public Task(String taskName, String taskDescriptionl, Statuc taskStatus) {
        this.taskName = taskName;
        this.taskDescriptionl = taskDescriptionl;
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescriptionl(String taskDescriptionl) {
        this.taskDescriptionl = taskDescriptionl;
    }

    public void setTaskStatus(Statuc taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescriptionl() {
        return taskDescriptionl;
    }

    public Statuc getTaskStatus() {
        return taskStatus;
    }

    // Геттер для времени окончания
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    // Геттеры и сеттеры
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", taskDescriptionl='" + taskDescriptionl + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
