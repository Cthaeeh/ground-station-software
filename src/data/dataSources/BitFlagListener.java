package data.dataSources;

import data.Point;

/**
 * Created by Kai on 04.06.2017.
 */
public interface BitFlagListener {
    void onUpdateData(BitFlag bitFlag, Point<Boolean> point);
}
