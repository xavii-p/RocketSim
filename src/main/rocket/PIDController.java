package rocket;

public class PIDController {
    private double kp;
    private double ki;
    private double kd;
    private double previousError = 0;
    private double integral = 0;
    private double outputMin = Double.NEGATIVE_INFINITY;
    private double outputMax = Double.POSITIVE_INFINITY;

    public PIDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public void setOutputLimits(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum limit must be less than maximum limit");
        }
        this.outputMin = min;
        this.outputMax = max;
    }

    public double compute(double error, double timeElapsed) {
        integral += error * timeElapsed;
        double derivative = (error - previousError) / timeElapsed;
        double output = kp * error + ki * integral + kd * derivative;

        // Anti-windup: Clamp the integral term if output is out of bounds
        if (output > outputMax) {
            output = outputMax;
            integral -= error * timeElapsed; // Prevent integral windup
        } else if (output < outputMin) {
            output = outputMin;
            integral -= error * timeElapsed; // Prevent integral windup
        }

        previousError = error;
        return output;
    }

    public void reset() {
        previousError = 0;
        integral = 0;
    }
}
