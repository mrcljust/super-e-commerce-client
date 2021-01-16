package SEPClient;

import java.io.IOException;

import com.dlsc.gmapsfx.GoogleMapView;
import com.dlsc.gmapsfx.MapComponentInitializedListener;
import com.dlsc.gmapsfx.javascript.event.UIEventType;
import com.dlsc.gmapsfx.javascript.object.GoogleMap;
import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MapOptions;
import com.dlsc.gmapsfx.javascript.object.MapTypeIdEnum;
import com.dlsc.gmapsfx.javascript.object.Marker;
import com.dlsc.gmapsfx.javascript.object.MarkerOptions;
import com.dlsc.gmapsfx.service.geocoding.GeocoderStatus;
import com.dlsc.gmapsfx.service.geocoding.GeocodingResult;
import com.dlsc.gmapsfx.service.geocoding.GeocodingService;
import com.dlsc.gmapsfx.service.geocoding.GeocodingServiceCallback;
import com.dlsc.gmapsfx.util.MarkerImageFactory;
import java.util.List;
import SEPCommon.Auction;
import SEPCommon.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class AuctionsMapController implements MapComponentInitializedListener {

	// QUELLE:
	// https://staticfinal.blog/2014/04/25/gmapsfx-add-google-maps-to-your-javafx-application/
	// GMapsFX :: Add Google Maps to your JavaFX application.
	// April 25, 2014 von rterp

	// QUELLE: https://github.com/dlsc-software-consulting-gmbh/GMapsFX (diese API
	// wird benutzt)/Examples
	// GMapsFX-Examples von dlemmermann, 05.01.2021

	// QUELLE Listener für Marker:
	// https://github.com/dlsc-software-consulting-gmbh/GMapsFX/issues/83
	// GeoffCapper commented on 16 Nov 2016

	private static User user = null;
	private static ObservableList<Auction> allAuctions = null;

	private LatLong userLatLong;
	private GeocodingService geocodingService;
	private GoogleMap map;

	private int addressNotFoundCount = 0;
	private int notInRadiusCount = 0;
	private int addedAuctions = 0;
	private int auctionCounter = 0;

	private List<Marker> markerList = new java.util.ArrayList<Marker>();
	private List<LatLong> latLongList = new java.util.ArrayList<LatLong>();
	
	private Marker currentMarker;
	private int currentAuctId = -1;

	public static void setUser(User _user) {
		user = _user;
	}

	public static void setAllAuctions(ObservableList<Auction> _allAuctions) {
		allAuctions = _allAuctions;
	}

	public void initialize() throws IOException {
		mapView.setKey(SEPCommon.Constants.GOOGLE_APIKEY);
		mapView.addMapInitializedListener(this);
	}

	@Override
	public void mapInitialized() {
		geocodingService = new GeocodingService();

		addHomeMarker();
	}

	private void addHomeMarker() {
		String userAddress = user.getAddress().getStreet() + " " + user.getAddress().getNumber() + ", "
				+ user.getAddress().getZipcode() + " " + user.getAddress().getCity() + ", "
				+ user.getAddress().getCountry();
		geocodingService.geocode(userAddress, new GeocodingServiceCallback() {

			@Override
			public void geocodedResultsReceived(GeocodingResult[] results, GeocoderStatus status) {
				// User-Adresse, falls möglich, als Zentrierpunkt der Map setzen
				if (status == GeocoderStatus.ZERO_RESULTS) {
					System.out.println(
							"Maps - Keine übereinstimmende User-Adresse gefunden, nutze Adresse der Universität.");
					FXMLHandler.ShowMessageBox(
							"Ihre Adresse konnte nicht gefunden werden, daher wurde nun die Universität als Heimatadresse gesetzt. Bitte kontrollieren Sie Ihre Adresse im Fenster 'Konto bearbeiten'.",
							"Adresse nicht gefunden", "Adresse nicht gefunden", AlertType.WARNING, false, false);
					userLatLong = new LatLong(51.464185, 7.005747); // LatLong der UDE
				} else if (results.length > 1) {
					System.out.println(
							"Maps - Mehrere übereinstimmende User-Adressen gefunden, nutze den ersten Treffer.");
					userLatLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(),
							results[0].getGeometry().getLocation().getLongitude());
				} else {
					userLatLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(),
							results[0].getGeometry().getLocation().getLongitude());
				}

				// Map-Einstellungen setzen und Map initialisieren
				MapOptions mapOptions = new MapOptions();
				mapOptions.center(userLatLong).mapType(MapTypeIdEnum.ROADMAP).overviewMapControl(false)
						.panControl(false).rotateControl(false).scaleControl(false).streetViewControl(false)
						.zoomControl(false).zoom(11);

				map = mapView.createMap(mapOptions);

				// User Standort Marker hinzufügen
				MarkerOptions homeMarkerOptions = new MarkerOptions();

				homeMarkerOptions.position(userLatLong).visible(Boolean.TRUE)
						.icon(MarkerImageFactory
								.createMarkerImage(getClass().getResource("/SEPClient/UI/home.png").toString(), "png"))
						.title("Ihr Standort");

				Marker homeMarker = new Marker(homeMarkerOptions);
				map.addUIEventHandler(homeMarker, UIEventType.click, (JSObject obj) -> {
					// Home-Marker geklickt
				});

				map.addMarker(homeMarker);

				// Auktionen laden
				addAuctionsToMap(25);
			}
		});
	}

	private void addAuctionsToMap(double radius) {
		removeMarkersFromMap();

		// Counter reseten
		addressNotFoundCount = 0;
		notInRadiusCount = 0;
		addedAuctions = 0;
		auctionCounter = 0;

		// Übergebene Auktionen zur Map hinzufügen

		if (allAuctions != null && allAuctions.size() > 0) {
			String auctAddress = allAuctions.get(auctionCounter).getSeller().getAddress().getStreet() + " "
					+ allAuctions.get(auctionCounter).getSeller().getAddress().getNumber() + ", "
					+ allAuctions.get(auctionCounter).getSeller().getAddress().getZipcode() + " "
					+ allAuctions.get(auctionCounter).getSeller().getAddress().getCity() + ", "
					+ allAuctions.get(auctionCounter).getSeller().getAddress().getCountry();
			geocodingService.geocode(auctAddress, new GeocodingServiceCallback() {

				@Override
				public void geocodedResultsReceived(GeocodingResult[] results, GeocoderStatus status) {
					LatLong auctLatLong = null;
					// Auktions-Adresse, falls möglich, zu LatLong konvertieren
					if (status == GeocoderStatus.ZERO_RESULTS) {
						System.out.println("Maps - Keine übereinstimmende Adresse zur Auktion "
								+ allAuctions.get(auctionCounter).getId() + " gefunden, ignoriere.");
					} else if (results.length > 1) {
						System.out.println("Maps - Mehrere übereinstimmende Adressen zur Auktion "
								+ allAuctions.get(auctionCounter).getId() + " gefunden, nutze den ersten Treffer.");
						auctLatLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(),
								results[0].getGeometry().getLocation().getLongitude());
					} else {
						auctLatLong = new LatLong(results[0].getGeometry().getLocation().getLatitude(),
								results[0].getGeometry().getLocation().getLongitude());
					}

					if (auctLatLong == null) {
						addressNotFoundCount++;
					} else {
						// Adresse gefunden und LatLong zwischengespeichert. Distanz berechnen
						double distanceInMeters = auctLatLong.distanceFrom(userLatLong);
						double distanceInKm = distanceInMeters / 1000;

						// if(distanceInKm<=radius &&
						// allAuctions.get(auctionCounter).getSeller().getId()!=user.getId())

						// AUKTIONEN AN GENAU DEM SELBEN STANDORT WIE AKTUELLER USER WERDEN NICHT
						// DARGESTELLT.

						if (distanceInKm <= radius && (auctLatLong.getLatitude() != userLatLong.getLatitude()
								|| auctLatLong.getLongitude() != userLatLong.getLongitude())) {
							// im Radius und nicht an den Koordinaten des User-Standortes

							// prüfen ob eine Auktion an diesem Ort bereits hinzugefügt wurde
							boolean auctionAlreadyAddedAtThisLatLong = false;
							boolean multipleAuctionsAlreadyAtThisLatLong = false;
							boolean auctionsDifferentUsersSameAddressAtThisLatLong = false;

							int latLongId = 0;
							int counter = 0;

							for (LatLong ll : latLongList) {
								if (ll.getLatitude() == auctLatLong.getLatitude()
										&& ll.getLongitude() == auctLatLong.getLongitude()) {
									if (allAuctions.get(counter).getSeller().getId() == allAuctions.get(auctionCounter)
											.getSeller().getId()) {
										// Bestehende Auktion an dem Punkt vhd. von selbem Seller
										if (!markerList.get(counter).getTitle()
												.startsWith("Mehrere Auktionen des Benutzers")) {
											auctionAlreadyAddedAtThisLatLong = true;
											latLongId = counter;
											break;
										} else {
											// Marker "Mehrere Auktionen..." bereits vhd.
											multipleAuctionsAlreadyAtThisLatLong = true;
											latLongId = counter;
											break;
										}
									} else {
										// Bestehende Auktion an dem Punkt vhd. von anderem Seller
										if (!markerList.get(counter).getTitle()
												.equals("Mehrere Auktionen von unterschiedlichen Benutzern")) {
											// mehrere Auktionen von unterschiedlichen Usern an selber Adresse
											auctionsDifferentUsersSameAddressAtThisLatLong = true;
											latLongId = counter;
											break;
										} else {
											// bereits vhd.
											break;
										}
									}

								}
								counter++;
							}

							if (auctionAlreadyAddedAtThisLatLong) {
								map.removeMarker(markerList.get(latLongId));
								markerList.remove(markerList.get(latLongId));

								// Marker (mehrere Auktionen des Users) hinzufügen
								MarkerOptions auctMarkerOptions = new MarkerOptions();

								auctMarkerOptions.position(auctLatLong).visible(Boolean.TRUE)
										.title("Mehrere Auktionen des Benutzers '"
												+ allAuctions.get(auctionCounter).getSeller().getAddress().getFullname()
												+ "' (ID " + allAuctions.get(auctionCounter).getSeller().getId() + ")");

								Marker auctMarker = new Marker(auctMarkerOptions);
								int currentAuctionId = auctionCounter;
								map.addUIEventHandler(auctMarker, UIEventType.click, (JSObject obj) -> {
									auctionMarkerClicked(auctMarker, currentAuctionId);
								});

								map.addMarker(auctMarker);
								// Neuen Marker an selbe Stelle in der Liste setzen
								markerList.add(latLongId, auctMarker);
								addedAuctions++;
							} else if (auctionsDifferentUsersSameAddressAtThisLatLong) {
								map.removeMarker(markerList.get(latLongId));
								markerList.remove(markerList.get(latLongId));

								// Marker (mehrere Auktionen von unterschiedlichen Usern) hinzufügen
								MarkerOptions auctMarkerOptions = new MarkerOptions();

								auctMarkerOptions.position(auctLatLong).visible(Boolean.TRUE)
										.title("Mehrere Auktionen von unterschiedlichen Benutzern");

								Marker auctMarker = new Marker(auctMarkerOptions);
								int currentAuctionId = auctionCounter;
								map.addUIEventHandler(auctMarker, UIEventType.click, (JSObject obj) -> {
									auctionMarkerClicked(auctMarker, currentAuctionId);
								});

								map.addMarker(auctMarker);
								// Neuen Marker an selbe Stelle in der Liste setzen
								markerList.add(latLongId, auctMarker);
								addedAuctions++;
							} else if (multipleAuctionsAlreadyAtThisLatLong) {
								addedAuctions++;
							} else {
								// noch keine Auktion an diesen Koordinaten vorhanden
								// Auktions Marker hinzufügen
								MarkerOptions auctMarkerOptions = new MarkerOptions();

								auctMarkerOptions.position(auctLatLong).visible(Boolean.TRUE)
										.title("Auktion '" + allAuctions.get(auctionCounter).getTitle() + "'");

								Marker auctMarker = new Marker(auctMarkerOptions);
								int currentAuctionId = auctionCounter;
								map.addUIEventHandler(auctMarker, UIEventType.click, (JSObject obj) -> {
									auctionMarkerClicked(auctMarker, currentAuctionId);
								});

								map.addMarker(auctMarker);
								markerList.add(auctMarker);
								latLongList.add(auctLatLong);
								addedAuctions++;
							}
						} else {
							if (distanceInKm > radius) {
								notInRadiusCount++;
							}
						}
					}
					LabelAddedCount.setText("Im Radius: " + addedAuctions);
					LabelNotInRadiusCount.setText("Nicht im Radius: " + notInRadiusCount);
					LabelAddressNotFoundCount.setText("Adresse nicht zuordenbar: " + addressNotFoundCount);

					if (allAuctions.size() > (auctionCounter + 1)) {
						// rekursiv durchlaufen, da in einer for-Schleife problematisch mit Callback
						auctionCounter++;
						String auctAddress = allAuctions.get(auctionCounter).getSeller().getAddress().getStreet() + " "
								+ allAuctions.get(auctionCounter).getSeller().getAddress().getNumber() + ", "
								+ allAuctions.get(auctionCounter).getSeller().getAddress().getZipcode() + " "
								+ allAuctions.get(auctionCounter).getSeller().getAddress().getCity() + ", "
								+ allAuctions.get(auctionCounter).getSeller().getAddress().getCountry();

						geocodingService.geocode(auctAddress, this);
					}
				}
			});
		}
	}

	private void removeMarkersFromMap() {
		// Alle Marker, außer Home, von der Map entfernen
		if (markerList.size() > 0) {
			for (Marker marker : markerList) {
				map.removeMarker(marker);
			}
		}
		markerList.clear();
		latLongList.clear();
	}

	private void auctionMarkerClicked(Marker marker, int auctIndex) {
		if(marker!=null && auctIndex>=-1)
		{
			Label_SelectedAuction.setText("Selektierte Markierung: " + marker.getTitle());
			
			currentMarker = marker;
			currentAuctId = auctIndex;
			AuctionsMap_ButtonViewAuction.setDisable(false);
		}
		else {
			Label_SelectedAuction.setText("Selektierte Markierung: keine");
			currentMarker=null;
			currentAuctId=-1;
			AuctionsMap_ButtonViewAuction.setDisable(true);
		}
	}

	@FXML
	protected GoogleMapView mapView;

	@FXML
	private TextField AuctionMap_txtRadius;

	@FXML
	private Button AuctionsMap_ButtonShow;

	@FXML
	private Button AuctionsMap_ButtonViewAuction;

	@FXML
	private Button AuctionsMap_ButtonReturn;

	@FXML
	private Label LabelAddedCount;
	
	@FXML
	private Label Label_SelectedAuction;

	@FXML
	private Label LabelNotInRadiusCount;

	@FXML
	private Label LabelAddressNotFoundCount;

	@FXML
	void txtDistance_keyPressed(KeyEvent event) {
		// Taste wird gedrückt
		// Bei Enter: Button Klick simulieren
		if (event.getCode().equals(KeyCode.ENTER)) {
			AuctionsMap_ButtonShow.fire();
		}
	}
	
	@FXML
	void AuctionsMap_ButtonShow_Click(ActionEvent event) {
		double radius;
		try {
			radius = Double.parseDouble(AuctionMap_txtRadius.getText().replace(",", "."));
		} catch (NumberFormatException e) {
			FXMLHandler.ShowMessageBox("Bitte geben Sie einen Radius im folgenden Format ein: ##,##", "Fehler",
					"Fehler", AlertType.ERROR, false, false);
			AuctionMap_txtRadius.setText("");
			return;
		}

		if (radius <= 0) {
			FXMLHandler.ShowMessageBox("Bitte geben Sie einen Radius größer als 0 Kilometer ein", "Fehler", "Fehler",
					AlertType.ERROR, false, false);
			AuctionMap_txtRadius.setText("");
			return;
		}

		addAuctionsToMap(radius);
	}

	@FXML
	void AuctionsMap_ButtonViewAuction_Click(ActionEvent event) {
		if(currentMarker!=null && currentAuctId>=0)
		{
			String auctAddress = allAuctions.get(currentAuctId).getSeller().getAddress().getStreet() + " "
					+ allAuctions.get(currentAuctId).getSeller().getAddress().getNumber() + ", "
					+ allAuctions.get(currentAuctId).getSeller().getAddress().getZipcode() + " "
					+ allAuctions.get(currentAuctId).getSeller().getAddress().getCity() + ", "
					+ allAuctions.get(currentAuctId).getSeller().getAddress().getCountry();
			
			if (currentMarker.getTitle().startsWith("Mehrere Auktionen des Benutzers")) {
				FXMLHandler.ShowMessageBox("Standort: " + auctAddress + System.lineSeparator() + 
						"An dem selektierten Standort existieren mehrere Auktionen des Benutzers " + allAuctions.get(currentAuctId).getSeller().getAddress().getFullname() + " (ID " + allAuctions.get(currentAuctId).getSeller().getId() + "), weshalb keine Details angezeigt werden können.",
						"Mehrere Auktionen", "Mehrere Auktionen", AlertType.WARNING, false, false);
			} else if (currentMarker.getTitle().equals("Mehrere Auktionen von unterschiedlichen Benutzern")) {
				FXMLHandler.ShowMessageBox("Standort: " + auctAddress + System.lineSeparator() + 
						"An dem selektierten Standort existieren mehrere Auktionen von unterschiedlichen Benutzern, weshalb keine Details angezeigt werden können.",
						"Mehrere Auktionen", "Mehrere Auktionen", AlertType.WARNING, false, false);
			} else {
				String currentBidString = "Aktuelles Gebot: ";
				if (allAuctions.get(currentAuctId).getCurrentBid() == 0) {
					// kein Gebot
					currentBidString += "Kein Gebot";
				} else {
					// Gebot vhd.
					currentBidString += SEPCommon.Constants.DOUBLEFORMAT.format(allAuctions.get(currentAuctId).getCurrentBid())+ "$";
				}
				
				FXMLHandler.ShowMessageBox("Standort: " + auctAddress + System.lineSeparator() + 
						"Auktionszeitraum: " + allAuctions.get(currentAuctId).getStarttime().format(SEPCommon.Constants.DATEFORMAT)+ " - " + allAuctions.get(currentAuctId).getEnddate().format(SEPCommon.Constants.DATEFORMAT) + System.lineSeparator() + 
						"Startpreis: " + SEPCommon.Constants.DOUBLEFORMAT.format(allAuctions.get(currentAuctId).getStartPrice())+ "$" + System.lineSeparator() + 
						"Mindestgebot: " + SEPCommon.Constants.DOUBLEFORMAT.format(allAuctions.get(currentAuctId).getMinBid()) + "$" + System.lineSeparator() + 
						"Verkäufer: "+ allAuctions.get(currentAuctId).getSeller().getAddress().getFullname() + " (Benutzer " + allAuctions.get(currentAuctId).getSeller().getUsername() + ")" + System.lineSeparator() + 
						"Versandart: "+ allAuctions.get(currentAuctId).getShippingType().toString() + System.lineSeparator() + 
						currentBidString, "Auktionsdetails", "'" + allAuctions.get(currentAuctId).getTitle() + "'", AlertType.INFORMATION,false, false);
			}
		}
		else {
			FXMLHandler.ShowMessageBox("Die Auktionsdetails können momentan nicht angezeigt werden.", "Fehler", "Fehler", AlertType.ERROR, false, false);
		}
	}

	@FXML
	void AuctionsMap_ButtonReturn_Click(ActionEvent event) {
		FXMLHandler.OpenSceneInStage((Stage) AuctionsMap_ButtonReturn.getScene().getWindow(), "MainScreen",
				"Super-E-commerce-Platform", true, true);
	}
}
