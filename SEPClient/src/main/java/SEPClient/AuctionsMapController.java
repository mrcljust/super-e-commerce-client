package SEPClient;

import java.io.IOException;

import com.dlsc.gmapsfx.GoogleMapView;
import com.dlsc.gmapsfx.MapComponentInitializedListener;
import com.dlsc.gmapsfx.javascript.object.GoogleMap;
import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MapOptions;
import com.dlsc.gmapsfx.javascript.object.MapTypeIdEnum;
import com.dlsc.gmapsfx.javascript.object.Marker;
import com.dlsc.gmapsfx.javascript.object.MarkerOptions;
import com.dlsc.gmapsfx.service.geocoding.GeocodingService;

import SEPCommon.Auction;
import SEPCommon.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuctionsMapController implements MapComponentInitializedListener {
	
	//QUELLE: https://staticfinal.blog/2014/04/25/gmapsfx-add-google-maps-to-your-javafx-application/
	//GMapsFX :: Add Google Maps to your JavaFX application.
	//April 25, 2014 von rterp
	
	//QUELLE: https://github.com/dlsc-software-consulting-gmbh/GMapsFX (diese API wird benutzt)
	//GMapsFX-Examples von dlemmermann, 05.01.2021
	
	
	private static User user = null;
	private static ObservableList<Auction> allAuctions = null;
	private LatLong userLatLong;
    private GeocodingService geocodingService;
	
    @FXML
    protected GoogleMapView mapView;
    
	GoogleMap map;
	
	public static void setUser(User _user)
	{
		user=_user;
	}
	
	public static void setAllAuctions(ObservableList<Auction> _allAuctions)
	{
		allAuctions=_allAuctions;
	}
	
	public void initialize() throws IOException {
		mapView.setKey("AIzaSyAw19D5oAD7yBKkzRlUPkcq1DdNzSn71r4");
		mapView.addMapInitializedListener(this);
	}
	
	@Override
	public void mapInitialized() {
		//user-adresse zu latlong konvertieren
		userLatLong = new LatLong(51.464185, 7.005747);
		geocodingService = new GeocodingService();
		
		//Set the initial properties of the map.
	    MapOptions mapOptions = new MapOptions();

	    mapOptions.center(userLatLong)
	            .mapType(MapTypeIdEnum.ROADMAP)
	            .overviewMapControl(false)
	            .panControl(false)
	            .rotateControl(false)
	            .scaleControl(false)
	            .streetViewControl(false)
	            .zoomControl(false)
	            .zoom(12);

	    map = mapView.createMap(mapOptions);

	    //Add a marker to the map
	    MarkerOptions markerOptions = new MarkerOptions();

	    markerOptions.position( new LatLong(51.464165, 7.006696) )
	                .visible(Boolean.TRUE)
	                .title("Neben der Uni");

	    Marker marker = new Marker( markerOptions );

	    map.addMarker(marker);
	}
	
	@FXML
    private TextField AuctionMap_txtRadius;

    @FXML
    private Button AuctionsMap_ButtonShow;

    @FXML
    private Button AuctionsMap_ButtonViewAuction;
    
    @FXML
    private Button AuctionsMap_ButtonReturn;

    @FXML
    void AuctionsMap_ButtonShow_Click(ActionEvent event) {

    }

    @FXML
    void AuctionsMap_ButtonViewAuction_Click(ActionEvent event) {

    }
    
    @FXML
    void AuctionsMap_ButtonReturn_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) AuctionsMap_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
