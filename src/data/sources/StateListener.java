package data.sources;

import data.Point;

/**
 * Created by Kai on 08.07.2017.
 */
public interface StateListener {
    void onUpdateData(State state, Point<String> point);
}
