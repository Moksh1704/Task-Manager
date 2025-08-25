import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class TaskManagerGUI extends JFrame {
    private static final String FILE_NAME = "users.dat";
    private ArrayList<User> users;
    private User currentUser;
    private int taskCounter = 1;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private boolean darkModeEnabled = false;

    public TaskManagerGUI() {
        users = loadUsers();
        if(users.isEmpty()) users.add(new User(1,"Somanadh","1234"));
        currentUser = users.get(0);
        setTitle("Task Manager");
        setSize(950, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel();
        JLabel currentUserLabel = new JLabel("Current User: " + currentUser.getName());
        currentUserLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton switchUserBtn = new JButton("Switch User");
        JButton addUserBtn = new JButton("Add User");
        JCheckBox darkModeToggle = new JCheckBox("Dark Mode");
        topPanel.add(currentUserLabel);
        topPanel.add(switchUserBtn);
        topPanel.add(addUserBtn);
        topPanel.add(darkModeToggle);

        String[] columns = {"ID","Title","Description","Priority","Deadline","Status"};
        tableModel = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row, int column){ return false; }
        };
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(30);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 14));
        taskTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        taskTable.setSelectionBackground(new Color(173,216,230));
        taskTable.setDefaultRenderer(Object.class,new TableCellRenderer(){
            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
                Component c = defaultRenderer.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
                c.setForeground(darkModeEnabled ? Color.WHITE : Color.BLACK);
                c.setBackground(darkModeEnabled ? new Color(50,50,50) : Color.WHITE);

                if(column==3){ // Priority
                    String priority = (String)table.getValueAt(row,column);
                    switch(priority.toLowerCase()){
                        case "high": c.setBackground(new Color(255,102,102)); break;
                        case "medium": c.setBackground(new Color(255,178,102)); break;
                        case "low": c.setBackground(new Color(102,255,102)); break;
                    }
                }
                if(column==5){ // Status
                    String status = (String)table.getValueAt(row,column);
                    if(status.equalsIgnoreCase("Completed")) c.setBackground(new Color(102,255,102));
                    else c.setBackground(new Color(255,255,153));
                }
                if(column==4){ // Deadline gradient
                    String deadlineStr = (String)table.getValueAt(row,column);
                    try{
                        LocalDate deadline = LocalDate.parse(deadlineStr);
                        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
                        if(daysLeft < 3) c.setBackground(new Color(200,0,0));
                        else if(daysLeft <= 7) c.setBackground(new Color(255,140,0));
                        else c.setBackground(new Color(144,238,144));
                    }catch(Exception e){}
                }
                if(isSelected) c.setBackground(new Color(173,216,230));
                return c;
            }
        });
        JScrollPane tableScroll = new JScrollPane(taskTable);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2,3,5,5));
        JButton addTaskBtn = new JButton("Add Task");
        JButton updateTaskBtn = new JButton("Update Task");
        JButton deleteTaskBtn = new JButton("Delete Task");
        JButton markCompletedBtn = new JButton("Mark Completed");
        JButton sortPriorityBtn = new JButton("Sort by Priority");
        JButton sortDeadlineBtn = new JButton("Sort by Deadline");
        bottomPanel.add(addTaskBtn);
        bottomPanel.add(updateTaskBtn);
        bottomPanel.add(deleteTaskBtn);
        bottomPanel.add(markCompletedBtn);
        bottomPanel.add(sortPriorityBtn);
        bottomPanel.add(sortDeadlineBtn);

        add(topPanel,BorderLayout.NORTH);
        add(tableScroll,BorderLayout.CENTER);
        add(bottomPanel,BorderLayout.SOUTH);

        // Button actions
        addTaskBtn.addActionListener(e -> addTaskDialog());
        updateTaskBtn.addActionListener(e -> updateTaskDialog());
        deleteTaskBtn.addActionListener(e -> deleteTask());
        markCompletedBtn.addActionListener(e -> markCompleted());
        sortPriorityBtn.addActionListener(e -> { currentUser.sortTasksByPriority(); refreshTable(); });
        sortDeadlineBtn.addActionListener(e -> { currentUser.sortTasksByDeadline(); refreshTable(); });
        addUserBtn.addActionListener(e -> addUserDialog(currentUserLabel));
        switchUserBtn.addActionListener(e -> switchUserDialog(currentUserLabel));
        darkModeToggle.addActionListener(e -> { darkModeEnabled = darkModeToggle.isSelected(); refreshTable(); });

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){ saveUsers(users); }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for(Task t: currentUser.getTasks()){
            tableModel.addRow(new Object[]{t.getTaskId(),t.getTitle(),t.getDescription(),t.getPriority(),t.getDeadline(),t.getStatus()});
        }
    }

    private void addTaskDialog() {
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField priorityField = new JTextField();
        JTextField deadlineField = new JTextField();
        Object[] message = {"Title:",titleField,"Description:",descField,"Priority (High/Medium/Low):",priorityField,"Deadline (YYYY-MM-DD):",deadlineField};
        int option = JOptionPane.showConfirmDialog(this,message,"Add Task",JOptionPane.OK_CANCEL_OPTION);
        if(option==JOptionPane.OK_OPTION){
            Task t = new Task(taskCounter++,titleField.getText(),descField.getText(),priorityField.getText(),deadlineField.getText());
            currentUser.addTask(t);
            refreshTable();
        }
    }

    private void updateTaskDialog(){
        int row = taskTable.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"Select a task first."); return; }
        Task t = currentUser.getTasks().get(row);
        JTextField titleField = new JTextField(t.getTitle());
        JTextField descField = new JTextField(t.getDescription());
        JTextField priorityField = new JTextField(t.getPriority());
        JTextField deadlineField = new JTextField(t.getDeadline());
        Object[] message = {"Title:",titleField,"Description:",descField,"Priority:",priorityField,"Deadline:",deadlineField};
        int option = JOptionPane.showConfirmDialog(this,message,"Update Task",JOptionPane.OK_CANCEL_OPTION);
        if(option==JOptionPane.OK_OPTION){
            currentUser.updateTask(t.getTaskId(),titleField.getText(),descField.getText(),priorityField.getText(),deadlineField.getText());
            refreshTable();
        }
    }

    private void deleteTask(){
        int row = taskTable.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"Select a task first."); return; }
        Task t = currentUser.getTasks().get(row);
        currentUser.deleteTask(t.getTaskId());
        refreshTable();
    }

    private void markCompleted(){
        int row = taskTable.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"Select a task first."); return; }
        Task t = currentUser.getTasks().get(row);
        currentUser.markTaskCompleted(t.getTaskId());
        refreshTable();
    }

    private void addUserDialog(JLabel currentUserLabel){
        String name = JOptionPane.showInputDialog(this,"Enter new user name:");
        if(name != null && !name.isEmpty()){
            String password = JOptionPane.showInputDialog(this,"Set a password for " + name + ":");
            if(password != null){
                int newId = users.size() + 1;
                users.add(new User(newId, name, password));
                JOptionPane.showMessageDialog(this,"User added: "+name);
            }
        }
    }

    private void switchUserDialog(JLabel currentUserLabel){
        StringBuilder sb = new StringBuilder("Available Users:\n");
        for(User u: users) sb.append(u.getUserId()).append(". ").append(u.getName()).append("\n");
        String input = JOptionPane.showInputDialog(this,sb.toString()+"Enter user ID to switch:");
        if(input != null){
            try{
                int id = Integer.parseInt(input);
                for(User u: users){
                    if(u.getUserId() == id){
                        String password = JOptionPane.showInputDialog(this,"Enter password for " + u.getName() + ":");
                        if(password != null && u.checkPassword(password)){
                            currentUser = u;
                            currentUserLabel.setText("Current User: " + currentUser.getName());
                            refreshTable();
                            return;
                        } else {
                            JOptionPane.showMessageDialog(this,"Incorrect password!");
                            return;
                        }
                    }
                }
                JOptionPane.showMessageDialog(this,"User not found.");
            }catch(Exception e){ JOptionPane.showMessageDialog(this,"Invalid input."); }
        }
    }

    private ArrayList<User> loadUsers(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))){
            return (ArrayList<User>)ois.readObject();
        }catch(Exception e){ return new ArrayList<>(); }
    }

    private void saveUsers(ArrayList<User> users){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))){
            oos.writeObject(users);
        }catch(Exception e){ e.printStackTrace(); }
    }
}
