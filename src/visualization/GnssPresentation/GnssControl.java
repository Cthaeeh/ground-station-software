package visualization.GnssPresentation;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import data.Point;
import data.sources.Gnss;
import data.sources.GnssFrame;
import data.sources.GnssListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import netscape.javascript.JSObject;
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
    private HashMap<Gnss, Marker> gnssMarkerMap = new HashMap<>();
    private HashMap<Gnss, InfoWindow> gnssInfoWindowMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapView.addMapInializedListener(this);
    }

    @Override
    public void mapInitialized() {
        //TODO carefully select Map options.
        MapOptions mapOptions = new MapOptions();
        mapOptions.mapType(MapTypeIdEnum.TERRAIN)
                .center(new LatLong(0,0))
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
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
        map.removeMarkers(gnssMarkerMap.values());
        gnssMarkerMap.clear();
        for (Gnss gnss : gnssList) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.label(gnss.getName());
            markerOptions.visible(false);
            Marker marker = new Marker(markerOptions);
            InfoWindow infoWindow = new InfoWindow();
            map.addMarker(marker);
            gnssMarkerMap.put(gnss, marker);
            gnssInfoWindowMap.put(gnss, infoWindow);
            gnss.addListener(this);
            map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                //Open a Info Window at marker location.
                infoWindow.open(map,marker);
            });
        }
    }
    @Override
    public void onUpdateData(Gnss dataSource, Point<GnssFrame> point) {
        Marker marker = gnssMarkerMap.get(dataSource);
        InfoWindow infoWindow = gnssInfoWindowMap.get(dataSource);
        marker.setPosition(new LatLong(point.y.getLatitude(),point.y.getLatitude()));
        infoWindow.setContent("Satellites: " + point.y.getNumOfSatellites() + System.lineSeparator() +
                              "Fix Quality:" + point.y.getFixQuality());
        marker.setVisible(true);
    }

}
