package data.sources;

import data.Point;

/**
 * Created by Kai on 15.08.2017.
 */
public interface StringSourceListener {
    void onUpdateData(StringSource stringSource, Point<String> point);
}
