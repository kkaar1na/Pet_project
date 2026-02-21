package gui;

import java.awt.Frame;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram
{
    public static void main(String[] args) {
        try {
            Locale.setDefault(new Locale("ru", "RU"));

            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            ResourceBundle bundle = ResourceBundle.getBundle("swing_ru");

            for (String key : bundle.keySet()) {
                UIManager.put(key, bundle.getString(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame();
            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        });
    }
}
