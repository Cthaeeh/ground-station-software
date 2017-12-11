package data.sources;

import data.Point;

/**
 * Created by kai on 12/11/17.
 */
public interface GnssListener {
    void onUpdateData(Gnss dataSource, Point<GnssFrame> point);
}
