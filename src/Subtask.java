public class Subtask extends Task {
    private int epicId;

    public Subtask(Epic epicTask, String taskName, String taskDescriptionl, Statuc taskStatus ) {
        super(taskName,taskDescriptionl,taskStatus);
        this.epicId = epicTask.getTaskId();

    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getTaskId() +
                ", epicId=" + epicId +
                ", name=" + getTaskName() + " , taskDescriptionl='" + super.getTaskDescriptionl() +
                ", status=" + getTaskStatus() +
                '}';
    }
}
