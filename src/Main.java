import data.MusicTrack;
import data.MusicTrackImpl1;
import math.ConvolveProcessor;
import math.FourierProcessor;
import tools.Ressources;
import ui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        //init Ressources
        new Ressources(args);

        JFrame mainWindow = new JFrame("Unkreativ");
        initFrame(mainWindow);
    }

    private static void initFrame(JFrame mainWindow) {
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        mainWindow.setPreferredSize(new Dimension(dim.width / 2, dim.height / 2));
        mainWindow.setResizable(false);

        MainWindow content = new MainWindow();
        mainWindow.setContentPane(content.getMainPanel());
        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }
}
