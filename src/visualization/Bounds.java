package visualization;


/**
 * A POJO for the BoundsDialog to pass back if the user entered the bounds.
 * Specifically for the LiveLineChart
 */
public class Bounds{

    /**
     * Lower Bound of the y-Axis of a chart*/
    private final double yLowerBound;
    /**
     * Upper Bound of the y-Axis of a chart*/
    private final double yUpperBound;

    private final boolean yAutoRange;
    /**
     * Intervall that is shown in the x Axis in seconds.
     */
    private final double xTimeSpanSec;


    public Bounds(double yLowerBound, double yUpperBound, double xTimeSpanSec, boolean yAutoRange) {
        this.yLowerBound = yLowerBound;
        this.yUpperBound = yUpperBound;
        this.xTimeSpanSec = xTimeSpanSec;
        this.yAutoRange = yAutoRange;
    }

    public double getyLowerBound() {
        return yLowerBound;
    }

    public double getyUpperBound() {
        return yUpperBound;
    }

    public double getxTimeSpanSec() {
        return xTimeSpanSec;
    }

    public boolean yAutoRange() {
        return yAutoRange;
    }
}
