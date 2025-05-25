import java.util.Objects;

public class Task {
    private int taskId; //Индетификатор задач уникальное для всех типов задач
    private String taskName;
    private String taskDescriptionl;
    private Statuc taskStatus;

    public Task( String taskName, String taskDescriptionl, Statuc taskStatus) {
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
