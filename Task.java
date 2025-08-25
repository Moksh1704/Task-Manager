import java.io.Serializable;

public class Task implements Serializable {
    private int taskId;
    private String title;
    private String description;
    private String priority; // High, Medium, Low
    private String deadline; // YYYY-MM-DD
    private String status;

    public Task(int taskId, String title, String description, String priority, String deadline) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = "Pending";
    }

    public void markCompleted() { this.status = "Completed"; }

    public void update(String title, String description, String priority, String deadline) {
        if(title != null && !title.isEmpty()) this.title = title;
        if(description != null && !description.isEmpty()) this.description = description;
        if(priority != null && !priority.isEmpty()) this.priority = priority;
        if(deadline != null && !deadline.isEmpty()) this.deadline = deadline;
    }

    public int getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
