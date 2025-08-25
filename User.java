import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class User implements Serializable {
    private int userId;
    private String name;
    private String password;
    private ArrayList<Task> tasks;

    public User(int userId, String name, String password){
        this.userId = userId;
        this.name = name;
        this.password = password;
        tasks = new ArrayList<>();
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public ArrayList<Task> getTasks() { return tasks; }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    public void addTask(Task t){ tasks.add(t); }
    public void deleteTask(int taskId){ tasks.removeIf(t -> t.getTaskId() == taskId); }
    public void markTaskCompleted(int taskId){
        for(Task t : tasks){
            if(t.getTaskId() == taskId){ t.markCompleted(); break; }
        }
    }
    public void updateTask(int taskId, String title, String desc, String priority, String deadline){
        for(Task t : tasks){
            if(t.getTaskId() == taskId){ t.update(title, desc, priority, deadline); break; }
        }
    }

    public void sortTasksByPriority(){ tasks.sort(Comparator.comparing(Task::getPriority)); }
    public void sortTasksByDeadline(){ tasks.sort(Comparator.comparing(Task::getDeadline)); }
}
