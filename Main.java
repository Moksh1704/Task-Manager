import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TaskManagerGUI gui = new TaskManagerGUI();
                gui.setVisible(true);
            }
        });
    }
}
