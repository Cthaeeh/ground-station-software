package data.sources;

import data.Point;

/**
 *
 * @param <T> Type of data we listen to.
 */
public interface DataSourceListener <T> {
    void onUpdateData(DataSource<T> dataSource, T point);
}
