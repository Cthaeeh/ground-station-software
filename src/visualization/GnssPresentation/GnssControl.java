package visualization.GnssPresentation;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import data.Point;
import data.sources.Gnss;
import data.sources.GnssFrame;
import data.sources.GnssListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import visualization.VisualizationElement;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by kai on 12/11/17.
 */
public class GnssControl implements Initializable, MapComponentInitializedListener, VisualizationElement, GnssListener{

    @FXML
    private GoogleMapView mapView;

    private GoogleMap map;
    private List<Gnss> gnssList;
    private HashMap<Gnss, Marker> gnssDataSourceMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapView.addMapInializedListener(this);
    }

    @Override
    public void mapInitialized() {
        //TODO carefully select Map options.
        MapOptions mapOptions = new MapOptions();
        mapOptions.mapType(MapTypeIdEnum.TERRAIN)
                .center(new LatLong(50,10))
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(true)
                .scaleControl(true)
                .zoomControl(true)
                .zoom(2);
        map = mapView.createMap(mapOptions);
        processGnssList();
    }

    @Override
    public void unsubscibeDataSources() {
        for(Gnss sensor : gnssList){
            sensor.removeListeners(this);
        }
    }

    public void setGnssList(List<Gnss> gnssList) {
        this.gnssList = gnssList;
    }

    private void processGnssList() {
        unsubscibeDataSources();
        map.removeMarkers(gnssDataSourceMap.values());
        gnssDataSourceMap.clear();
        for (Gnss gnss : gnssList) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.label(gnss.getName());
            markerOptions.visible(false);
            Marker marker = new Marker(markerOptions);
            map.addMarker(marker);
            gnssDataSourceMap.put(gnss, marker);
            gnss.addListener(this);
        }
    }
    @Override
    public void onUpdateData(Gnss dataSource, Point<GnssFrame> point) {
        Marker marker = gnssDataSourceMap.get(dataSource);
        marker.setPosition(new LatLong(point.y.getLatitude(),point.y.getLatitude()));
        marker.setVisible(true);
        //TODO somehow use numOfSatellites etc.
    }

}
