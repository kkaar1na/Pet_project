package model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RobotModel {
    private double x = 100;
    private double y = 100;
    private double direction = 0;

    private double targetX = 150;
    private double targetY = 100;

    private final List<RobotModelListener> listeners = new CopyOnWriteArrayList<>();
    private static final double MAX_VELOCITY = 100.0;
    private static final double MAX_ANGULAR_VELOCITY = 10.0;

    public void addListener(RobotModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RobotModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (RobotModelListener listener : listeners) {
            listener.onModelChanged(this);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public double getTargetX() { return targetX; }
    public double getTargetY() { return targetY; }

    public void setTargetPosition(double targetX, double targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
        notifyListeners();
    }

    public void updateModel(int durationMs) {
        double distance = distance(targetX, targetY, x, y);
        if (distance < 0.5) {
            notifyListeners();
            return;
        }

        double velocity = Math.min(MAX_VELOCITY, distance * 5);
        double angleToTarget = angleTo(x, y, targetX, targetY);
        double angularVelocity = 0;

        double angleDifference = asNormalizedRadians(angleToTarget - direction);

        if (angleDifference > Math.PI) {
            angleDifference = angleDifference - 2 * Math.PI;
        }

        if (angleDifference > 0) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        } else if (angleDifference < 0) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        moveRobot(velocity, angularVelocity, durationMs / 1000.0);
        notifyListeners();
    }

    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = x + velocity / angularVelocity *
                (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));
        if (!Double.isFinite(newX))
        {
            newX = x + velocity * duration * Math.cos(direction);
        }
        double newY = y - velocity / angularVelocity *
                (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
        if (!Double.isFinite(newY))
        {
            newY = y + velocity * duration * Math.sin(direction);
        }

        x = newX;
        y = newY;
        direction = asNormalizedRadians(direction + angularVelocity * duration);
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double dx = toX - fromX;
        double dy = toY - fromY;
        double angle = Math.atan2(dy, dx);
        return asNormalizedRadians(angle);
    }
}