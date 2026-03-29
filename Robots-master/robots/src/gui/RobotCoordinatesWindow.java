package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import model.RobotModel;
import model.RobotModelListener;

public class RobotCoordinatesWindow extends JInternalFrame implements RobotModelListener {

    private final JLabel xLabel;
    private final JLabel yLabel;
    private final JLabel directionLabel;
    private final JLabel targetXLabel;
    private final JLabel targetYLabel;
    private final DecimalFormat df = new DecimalFormat("#0.00");

    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);

        model.addListener(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new java.awt.GridLayout(5, 2, 5, 10));
        infoPanel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        infoPanel.add(new JLabel("Позиция X:", SwingConstants.RIGHT));
        xLabel = new JLabel("0.00");
        infoPanel.add(xLabel);

        infoPanel.add(new JLabel("Позиция Y:", SwingConstants.RIGHT));
        yLabel = new JLabel("0.00");
        infoPanel.add(yLabel);

        infoPanel.add(new JLabel("Направление (градусы):", SwingConstants.RIGHT));
        directionLabel = new JLabel("0.00");
        infoPanel.add(directionLabel);

        infoPanel.add(new JLabel("Цель X:", SwingConstants.RIGHT));
        targetXLabel = new JLabel("0.00");
        infoPanel.add(targetXLabel);

        infoPanel.add(new JLabel("Цель Y:", SwingConstants.RIGHT));
        targetYLabel = new JLabel("0.00");
        infoPanel.add(targetYLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        getContentPane().add(panel);
        setSize(250, 180);
        setLocation(320, 10);

        updateCoordinates(model);
    }

    private void updateCoordinates(RobotModel model)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                xLabel.setText(df.format(model.getX()));
                yLabel.setText(df.format(model.getY()));
                directionLabel.setText(df.format(Math.toDegrees(model.getDirection())));
                targetXLabel.setText(df.format(model.getTargetX()));
                targetYLabel.setText(df.format(model.getTargetY()));
            }
        });
    }

    @Override
    public void onModelChanged(RobotModel model) {
        updateCoordinates(model);
    }
}