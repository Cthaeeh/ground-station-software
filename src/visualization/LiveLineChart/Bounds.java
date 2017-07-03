package visualization.LiveLineChart;


/**
 * A immutable class for the BoundsDialog to pass back if the user entered the bounds.
 * Specifically for the LiveLineChart
 * Builder pattern is used here!
 */
public class Bounds {

    /**
     * Lower Bound of the y-Axis of a chart
     */
    private final double yLowerBound;
    /**
     * Upper Bound of the y-Axis of a chart
     */
    private final double yUpperBound;

    private final boolean yAutoRange;
    /**
     * Intervall that is shown in the x Axis in seconds.
     */
    private final double xTimeIntervalSec;

    public Bounds(Builder builder) {
        this.yLowerBound = builder.yLowerBound;
        this.yUpperBound = builder.yUpperBound;
        this.yAutoRange = builder.yAutoRange;
        this.xTimeIntervalSec = builder.xTimeIntervalSec;
    }

    public double getyLowerBound() {
        return yLowerBound;
    }

    public double getyUpperBound() {
        return yUpperBound;
    }

    public double getxTimeIntervalSec() {
        return xTimeIntervalSec;
    }

    public boolean yAutoRange() {
        return yAutoRange;
    }

    public static class Builder {
        private double yLowerBound = 0;
        private double yUpperBound = 10;
        private final boolean yAutoRange;
        private double xTimeIntervalSec = 5;

        public Builder(boolean yAutoRange){
            this.yAutoRange = yAutoRange;
        }

        public Builder yLowerBound(double yLowerBound){
            this.yLowerBound = yLowerBound;
            return this;
        }
        public Builder yUpperBound(double yUpperBound){
            this.yUpperBound = yUpperBound;
            return this;
        }
        public Builder xTimeIntervalSec(double xTimeIntervalSec){
            this.xTimeIntervalSec = xTimeIntervalSec;
            return this;
        }

        public Bounds build(){
            return new Bounds(this);
        }
    }
}
