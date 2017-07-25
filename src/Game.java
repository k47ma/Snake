import javax.swing.*;
import java.awt.*;

/**
 * Created by mk on 2017/7/25.
 */
public class Game {
    public static void main(String [] args) {
        JFrame frame = new JFrame("Hello world!");
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JLabel label = new JLabel("This is a label!");
        JButton button = new JButton("Button");

        panel.add(label);
        panel.add(button);

        frame.add(panel);
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
