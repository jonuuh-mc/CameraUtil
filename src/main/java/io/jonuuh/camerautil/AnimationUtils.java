package io.jonuuh.camerautil;

public class AnimationUtils
{
    public static double easeIn(float scale, double x)
    {
        return Math.pow(x, scale);
    }

    public static double easeOut(float scale, double x)
    {
        return 1 - Math.pow(1 - x, scale);
    }

    public static double easeInOut(float scale, double x)
    {
        double firstHalf = (0.5F / Math.pow(0.5, scale)) * Math.pow(x, scale);
        double secondHalf = 1 - (Math.pow(-2 * x + 2, scale) / 2);

        return (x < 0.5) ? (firstHalf) : (secondHalf);
    }

    public static double normalize(double min, double max, double value)
    {
        return (value - min) / (max - min);
    }

    public static double denormalize(double min, double max, double value)
    {
        return value * (max - min) + min;
    }

    public static double clamp(double min, double max, double value)
    {
        return Math.min((Math.max(value, min)), max);
    }
}
