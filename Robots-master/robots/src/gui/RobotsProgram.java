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

      try {
        ResourceBundle bundle = ResourceBundle.getBundle("swing_ru");

        UIManager.put("OptionPane.yesButtonText", bundle.getString("OptionPane.yesButtonText"));
        UIManager.put("OptionPane.noButtonText", bundle.getString("OptionPane.noButtonText"));
        UIManager.put("OptionPane.cancelButtonText", bundle.getString("OptionPane.cancelButtonText"));
        UIManager.put("OptionPane.okButtonText", bundle.getString("OptionPane.okButtonText"));
        UIManager.put("FileChooser.openButtonText", bundle.getString("FileChooser.openButtonText"));
        UIManager.put("FileChooser.saveButtonText", bundle.getString("FileChooser.saveButtonText"));
        UIManager.put("FileChooser.cancelButtonText", bundle.getString("FileChooser.cancelButtonText"));

      } catch (Exception e) {
        System.out.println("Ресурсы русские не удалось загрузить: " + e.getMessage());
      }

      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

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
