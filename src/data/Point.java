package data;

/**
 * Created by Kai on 03.06.2017.
 * A generic Pair/Point/Touple consisting of a timepoint -> Number (Maybe ill later make this a more time related object, like a Date or so.)
 * and a value that can be anything.
 */
public class Point<Y> {
    public final Number x;
    public final Y y;

    public Point(Number x,Y y) {
        this.x = x;
        this.y = y;
    }
}
