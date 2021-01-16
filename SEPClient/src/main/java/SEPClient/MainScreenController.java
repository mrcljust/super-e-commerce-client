package SEPClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.maps.*;
import com.google.maps.model.LatLng;
import SEPCommon.Auction;
import SEPCommon.AuctionType;
import SEPCommon.ClientRequest;
import SEPCommon.Constants;
import SEPCommon.Customer;
import SEPCommon.Product;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.Seller;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class MainScreenController {

	private static User user = null;

	private Product[] lastSearchResult;
	private boolean currentSearchEvent = false;
	private boolean avoidCategoryChangedEvent = false;
	private boolean avoidClearAuctions = false;

	private LatLng userLatLong;
	public final static GeoApiContext context = new GeoApiContext.Builder().apiKey(SEPCommon.Constants.GOOGLE_APIKEY)
			.build();
	private CountDownLatch cdLatch = new CountDownLatch(1);

	private ObservableList<Product> fetchedObservableProducts = null;
	private int productCounter = 0;
	private List<Integer> addedIds = new ArrayList<Integer>();
	private List<Integer> addedIdsLastViewed = new ArrayList<Integer>();
	private boolean alreadyLoadedLastViewed = false;
	private boolean noLatLngFound = false;
	private boolean LatLngConnError = false;
	private boolean isRefresh = false;

	public static void setUser(User _user) {
		user = _user;
	}

	@FXML
	public void initialize() {
		startView();
		refreshViewArticles();

		selectionsChangedListener();
		categoryChangedListener();
		tabChangedListener();
	}

	private void startView() {
		// Wird einmal am Start aufgerufen um Zellwerte sowie ToggleGroups festzulegen

		// Werte an die Zellen der Katalogliste zuweisen
		catalogIdColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
		catalogProductColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
		catalogSellerColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("businessname"));

		catalogPriceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
		// Anzeigewert für Preis anpassen
		catalogPriceColumn.setCellFactory(tc -> new TableCell<Product, Double>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				if (empty || price == null) {
					setText(null);
				} else {
					setText(Constants.DOUBLEFORMAT.format(price) + Constants.CURRENCY);
				}
			}
		});

		catalogCategoryColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
		// Anzeigewert für Kategorie anpassen
		catalogCategoryColumn.setCellFactory(tc -> new TableCell<Product, String>() {
			@Override
			protected void updateItem(String category, boolean empty) {
				super.updateItem(category, empty);
				if (empty) {
					setText(null);
				} else if (category == "" || category == null) {
					setText("(Keine Kategorie)");
				} else {
					setText(category);
				}
			}
		});

		catalogDistanceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("distance"));
		// Anzeigewert für Distanz anpassen
		catalogDistanceColumn.setCellFactory(tc -> new TableCell<Product, Double>() {
			@Override
			protected void updateItem(Double distance, boolean empty) {
				super.updateItem(distance, empty);
				if (empty) {
					setText(null);
				} else if (distance == -1) {
					setText("(Unbekannt)");
				} else {
					setText(Constants.DOUBLEFORMAT.format(distance) + "km");
				}
			}
		});

		// Werte an die Zellen der LastViewedListe zuweisen
		lastviewedIdColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
		lastviewedProductColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
		lastviewedSellerColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("businessname"));

		lastviewedPriceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
		// Anzeigewert für Preis anpassen
		lastviewedPriceColumn.setCellFactory(tc -> new TableCell<Product, Double>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				if (empty || price == null) {
					setText(null);
				} else {
					setText(Constants.DOUBLEFORMAT.format(price) + Constants.CURRENCY);
				}
			}
		});

		lastviewedCategoryColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("category"));
		// Anzeigewert für Kategorie anpassen
		lastviewedCategoryColumn.setCellFactory(tc -> new TableCell<Product, String>() {
			@Override
			protected void updateItem(String category, boolean empty) {
				super.updateItem(category, empty);
				if (empty) {
					setText(null);
				} else if (category == "" || category == null) {
					setText("(Keine Kategorie)");
				} else {
					setText(category);
				}
			}
		});

		lastviewedDistanceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("distance"));
		// Anzeigewert für Distanz anpassen
		lastviewedDistanceColumn.setCellFactory(tc -> new TableCell<Product, Double>() {
			@Override
			protected void updateItem(Double distance, boolean empty) {
				super.updateItem(distance, empty);
				if (empty) {
					setText(null);
				} else if (distance == -1) {
					setText("(Unbekannt)");
				} else {
					setText(Constants.DOUBLEFORMAT.format(distance) + "km");
				}
			}
		});

		// Werte an die Zellen der Auktionslisten zuweisen
		auctionsCatalogIdColumn.setCellValueFactory(new PropertyValueFactory<Auction, Integer>("id"));
		auctionsCatalogTitleColumn.setCellValueFactory(new PropertyValueFactory<Auction, String>("title"));

		auctionsCatalogCurrentBidColumn.setCellValueFactory(new PropertyValueFactory<Auction, Double>("currentBid"));
		// Anzeigewert für Gebot anpassen
		auctionsCatalogCurrentBidColumn.setCellFactory(tc -> new TableCell<Auction, Double>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				if (empty || price == null) {
					setText(null);
				} else {
					if (price == 0) {
						setText("Kein Gebot");
					} else {
						setText(Constants.DOUBLEFORMAT.format(price) + Constants.CURRENCY);
					}
				}
			}
		});

		auctionsCatalogMinBidColumn.setCellValueFactory(new PropertyValueFactory<Auction, Double>("minBid"));
		// Anzeigewert für Gebot anpassen
		auctionsCatalogMinBidColumn.setCellFactory(tc -> new TableCell<Auction, Double>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				if (empty || price == null) {
					setText(null);
				} else {
					setText(Constants.DOUBLEFORMAT.format(price) + Constants.CURRENCY);
				}
			}
		});

		auctionsCatalogStartColumn.setCellValueFactory(new PropertyValueFactory<Auction, LocalDateTime>("starttime"));
		// Anzeigewert für Startdatum anpassen
		auctionsCatalogStartColumn.setCellFactory(tc -> new TableCell<Auction, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime date, boolean empty) {
				super.updateItem(date, empty);
				if (empty) {
					setText("Kein Datum");
				}
				if (date == null) {
					setText(null);
				} else {
					setText(date.format(SEPCommon.Constants.DATEFORMAT));
				}
			}
		});

		auctionsCatalogEndColumn.setCellValueFactory(new PropertyValueFactory<Auction, LocalDateTime>("enddate"));
		// Anzeigewert für Enddatum anpassen
		auctionsCatalogEndColumn.setCellFactory(tc -> new TableCell<Auction, LocalDateTime>() {
			@Override
			protected void updateItem(LocalDateTime date, boolean empty) {
				super.updateItem(date, empty);
				if (empty) {
					setText("Kein Datum");
				}
				if (date == null) {
					setText(null);
				} else {
					setText(date.format(SEPCommon.Constants.DATEFORMAT));
				}
			}
		});

		// ToggleGroups
		ToggleGroup radioViewAuctionsGroup = new ToggleGroup();
		radioAllAuctions.setToggleGroup(radioViewAuctionsGroup);
		radioMyBids.setToggleGroup(radioViewAuctionsGroup);
		radioMyAuctions.setToggleGroup(radioViewAuctionsGroup);
		radioSavedAuctions.setToggleGroup(radioViewAuctionsGroup);

		ToggleGroup radioAuctionTypeGroup = new ToggleGroup();
		radioCurrentAuctions.setToggleGroup(radioAuctionTypeGroup);
		radioEndedAuctions.setToggleGroup(radioAuctionTypeGroup);
		radioFutureAuctions.setToggleGroup(radioAuctionTypeGroup);
	}

	private void refreshUserDetails() {
		// Aktualisierte Nutzerdetails laden
		MainScreen_LabelWallet.setText("Guthaben: "
				+ Constants.DOUBLEFORMAT.format(SEPCommon.Methods.round(user.getWallet(), 2)) + Constants.CURRENCY);

		// Standardbild setzen
		Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
		MainScreen_ImgProfilePicture.setImage(defaultImage);

		if (user instanceof Seller) {
			// Gewerbekunde
			MainScreen_LabelLoggedInAs
					.setText("Angemeldet als: " + user.getUsername() + " (ID " + user.getId() + ", Gewerbekunde)");
			MainScreen_ButtonAddWallet.setDisable(true);
			MainScreen_ButtonSellProduct.setDisable(false);
			MainScreen_ButtonPurchases.setDisable(true);
			MainScreen_ButtonSales.setDisable(false);
			MainScreen_ButtonCreateAuction.setDisable(true);
		} else {
			// Privatkunde
			MainScreen_LabelLoggedInAs
					.setText("Angemeldet als: " + user.getUsername() + " (ID " + user.getId() + ", Privatkunde)");
			MainScreen_ButtonAddWallet.setDisable(false);
			MainScreen_ButtonSellProduct.setDisable(true);
			MainScreen_ButtonPurchases.setDisable(false);
			MainScreen_ButtonSales.setDisable(false);
			MainScreen_ButtonCreateAuction.setDisable(false);
		}

		// Bild setzen
		InputStream in = new ByteArrayInputStream(user.getPicture());
		Image img = new Image(in);
		if (!img.isError()) {
			MainScreen_ImgProfilePicture.setImage(img);
		}
	}

	private void refreshViewArticles() {
		refreshUserDetails();

		// Aktuelle Produktdetails leeren
		clearProductDetails();
		AnchorPaneArticleDetails.setVisible(true);
		AnchorPaneAuctionDetails.setVisible(false);

		// Selektierte Artikel ggf. deselektieren
		if (MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null) {
			MainScreen_ListCatalog.getSelectionModel().clearSelection();
		}

		if (MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null) {
			MainScreen_ListLastViewed.getSelectionModel().clearSelection();
		}

		// Suche leeren
		MainScreen_txtSearch.setText("");
		currentSearchEvent = false;
		lastSearchResult = null;

		// Produkte laden
		LoadAllProducts();
		// loadLastViewedProducts(); wird in LoadAllProducts aufgerufen

		// Alle Kategorien auswählen
		MainScreen_ChoiceBox_Category.getSelectionModel().select(0);
	}

	private void clearProductDetails() {
		// Aktuelle Produktinfos leeren
		MainScreen_LabelProductTitle.setText("");
		MainScreen_LabelProductPrice.setText("");
		MainScreen_LabelReducedPrice.setText("");
		MainScreen_LabelProductSeller.setText("");
		MainScreen_LabelProductCategory.setText("");
		MainScreen_txtAverageRating.setText("");
		MainScreen_txtRatingCount.setText("");
		MainScreen_WebViewProductDescription.getEngine().loadContent("");
		MainScreen_ButtonBuyProduct.setVisible(false);
		MainScreen_ButtonShowRatings.setVisible(false);
		MainScreen_WebViewProductDescription.setVisible(false);

		MainScreen_LabelAlsoBought.setVisible(false);
		MainScreen_ListAlsoBought.setVisible(false);
		MainScreen_ButtonSendMessage.setVisible(false);
		MainScreen_ButtonUpdatePrice.setVisible(false);
	}

	private void refreshViewAuctions(AuctionType auctionType) {
		// AuctionType auctionType = aktuell ausgewaehltes Fenster

		refreshUserDetails();

		// Aktuelle Auktionsdetails leeren
		clearAuctionDetails();
		AnchorPaneAuctionDetails.setVisible(true);
		AnchorPaneArticleDetails.setVisible(false);

		// Selektierte Auktion ggf. deselektieren
		if (MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null) {
			MainScreen_ListAuctions.getSelectionModel().clearSelection();
		}

		if (auctionType != AuctionType.Active) {
			MainScreen_ShowAuctionsOnMap.setDisable(true);
		}

		// Ansicht anpassen, Auktionen laden
		if (auctionType == AuctionType.Active) {
			radioAllAuctions.setSelected(true);
			radioCurrentAuctions.setSelected(true);

			radioCurrentAuctions.setVisible(true);
			radioEndedAuctions.setVisible(true);
			radioFutureAuctions.setVisible(true);

			MainScreen_txtSearchAuctions.setText("");
			MainScreen_txtSearchAuctions.setVisible(true);
			MainScreen_btnAuctionsSearchOK.setVisible(true);

			auctionsCatalogCurrentBidColumn.setText("Aktuelles Gebot");

			MainScreen_ShowAuctionsOnMap.setDisable(false);

			// Falls ein Suchbegriff eingegeben wurde, wird die Suche ausgeführt, ansonsten
			// alle aktuellen Auktionen geladen
			auctionsSearchChangedEvent(AuctionType.Active);
		} else if (auctionType == AuctionType.Ended) {
			radioAllAuctions.setSelected(true);
			radioEndedAuctions.setSelected(true);

			radioCurrentAuctions.setVisible(true);
			radioEndedAuctions.setVisible(true);
			radioFutureAuctions.setVisible(true);

			MainScreen_txtSearchAuctions.setText("");
			MainScreen_txtSearchAuctions.setVisible(true);
			MainScreen_btnAuctionsSearchOK.setVisible(true);

			auctionsCatalogCurrentBidColumn.setText("Höchstgebot");

			// Falls ein Suchbegriff eingegeben wurde, wird die Suche ausgeführt, ansonsten
			// alle geendeten Auktionen geladen
			auctionsSearchChangedEvent(AuctionType.Ended);
		} else if (auctionType == AuctionType.Future) {
			radioAllAuctions.setSelected(true);
			radioFutureAuctions.setSelected(true);

			radioCurrentAuctions.setVisible(true);
			radioEndedAuctions.setVisible(true);
			radioFutureAuctions.setVisible(true);

			MainScreen_txtSearchAuctions.setText("");
			MainScreen_txtSearchAuctions.setVisible(true);
			MainScreen_btnAuctionsSearchOK.setVisible(true);

			auctionsCatalogCurrentBidColumn.setText("Gebot");

			// Falls ein Suchbegriff eingegeben wurde, wird die Suche ausgeführt, ansonsten
			// alle zukünftigen Auktionen geladen
			auctionsSearchChangedEvent(AuctionType.Future);
		} else if (auctionType == AuctionType.SavedAuctions) {
			radioSavedAuctions.setSelected(true);

			radioCurrentAuctions.setSelected(false);
			radioEndedAuctions.setSelected(false);
			radioFutureAuctions.setSelected(false);

			radioCurrentAuctions.setVisible(false);
			radioEndedAuctions.setVisible(false);
			radioFutureAuctions.setVisible(false);

			MainScreen_txtSearchAuctions.setText("");
			MainScreen_txtSearchAuctions.setVisible(false);
			MainScreen_btnAuctionsSearchOK.setVisible(false);

			auctionsCatalogCurrentBidColumn.setText("Gebot");

			// Alle vom User gespeicherten Auktionen werden geladen
			MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.SavedAuctions));
		} else if (auctionType == AuctionType.MyBids) {
			radioMyBids.setSelected(true);

			radioCurrentAuctions.setSelected(false);
			radioEndedAuctions.setSelected(false);
			radioFutureAuctions.setSelected(false);

			radioCurrentAuctions.setVisible(false);
			radioEndedAuctions.setVisible(false);
			radioFutureAuctions.setVisible(false);

			MainScreen_txtSearchAuctions.setText("");
			MainScreen_txtSearchAuctions.setVisible(false);
			MainScreen_btnAuctionsSearchOK.setVisible(false);

			auctionsCatalogCurrentBidColumn.setText("Aktuelles Gebot");

			// Alle Auktionen mit Geboten vom User werden geladen
			MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.MyBids));
		} else if (auctionType == AuctionType.MyAuctions) {
			radioMyAuctions.setSelected(true);

			radioCurrentAuctions.setSelected(false);
			radioEndedAuctions.setSelected(false);
			radioFutureAuctions.setSelected(false);

			radioCurrentAuctions.setVisible(false);
			radioEndedAuctions.setVisible(false);
			radioFutureAuctions.setVisible(false);

			MainScreen_txtSearchAuctions.setText("");
			MainScreen_txtSearchAuctions.setVisible(false);
			MainScreen_btnAuctionsSearchOK.setVisible(false);

			auctionsCatalogCurrentBidColumn.setText("Gebot");

			// Alle vom User eingestellten Auktionen werden geladen
			MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.MyAuctions));
		}

	}

	private void clearAuctionDetails() {
		// Aktuelle Auktionsinfos leeren
		MainScreen_LabelProductTitleAuction.setText("");
		MainScreen_LabelMinBidAuction.setText("");
		MainScreen_LabelStartPriceAuction.setText("");
		MainScreen_LabelAuctionSeller.setText("");
		MainScreen_LabelCurrentBidAuction.setText("");
		MainScreen_LabelShippingAuction.setText("");
		MainScreen_LabelTimeAuction.setText("");
		MainScreen_txtAverageRatingAuction.setText("");
		MainScreen_txtRatingCountAuction.setText("");
		MainScreen_WebViewAuctionDescription.getEngine().loadContent("");
		MainScreen_ImgAuction.setVisible(false);
		MainScreen_txtDollarBidAmount.setVisible(false);
		MainScreen_TextboxBidAmount.setVisible(false);
		MainScreen_ButtonBidAuction.setVisible(false);
		MainScreen_ButtonSaveAuction.setVisible(false);
		MainScreen_ButtonShowRatingsAuction.setVisible(false);
		MainScreen_ButtonSendMessageAuction.setVisible(false);
		MainScreen_WebViewAuctionDescription.setVisible(false);
	}

	private void getUserLatLong() {
		// QUELLE: https://github.com/googlemaps/google-maps-services-java

		String userAddress = user.getAddress().getStreet() + " " + user.getAddress().getNumber() + ", "
				+ user.getAddress().getZipcode() + " " + user.getAddress().getCity() + ", "
				+ user.getAddress().getCountry();

		GeocodingApi.geocode(context, userAddress)
				.setCallback(new PendingResult.Callback<com.google.maps.model.GeocodingResult[]>() {
					@Override
					public void onResult(com.google.maps.model.GeocodingResult[] result) {
						if (result.length == 0) {
							System.out.println(
									"Maps - Keine übereinstimmende User-Adresse gefunden, nutze Adresse der Universität.");
							noLatLngFound = true;
							userLatLong = new LatLng(51.464185, 7.005747); // LatLong der UDE
						} else if (result.length > 1) {
							System.out.println(
									"Maps - Mehrere übereinstimmende User-Adressen gefunden, nutze den ersten Treffer.");
							userLatLong = result[0].geometry.location;
						} else {
							userLatLong = result[0].geometry.location;
						}
						cdLatch.countDown();
					}

					@Override
					public void onFailure(Throwable e) {
						System.out.println(
								"Maps - Keine übereinstimmende User-Adresse gefunden, vermutlich keine Verbindung.");
						LatLngConnError = true;
						userLatLong = new LatLng(51.464185, 7.005747); // LatLong der UDE
						cdLatch.countDown();
					}
				});
	}

	private void LoadAllProducts() {
		if (userLatLong == null) {
			getUserLatLong();
			try {
				cdLatch.await(10, TimeUnit.SECONDS);
				if(noLatLngFound)
				{
					FXMLHandler.ShowMessageBox(
							"Ihre Adresse konnte nicht gefunden werden, vermutlich haben Sie eine ungültige Adresse angegeben. Zur Berechnung der Entfernung wird daher nun die Adresse der Universität genutzt. Bitte kontrollieren Sie Ihre Adresse im Fenster 'Konto bearbeiten' und Ihre Internetverbindung.",
							"Adresse nicht gefunden", "Adresse nicht gefunden", AlertType.WARNING, false, false);
					noLatLngFound=false;
				}
				else if(LatLngConnError)
				{
					FXMLHandler.ShowMessageBox(
							"Ihre Adresse konnte nicht gefunden werden, vermutlich konnte keine Verbindung zum Google Maps Server hergestellt werden, weshalb die Entfernungen nicht dargestellt werden. Bitte kontrollieren Sie Ihre Internetverbindung und Ihre Adresse im Fenster 'Konto bearbeiten'.",
							"Adresse nicht gefunden", "Adresse nicht gefunden", AlertType.WARNING, false, false);
					LatLngConnError=false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				FXMLHandler.ShowMessageBox(
						"Ihre Adresse konnte nicht gefunden werden, vermutlich konnte keine Verbindung zum Google Maps Server hergestellt werden, weshalb die Entfernungen nicht dargestellt werden. Bitte kontrollieren Sie Ihre Internetverbindung und Ihre Adresse im Fenster 'Konto bearbeiten'.",
						"Adresse nicht gefunden", "Adresse nicht gefunden", AlertType.WARNING, false, false);
			}
		}
		fetchedObservableProducts = null;
		productCounter = 0;
		addedIds = new ArrayList<Integer>();

		MainScreen_Load_Catalog.setVisible(true);

		if (MainScreen_ListCatalog.getItems() != null) {
			MainScreen_ListCatalog.getItems().clear();
		}

		if (MainScreen_ChoiceBox_Category.getItems() != null) {
			MainScreen_ChoiceBox_Category.getItems().clear();
			// Alle Kategorien Item hinzufügen
			MainScreen_ChoiceBox_Category.getItems().add("Alle Kategorien");
			MainScreen_ChoiceBox_Category.getSelectionModel().select("Alle Kategorien");
		}

		ClientRequest req = new ClientRequest(Request.FetchProducts, null);
		Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);

		if (queryResponse != null && queryResponse.getResponseMap() != null
				&& queryResponse.getResponseMap().get("Products") != null) {
			// Product Array
			Product[] products = (Product[]) queryResponse.getResponseMap().get("Products");

			// Sorter Quelle:
			// https://stackoverflow.com/questions/27711923/javafx-sort-listview
			// answered Mar 22 '19 at 16:49 S. Sean
			fetchedObservableProducts = FXCollections.observableArrayList(products).sorted((o1, o2) -> {
				if (o1.getId() == o2.getId())
					return 0;
				if (o1.getId() > o2.getId())
					return 1;
				else
					return 0;
			});
			fetchedObservableProducts.removeIf(n -> (n == null));

			// Kategorien in Liste einfügen
			for (Product p : products) {
				String pCategory = p.getCategory();
				if (!MainScreen_ChoiceBox_Category.getItems().contains(pCategory)) {
					if (pCategory != "") // leeren Kategorie-String nicht hinzufügen
					{
						MainScreen_ChoiceBox_Category.getItems().add(pCategory);
					}
				}
			}
			addDistanceAndShowInCatalog();
		}
	}
	
	private void addDistanceAndShowInCatalog()
	{
		if (fetchedObservableProducts != null) {
			// QUELLE: https://github.com/googlemaps/google-maps-services-java
			String productAddress = fetchedObservableProducts.get(productCounter).getSeller().getAddress().getStreet()
					+ " " + fetchedObservableProducts.get(productCounter).getSeller().getAddress().getNumber() + ", "
					+ fetchedObservableProducts.get(productCounter).getSeller().getAddress().getZipcode() + " "
					+ fetchedObservableProducts.get(productCounter).getSeller().getAddress().getCity() + ", "
					+ fetchedObservableProducts.get(productCounter).getSeller().getAddress().getCountry();
			GeocodingApi.geocode(context, productAddress)
					.setCallback(new PendingResult.Callback<com.google.maps.model.GeocodingResult[]>() {
						@Override
						public void onResult(com.google.maps.model.GeocodingResult[] result) {
							LatLng productLatLong = null;
							// Produkt-Adresse, falls möglich, zu LatLong konvertieren
							if (result.length == 0) {
								System.out.println("Maps - Keine übereinstimmende Adresse zum Produkt " + fetchedObservableProducts.get(productCounter).getId()
										+ " gefunden, ignoriere.");
							} else if (result.length > 1) {
								System.out.println("Maps - Mehrere übereinstimmende Adressen zum Produkt "
										+ fetchedObservableProducts.get(productCounter).getId() + " gefunden, nutze den ersten Treffer.");
								productLatLong = result[0].geometry.location;
							} else {
								productLatLong = result[0].geometry.location;
							}

							// Distanz berechnen
							if (productLatLong == null) {
								fetchedObservableProducts.get(productCounter).setDistance(-1.0);
							} else {
								// Adresse gefunden und LatLong zwischengespeichert. Distanz berechnen
								double distanceInMeters = SEPCommon.Methods.getDistance(productLatLong.lat,
										productLatLong.lng, userLatLong.lat, userLatLong.lng);
								double distanceInKm = distanceInMeters / 1000;
								fetchedObservableProducts.get(productCounter).setDistance(distanceInKm);
							}

							if (!addedIds.contains(fetchedObservableProducts.get(productCounter).getId())) {
								MainScreen_ListCatalog.getItems().add(fetchedObservableProducts.get(productCounter));
								addedIds.add(fetchedObservableProducts.get(productCounter).getId());
							}
							if (fetchedObservableProducts.size() > (productCounter + 1)) {
								// rekursiv durchlaufen, da in einer for-Schleife problematisch mit Callback
								productCounter++;
								String auctAddress = fetchedObservableProducts.get(productCounter).getSeller()
										.getAddress().getStreet()
										+ " "
										+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
												.getNumber()
										+ ", "
										+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
												.getZipcode()
										+ " "
										+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
												.getCity()
										+ ", " + fetchedObservableProducts.get(productCounter).getSeller().getAddress()
												.getCountry();
								
								if(fetchedObservableProducts.get(productCounter).getId()==17)
								{
									System.out.println(auctAddress);
								}

								GeocodingApi.geocode(context, auctAddress).setCallback(this);
							} else if (!alreadyLoadedLastViewed) {
								MainScreen_Load_Catalog.setVisible(false);
								if(isRefresh)
								{
									isRefresh=false;
								}
								else {
									loadLastViewedProducts();
								}
							}
							else
							{
								MainScreen_Load_Catalog.setVisible(false);
							}
						}

						@Override
						public void onFailure(Throwable e) {
							if (fetchedObservableProducts != null) {
								fetchedObservableProducts.get(productCounter).setDistance(-1.0);
								if (!addedIds.contains(fetchedObservableProducts.get(productCounter).getId())) {
									MainScreen_ListCatalog.getItems()
											.add(fetchedObservableProducts.get(productCounter));
									addedIds.add(fetchedObservableProducts.get(productCounter).getId());
								}
								if (fetchedObservableProducts.size() > (productCounter + 1)) {
									// rekursiv durchlaufen, da in einer for-Schleife problematisch mit Callback
									productCounter++;
									String auctAddress = fetchedObservableProducts.get(productCounter).getSeller()
											.getAddress().getStreet()
											+ " "
											+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
													.getNumber()
											+ ", "
											+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
													.getZipcode()
											+ " "
											+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
													.getCity()
											+ ", " + fetchedObservableProducts.get(productCounter).getSeller()
													.getAddress().getCountry();

									GeocodingApi.geocode(context, auctAddress).setCallback(this);
								} else if (!alreadyLoadedLastViewed) {
									MainScreen_Load_Catalog.setVisible(false);
									if(isRefresh)
									{
										isRefresh=false;
									}
									else {
										loadLastViewedProducts();
									}
								}
								else
								{
									MainScreen_Load_Catalog.setVisible(false);
								}
							} else if (!alreadyLoadedLastViewed) {
								MainScreen_Load_Catalog.setVisible(false);
								if(isRefresh)
								{
									isRefresh=false;
								}
								else {
									loadLastViewedProducts();
								}
							}
							else
							{
								MainScreen_Load_Catalog.setVisible(false);
							}
						}
					});
		} else if (!alreadyLoadedLastViewed) {
			MainScreen_Load_Catalog.setVisible(false);
			if(isRefresh)
			{
				isRefresh=false;
			}
			else {
				loadLastViewedProducts();
			}
		}
		else
		{
			MainScreen_Load_Catalog.setVisible(false);
		}
	}

	private void loadLastViewedProducts() {
		if (userLatLong == null) {
			getUserLatLong();
			try {
				cdLatch.await(10, TimeUnit.SECONDS);
				if(noLatLngFound)
				{
					FXMLHandler.ShowMessageBox(
							"Ihre Adresse konnte nicht gefunden werden, vermutlich haben Sie eine ungültige Adresse angegeben. Zur Berechnung der Entfernung wird daher nun die Adresse der UDE genutzt. Bitte kontrollieren Sie Ihre Adresse im Fenster 'Konto bearbeiten' und Ihre Internetverbindung.",
							"Adresse nicht gefunden", "Adresse nicht gefunden", AlertType.WARNING, false, false);
					noLatLngFound=false;
				}
				else if(LatLngConnError)
				{
					FXMLHandler.ShowMessageBox(
							"Ihre Adresse konnte nicht gefunden werden, vermutlich konnte keine Verbindung zum Google Maps Server hergestellt werden. Bitte kontrollieren Sie Ihre Internetverbindung und Ihre Adresse im Fenster 'Konto bearbeiten'.",
							"Adresse nicht gefunden", "Adresse nicht gefunden", AlertType.WARNING, false, false);
					LatLngConnError=false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zu Google Maps hergestellt werden, weshalb die Entfernungen nicht dargestellt werden.", "Keine Verbindung", "Keine Verbindung", AlertType.ERROR, true, false);
				return;
			}
		}
		fetchedObservableProducts = null;
		productCounter = 0;
		addedIdsLastViewed = new ArrayList<Integer>();

		MainScreen_Load_LastViewed.setVisible(true);

		if (MainScreen_ListLastViewed.getItems() != null) {
			MainScreen_ListLastViewed.getItems().clear();
		}

		HashMap<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("User", user);
		ClientRequest req = new ClientRequest(Request.LastViewedProducts, requestMap);
		Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);

		if (queryResponse != null && queryResponse.getResponseMap() != null
				&& queryResponse.getResponseType() == Response.Success) {
			Product[] products = (Product[]) queryResponse.getResponseMap().get("Products");
			fetchedObservableProducts = FXCollections.observableArrayList(products);
			fetchedObservableProducts.removeIf(n -> (n == null));
		}

		if (fetchedObservableProducts != null) {
			// QUELLE: https://github.com/googlemaps/google-maps-services-java
			String productAddress = fetchedObservableProducts.get(productCounter).getSeller().getAddress().getStreet()
					+ " " + fetchedObservableProducts.get(productCounter).getSeller().getAddress().getNumber() + ", "
					+ fetchedObservableProducts.get(productCounter).getSeller().getAddress().getZipcode() + " "
					+ fetchedObservableProducts.get(productCounter).getSeller().getAddress().getCity() + ", "
					+ fetchedObservableProducts.get(productCounter).getSeller().getAddress().getCountry();
			GeocodingApi.geocode(context, productAddress)
					.setCallback(new PendingResult.Callback<com.google.maps.model.GeocodingResult[]>() {
						@Override
						public void onResult(com.google.maps.model.GeocodingResult[] result) {
							LatLng productLatLong = null;
							// Produkt-Adresse, falls möglich, zu LatLong konvertieren
							if (result.length == 0) {
								System.out.println("Maps - Keine übereinstimmende Adresse zum Produkt " + fetchedObservableProducts.get(productCounter).getId()
										+ " gefunden, ignoriere.");
							} else if (result.length > 1) {
								System.out.println("Maps - Mehrere übereinstimmende Adressen zum Produkt "
										+ fetchedObservableProducts.get(productCounter).getId() + " gefunden, nutze den ersten Treffer.");
								productLatLong = result[0].geometry.location;
							} else {
								productLatLong = result[0].geometry.location;
							}

							// Distanz berechnen
							if (productLatLong == null) {
								fetchedObservableProducts.get(productCounter).setDistance(-1.0);
							} else {
								// Adresse gefunden und LatLong zwischengespeichert. Distanz berechnen
								double distanceInMeters = SEPCommon.Methods.getDistance(productLatLong.lat,
										productLatLong.lng, userLatLong.lat, userLatLong.lng);
								double distanceInKm = distanceInMeters / 1000;
								fetchedObservableProducts.get(productCounter).setDistance(distanceInKm);
							}

							if (!addedIdsLastViewed.contains(fetchedObservableProducts.get(productCounter).getId())) {
								MainScreen_ListLastViewed.getItems().add(fetchedObservableProducts.get(productCounter));
								addedIdsLastViewed.add(fetchedObservableProducts.get(productCounter).getId());
							}

							if (fetchedObservableProducts.size() > (productCounter + 1)) {
								// rekursiv durchlaufen, da in einer for-Schleife problematisch mit Callback
								productCounter++;
								String auctAddress = fetchedObservableProducts.get(productCounter).getSeller()
										.getAddress().getStreet()
										+ " "
										+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
												.getNumber()
										+ ", "
										+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
												.getZipcode()
										+ " "
										+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
												.getCity()
										+ ", " + fetchedObservableProducts.get(productCounter).getSeller().getAddress()
												.getCountry();

								GeocodingApi.geocode(context, auctAddress).setCallback(this);
							} else {
								alreadyLoadedLastViewed = true;
								MainScreen_Load_LastViewed.setVisible(false);
							}
						}

						@Override
						public void onFailure(Throwable e) {
							if (fetchedObservableProducts != null) {
								fetchedObservableProducts.get(productCounter).setDistance(-1.0);

								if (!addedIdsLastViewed
										.contains(fetchedObservableProducts.get(productCounter).getId())) {
									MainScreen_ListLastViewed.getItems()
											.add(fetchedObservableProducts.get(productCounter));
									addedIdsLastViewed.add(fetchedObservableProducts.get(productCounter).getId());
								}

								if (fetchedObservableProducts.size() > (productCounter + 1)) {
									// rekursiv durchlaufen, da in einer for-Schleife problematisch mit Callback
									productCounter++;
									String auctAddress = fetchedObservableProducts.get(productCounter).getSeller()
											.getAddress().getStreet()
											+ " "
											+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
													.getNumber()
											+ ", "
											+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
													.getZipcode()
											+ " "
											+ fetchedObservableProducts.get(productCounter).getSeller().getAddress()
													.getCity()
											+ ", " + fetchedObservableProducts.get(productCounter).getSeller()
													.getAddress().getCountry();

									GeocodingApi.geocode(context, auctAddress).setCallback(this);
								} else {
									MainScreen_Load_LastViewed.setVisible(false);
									alreadyLoadedLastViewed = true;
								}
							} else {
								alreadyLoadedLastViewed = true;
								MainScreen_Load_LastViewed.setVisible(false);
							}
						}
					});
		} else {
			alreadyLoadedLastViewed = true;
			MainScreen_Load_LastViewed.setVisible(false);
		}
	}

	private ObservableList<Auction> LoadAuctions(AuctionType auctionType) {
		if (MainScreen_ListAuctions.getItems() != null) {
			if (avoidClearAuctions) {
				avoidClearAuctions = false;
			} else {
				MainScreen_ListAuctions.getItems().clear();
			}
		}

		HashMap<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("AuctionType", auctionType);

		if (auctionType == AuctionType.MyBids || auctionType == AuctionType.MyAuctions
				|| auctionType == AuctionType.SavedAuctions) {
			// für die AuktionsTypen wird ein User-Objekt benötigt
			requestMap.put("User", user);
		}

		ClientRequest req = new ClientRequest(Request.FetchAuctions, requestMap);
		Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);

		if (queryResponse != null && queryResponse.getResponseMap() != null
				&& queryResponse.getResponseMap().get("Auctions") != null) {
			Auction[] auctions = (Auction[]) queryResponse.getResponseMap().get("Auctions");
			ObservableList<Auction> ObservableAuctions = FXCollections.observableArrayList(auctions);
			ObservableAuctions.removeIf(n -> (n == null));

			return ObservableAuctions;
		}
		return null;
	}

	private void categoryChangedListener() {
		// Listener mit Hilfe folgender Quelle geschrieben:
		// https://stackoverflow.com/questions/14522680/javafx-choicebox-events
		// Antwort von zhujik, Jan 25 '13 at 14:08

		// ChoiceBox Categories Selection Change Listener
		// wird aufgerufen, wenn eine Kategorie ausgewählt wird

		MainScreen_ChoiceBox_Category.getSelectionModel().selectedIndexProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						categoryChangedEvent(newValue.intValue());
					}
				});
	}

	private void selectionsChangedListener() {
		// Listener mit Hilfe folgender Quelle geschrieben:
		// https://stackoverflow.com/questions/26424769/javafx8-how-to-create-listener-for-selection-of-row-in-tableview
		// Antwort von James_D, Oct 17 '14 at 14:11

		// ListCatalog Selection Change Listener

		MainScreen_ListCatalog.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					// was passiert, wenn ein Eintrag in der ListCatalog ausgewählt wird
					if (newSelection != null) {
						updateArticleInfo(true);
						addToLastViewedItems();
					}
				});

		// ListLastViewed Selection Change Listener

		MainScreen_ListLastViewed.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					// was passiert, wenn ein Eintrag in der ListLastViewed ausgewählt wird
					if (newSelection != null) {
						updateArticleInfo(false);
					}
				});

		// MainScreen_ListAuctions Selection Change Listener

		MainScreen_ListAuctions.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					// Eintrag in ListAuctions ausgewaehlt
					if (newSelection != null) {
						updateAuctionInfo();
					}
				});
	}

	private void tabChangedListener() {
		// Quelle:
		// https://stackoverflow.com/questions/17522686/javafx-tabpane-how-to-listen-to-selection-changes
		// Antwort von Mohammad Jafar Mashhadi, Jul 8 '13 at 9:17

		// Listener prueft ob der gewaehlte Tab geaendert wird
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> ov, Tab oldTab, Tab newTab) {
				System.out.println(newTab.getText());
				if (newTab == tabLiveAuctions) {
					tabLiveAuctions_Select();
				} else if (newTab == tabArticles) {
					tabArticles_Select();
				}
			}
		});
	}

	private void showReducedPrice(boolean selectionInCatalog) {
		// selectionInCatalog = true --> Selektion im Katalog geändert
		// selectionInCatalog = false --> Selektion in LastViewed geändert
		if (selectionInCatalog == true) {
			if (MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getPrice() >= MainScreen_ListCatalog
					.getSelectionModel().getSelectedItem().getOldPrice()) {
				// aktueller Preis höher oder gleich alter Preis, zeige nur den aktuellen Preis
				MainScreen_LabelReducedPrice.setVisible(false);
			} else {
				double newprice = MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getPrice();
				double oldprice = MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getOldPrice();
				String diff = SEPCommon.Constants.DOUBLEFORMAT.format(100 - (newprice / oldprice * 100)); // auf zwei
																											// Nachkommastellen
				MainScreen_LabelReducedPrice.setText("Reduziert um " + diff + "% (Vorheriger Preis: "
						+ MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getOldPriceString() + ")");
				MainScreen_LabelReducedPrice.setVisible(true);
			}
		} else {
			if (MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getPrice() >= MainScreen_ListLastViewed
					.getSelectionModel().getSelectedItem().getOldPrice()) {
				// aktueller Preis höher oder gleich alter Preis, zeige nur den aktuellen Preis
				MainScreen_LabelReducedPrice.setVisible(false);
			} else {
				double newprice = MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getPrice();
				double oldprice = MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getOldPrice();
				String diff = SEPCommon.Constants.DOUBLEFORMAT.format(100 - (newprice / oldprice * 100)); // auf zwei
																											// Nachkommastellen
				MainScreen_LabelReducedPrice.setText("Reduziert um " + diff + "% (Vorheriger Preis: "
						+ MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getOldPriceString() + ")");
				MainScreen_LabelReducedPrice.setVisible(true);
			}
		}
	}

	private void showUsersAlsoBought(boolean selectionInCatalog) {
		// TODO DENIS

		// selectionInCatalog = true --> Selektion im Katalog geändert
		// selectionInCatalog = false --> Selektion in LastViewed geändert
		if (selectionInCatalog == true) {

		} else {

		}
	}

	private void updateArticleInfo(boolean selectionInCatalog) {
		// selectionInCatalog = true --> Selektion im Katalog geändert
		// selectionInCatalog = false --> Selektion in LastViewed geändert
		if (selectionInCatalog == true) {
			// Artikel in der ListCatalog ausgewählt
			if (MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null) {
				MainScreen_ListLastViewed.getSelectionModel().clearSelection();
				// Item in der Katalog-Liste angewählt

				// Daten einfügen
				MainScreen_LabelProductTitle
						.setText(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getName());
				MainScreen_LabelProductPrice.setText(
						"Preis: " + MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getPriceString());

				showReducedPrice(true);

				MainScreen_LabelProductSeller.setText("Verkäufer: "
						+ MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getBusinessname()
						+ " (Benutzer "
						+ MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
				MainScreen_txtAverageRating.setText("Durchschnittliche Bewertung: X.XX");
				MainScreen_txtRatingCount.setText("(Anzahl: XX)");
				String selectedCategory = MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getCategory();
				if (selectedCategory == "") {
					MainScreen_LabelProductCategory.setText("Kategorie: (Keine Kategorie)");
				} else {
					MainScreen_LabelProductCategory.setText("Kategorie: " + selectedCategory);
				}
				MainScreen_WebViewProductDescription.getEngine().loadContent(MainScreen_ListCatalog.getSelectionModel()
						.getSelectedItem().getDescription().replace(System.lineSeparator(), "<br/>")); // <br/> = neue
																										// Zeile in HTML

				HashMap<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("User", MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller());
				requestMap.put("FetchAvg", true);

				ClientRequest req = new ClientRequest(Request.FetchRatings, requestMap);
				Client client = Client.getClient();
				ServerResponse queryResponse = client.sendClientRequest(req);

				if (queryResponse.getResponseType() != null && queryResponse.getResponseType() == Response.Success) {
					Double ratingAvg = (Double) queryResponse.getResponseMap().get("Average");
					int ratingCount = (Integer) queryResponse.getResponseMap().get("Amount");
					if (ratingAvg != null) {
						MainScreen_txtAverageRating.setText(
								"Durchschnittliche Bewertung: " + SEPCommon.Constants.DOUBLEFORMAT.format(ratingAvg));
						MainScreen_txtRatingCount.setText("(Anzahl: " + ratingCount + ")");
						MainScreen_ButtonShowRatings.setDisable(false);
						MainScreen_txtRatingCount.setVisible(true);
					}
				} else {
					MainScreen_txtAverageRating.setText("Bisher keine Bewertung erhalten");
					MainScreen_ButtonShowRatings.setDisable(true);
					MainScreen_txtRatingCount.setVisible(false);
				}

				showUsersAlsoBought(true);

				MainScreen_ButtonShowRatings.setVisible(true);
				MainScreen_ButtonBuyProduct.setVisible(true);
				MainScreen_WebViewProductDescription.setVisible(true);

				MainScreen_LabelAlsoBought.setVisible(true);
				MainScreen_ListAlsoBought.setVisible(true);
				MainScreen_ButtonSendMessage.setVisible(true);

				// Kaufen Button nur für Customer enablen
				if (user instanceof Customer) {
					MainScreen_ButtonBuyProduct.setDisable(false);
				} else {
					MainScreen_ButtonBuyProduct.setDisable(true);
				}

				// Prüfen ob Verkäufer des Produkts = aktueller Nutzer ist, falls ja, Preis
				// bearbeiten aktivieren
				if (user.getId() == MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller().getId()) {
					MainScreen_ButtonUpdatePrice.setVisible(true);
					MainScreen_ButtonUpdatePrice.setDisable(false);
					MainScreen_ButtonSendMessage.setDisable(true);
				} else {
					MainScreen_ButtonUpdatePrice.setVisible(false);
					MainScreen_ButtonUpdatePrice.setDisable(true);
					MainScreen_ButtonSendMessage.setDisable(false);
				}
			}
		} else {
			// Artikel in ListLastViewed ausgewählt
			if (MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null) {
				MainScreen_ListCatalog.getSelectionModel().clearSelection();
				// Item in der zuletzt angesehen Liste angewählt

				// Daten einfügen
				MainScreen_LabelProductTitle
						.setText(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getName());
				MainScreen_LabelProductPrice.setText(
						"Preis: " + MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getPriceString());

				showReducedPrice(false);

				MainScreen_LabelProductSeller.setText("Verkäufer: "
						+ MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller().getBusinessname()
						+ " (Benutzer "
						+ MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller().getUsername()
						+ ")");
				MainScreen_txtAverageRating.setText("Durchschnittliche Bewertung: X.XX");
				MainScreen_txtRatingCount.setText("(Anzahl: XX)");
				String selectedCategory = MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getCategory();
				if (selectedCategory == "") {
					MainScreen_LabelProductCategory.setText("Kategorie: (Keine Kategorie)");
				} else {
					MainScreen_LabelProductCategory.setText("Kategorie: " + selectedCategory);
				}
				MainScreen_WebViewProductDescription.getEngine()
						.loadContent(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getDescription()
								.replace(System.lineSeparator(), "<br/>")); // <br/> = neue Zeile in HTML
				MainScreen_WebViewProductDescription.setDisable(true);

				HashMap<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("User", MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller());
				requestMap.put("FetchAvg", true);

				ClientRequest req = new ClientRequest(Request.FetchRatings, requestMap);
				Client client = Client.getClient();
				ServerResponse queryResponse = client.sendClientRequest(req);

				if (queryResponse.getResponseType() != null && queryResponse.getResponseType() == Response.Success) {
					Double ratingAvg = (Double) queryResponse.getResponseMap().get("Average");
					int ratingCount = (Integer) queryResponse.getResponseMap().get("Amount");
					if (ratingAvg != null) {
						MainScreen_txtAverageRating.setText(
								"Durchschnittliche Bewertung: " + SEPCommon.Constants.DOUBLEFORMAT.format(ratingAvg));
						MainScreen_txtRatingCount.setText("(Anzahl: " + ratingCount + ")");
						MainScreen_ButtonShowRatings.setDisable(false);
						MainScreen_txtRatingCount.setVisible(true);
					}
				} else {
					MainScreen_txtAverageRating.setText("Bisher keine Bewertung erhalten");
					MainScreen_ButtonShowRatings.setDisable(true);
					MainScreen_txtRatingCount.setVisible(false);
				}

				showUsersAlsoBought(false);

				MainScreen_ButtonShowRatings.setVisible(true);
				MainScreen_ButtonBuyProduct.setVisible(true);
				MainScreen_WebViewProductDescription.setVisible(true);

				MainScreen_LabelAlsoBought.setVisible(true);
				MainScreen_ListAlsoBought.setVisible(true);
				MainScreen_ButtonSendMessage.setVisible(true);

				// Kaufen Button nur für Customer enablen
				if (user instanceof Customer) {
					MainScreen_ButtonBuyProduct.setDisable(false);
				} else {
					MainScreen_ButtonBuyProduct.setDisable(true);
				}

				// Prüfen ob Verkäufer des Produkts = aktueller Nutzer ist, falls ja, Preis
				// bearbeiten aktivieren
				if (user.getId() == MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller()
						.getId()) {
					MainScreen_ButtonUpdatePrice.setVisible(true);
					MainScreen_ButtonUpdatePrice.setDisable(false);
					MainScreen_ButtonSendMessage.setDisable(true);
				} else {
					MainScreen_ButtonUpdatePrice.setVisible(false);
					MainScreen_ButtonUpdatePrice.setDisable(true);
					MainScreen_ButtonSendMessage.setDisable(false);
				}
			}
		}
	}

	private void updateAuctionInfo() {
		// Auktion angeklickt
		if (MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null) {
			// Daten einfügen

			MainScreen_LabelProductTitleAuction
					.setText(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getTitle());
			MainScreen_LabelMinBidAuction.setText("Mindestgebot: " + SEPCommon.Constants.DOUBLEFORMAT
					.format(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getMinBid()) + "$");
			MainScreen_LabelStartPriceAuction
					.setText("Startpreis: "
							+ SEPCommon.Constants.DOUBLEFORMAT.format(
									MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getStartPrice())
							+ "$");
			MainScreen_LabelAuctionSeller.setText("Verkäufer: "
					+ MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller().getAddress()
							.getFullname()
					+ " (Benutzer "
					+ MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller().getUsername() + ")");
			MainScreen_LabelShippingAuction.setText("Versandart: "
					+ MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getShippingType().toString());
			MainScreen_LabelTimeAuction.setText("Auktionszeitraum: "
					+ MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getStarttime()
							.format(SEPCommon.Constants.DATEFORMAT)
					+ " - " + MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getEnddate()
							.format(SEPCommon.Constants.DATEFORMAT));
			MainScreen_txtAverageRatingAuction.setText("");
			MainScreen_txtRatingCountAuction.setText("");
			MainScreen_WebViewAuctionDescription.getEngine().loadContent(MainScreen_ListAuctions.getSelectionModel()
					.getSelectedItem().getDescription().replace(System.lineSeparator(), "<br/>")); // <br/> = neue Zeile
																									// in HTML
			MainScreen_WebViewAuctionDescription.setDisable(true);
			String currentBidTextString = "";
			if (radioCurrentAuctions.isSelected() || radioMyBids.isSelected()) {
				currentBidTextString = "Aktuelles Gebot: ";
				MainScreen_LabelCurrentBidAuction.setVisible(true);
			} else if (radioSavedAuctions.isSelected() || radioMyAuctions.isSelected()) {
				currentBidTextString = "Gebot: ";
				MainScreen_LabelCurrentBidAuction.setVisible(true);
			} else if (radioFutureAuctions.isSelected()) {
				currentBidTextString = "Gebot: ";
				MainScreen_LabelCurrentBidAuction.setVisible(false);
			} else if (radioEndedAuctions.isSelected()) {
				currentBidTextString = "Höchstgebot: ";
				MainScreen_LabelCurrentBidAuction.setVisible(true);
			}

			if (MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getCurrentBid() == 0) {
				// kein Gebot
				MainScreen_LabelCurrentBidAuction.setText(currentBidTextString + "Kein Gebot");
			} else {
				// Gebot vhd.
				MainScreen_LabelCurrentBidAuction
						.setText(currentBidTextString
								+ SEPCommon.Constants.DOUBLEFORMAT.format(
										MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getCurrentBid())
								+ "$");
			}

			// Standardbild setzen
			Image defaultImage = new Image(getClass().getResource("/SEPClient/UI/no-image.jpg").toString());
			MainScreen_ImgAuction.setImage(defaultImage);

			InputStream in = new ByteArrayInputStream(
					MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getImage());
			Image img = new Image(in);
			if (!img.isError()) {
				MainScreen_ImgAuction.setImage(img);
			}
			MainScreen_ImgAuction.setVisible(true);

			MainScreen_ButtonSaveAuction.setVisible(true);

			HashMap<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("User", MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller());
			requestMap.put("FetchAvg", true);

			ClientRequest req = new ClientRequest(Request.FetchRatings, requestMap);
			Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);

			if (queryResponse.getResponseType() != null && queryResponse.getResponseType() == Response.Success) {
				Double ratingAvg = (Double) queryResponse.getResponseMap().get("Average");
				int ratingCount = (Integer) queryResponse.getResponseMap().get("Amount");
				if (ratingAvg != null) {
					MainScreen_txtAverageRatingAuction.setText(
							"Durchschnittliche Bewertung: " + SEPCommon.Constants.DOUBLEFORMAT.format(ratingAvg));
					MainScreen_txtRatingCountAuction.setText("(Anzahl: " + ratingCount + ")");
					MainScreen_ButtonShowRatingsAuction.setDisable(false);
					MainScreen_txtRatingCountAuction.setVisible(true);
				}
			} else {
				MainScreen_txtAverageRatingAuction.setText("Bisher keine Bewertung erhalten");
				MainScreen_ButtonShowRatingsAuction.setDisable(true);
				MainScreen_txtRatingCountAuction.setVisible(false);
			}

			MainScreen_TextboxBidAmount.setVisible(true);
			MainScreen_txtDollarBidAmount.setVisible(true);
			MainScreen_ButtonBidAuction.setVisible(true);

			MainScreen_TextboxBidAmount.setDisable(true);
			MainScreen_ButtonBidAuction.setDisable(true);
			MainScreen_ButtonSaveAuction.setDisable(false);

			MainScreen_ButtonSendMessageAuction.setVisible(true);

			if (user.getId() == MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller().getId()) {
				// Verkaeufer = aktueller Nutzer, nicht bieten lassen und Auktion merken
				// deaktivieren, keine Nachricht schicken lassen
				MainScreen_TextboxBidAmount.setDisable(true);
				MainScreen_ButtonBidAuction.setDisable(true);
				MainScreen_ButtonSaveAuction.setDisable(true);
				MainScreen_ButtonSendMessageAuction.setDisable(true);
			} else {
				MainScreen_ButtonSendMessageAuction.setDisable(false);
				if (radioCurrentAuctions.isSelected() || radioMyBids.isSelected() || radioSavedAuctions.isSelected()) {
					// auf beendete, zukuenftige, eigene Auktionen kann nicht geboten werden.
					MainScreen_TextboxBidAmount.setDisable(false);
					MainScreen_ButtonBidAuction.setDisable(false);
					MainScreen_ButtonSaveAuction.setDisable(false);
				}

				if (radioSavedAuctions.isSelected()) {
					// Wenn aktuell in der Liste gespeicherter Auktionen, Auktionen merken
					// deaktivieren, da die aktuell
					// selektierte Auktion bereits gemerkt ist
					MainScreen_ButtonSaveAuction.setDisable(true);
				}

				if (user instanceof Seller) {
					// Aktueller Nutzer ist Gewerbekunde, deaktiviere das Bieten
					MainScreen_TextboxBidAmount.setDisable(true);
					MainScreen_ButtonBidAuction.setDisable(true);
				}
			}

			MainScreen_ButtonShowRatingsAuction.setVisible(true);
			MainScreen_WebViewAuctionDescription.setVisible(true);

			// Kaufen Button nur für Customer enablen
			if (user instanceof Customer) {
				MainScreen_ButtonBuyProduct.setDisable(false);
			} else {
				MainScreen_ButtonBuyProduct.setDisable(true);
			}
		}
	}

	private void addToLastViewedItems() {
		// Zu zuletzt angesehenen Produkten hinzufügen, wenn nicht bereits vhd.
		Product viewedProduct = MainScreen_ListCatalog.getSelectionModel().getSelectedItem();

		boolean alreadyInLastViewed = false;

		// Vor ClientRequest an den Server pruefen ob das Produkt bereits in den zuletzt
		// angesehenen Produkten ist
		if (MainScreen_ListLastViewed.getItems() != null) {
			for (Product p : MainScreen_ListLastViewed.getItems()) {
				if (p != null && p.getId() == viewedProduct.getId()) {
					alreadyInLastViewed = true;
				}
			}
		}
		if (!alreadyInLastViewed) {
			// noch nicht in der Liste der zuletzt angesehenen Produkte, hinzufügen
			HashMap<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("User", user);
			requestMap.put("ViewedProductID", viewedProduct.getId());
			ClientRequest req = new ClientRequest(Request.AddLastViewedProduct, requestMap);
			Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			if (queryResponse.getResponseType() == Response.Success) {
				loadLastViewedProducts();
			}
		}
	}

	private void saveAuction() {
		// Zu gespeicherten Auktionen hinzufügen
		Auction viewedAuction = MainScreen_ListAuctions.getSelectionModel().getSelectedItem();

		boolean alreadyInSavedAuctions = false;

		// Vor ClientRequest an den Server pruefen ob die Auktion bereits bei dem User
		// gespeichert ist
		avoidClearAuctions = true;
		ObservableList<Auction> savedAuctions = LoadAuctions(AuctionType.SavedAuctions);

		if (savedAuctions != null) {
			for (Auction a : savedAuctions) {
				if (a != null && a.getId() == viewedAuction.getId()) {
					alreadyInSavedAuctions = true;
				}
			}
		}
		if (!alreadyInSavedAuctions) {
			// Auktion noch nicht bei User gespeichert, hinzufügen
			HashMap<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("Auction", viewedAuction);
			requestMap.put("User", user);
			ClientRequest req = new ClientRequest(Request.SaveAuction, requestMap);
			Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);
			if (queryResponse.getResponseType() == Response.Success) {
				// MainScreen_ButtonRefresh.fire();
				return;
			}
		} else {
			FXMLHandler.ShowMessageBox("Die ausgewählte Auktion ist bereits in Ihrer Merkliste gespeichert.",
					"Bereits gespeichert", "Bereits gespeichert", AlertType.WARNING, true, false);
			return;
		}
	}

	private void categoryChangedEvent(int newValue) {
		fetchedObservableProducts = null;
		productCounter = 0;
		addedIds = new ArrayList<Integer>();
		MainScreen_Load_Catalog.setVisible(true);
		// Katalog leeren

		if (MainScreen_ListCatalog.getItems() != null) {
			MainScreen_ListCatalog.getItems().clear();
		}

		// keine Kategorie, also alle Kategorien
		if (newValue > -1) {
			String selectedCategoryString = (MainScreen_ChoiceBox_Category.getItems().get(newValue)); // Name der
																										// selektierten
																										// Kategorie

			if (MainScreen_ListCatalog.getItems() != null) {
				MainScreen_ListCatalog.getItems().clear(); // Katalog Liste leeren
			}

			// keine Kategorie
			if (newValue == 0) {
				// Alle Kategorien ausgewählt und kein Suchbegriff ist eingegeben
				if (avoidCategoryChangedEvent) {
					avoidCategoryChangedEvent = false;
					return;
				}

				if (currentSearchEvent) {
					currentSearchEvent = false;
					avoidCategoryChangedEvent = true;
					isRefresh=true;
					LoadAllProducts();
					return;
				}
				// Alle Kategorien ausgewählt und Suchbegriff ist eingegeben
				else {
					searchChangedEvent();
				}

			} else {
				// Sonstige Kategorie ausgewählt
				HashMap<String, Object> requestMap = new HashMap<String, Object>();
				requestMap.put("Category", selectedCategoryString);

				ClientRequest req = new ClientRequest(Request.FetchProducts, requestMap);
				Client client = Client.getClient();
				ServerResponse queryResponse = client.sendClientRequest(req);
				if (queryResponse.getResponseType() != null) {
					Product[] articleInCategory = (Product[]) queryResponse.getResponseMap().get("Products"); // Produkte
																												// in
																												// Kategorie
					Product[] articlesInCategoryAndSearch = null;

					if (lastSearchResult == null) {
						// Letzte Suche war leer bzw. noch keine Suche getätigt
						articlesInCategoryAndSearch = articleInCategory;
					} else {
						// aktuelle Suche
						int i = 0;
						for (Product p : lastSearchResult) {
							if (p.getCategory().equals(selectedCategoryString)) {
								i++;
							}
						}
						if (i > 0) {
							articlesInCategoryAndSearch = new Product[i];
							int ii = 0;
							for (Product p : lastSearchResult) {
								if (p.getCategory().equals(selectedCategoryString)) {
									articlesInCategoryAndSearch[ii] = p;
									ii++;
								}
							}
						}
					}

					if (articlesInCategoryAndSearch != null) {
						fetchedObservableProducts = FXCollections
								.observableArrayList(articlesInCategoryAndSearch).sorted((o1, o2) -> {
									if (o1.getId() == o2.getId())
										return 0;
									if (o1.getId() > o2.getId())
										return 1;
									else
										return 0;
								});
						fetchedObservableProducts.removeIf(n -> (n == null));

						addDistanceAndShowInCatalog();
					}
					else
					{
						MainScreen_Load_Catalog.setVisible(false);
					}
				}
			}
			currentSearchEvent = false;
		}
	}

	private void searchChangedEvent() {
		// Katalog leeren
		fetchedObservableProducts = null;
		productCounter = 0;
		addedIds = new ArrayList<Integer>();
		MainScreen_Load_Catalog.setVisible(true);

		if (MainScreen_ListCatalog.getItems() != null) {
			MainScreen_ListCatalog.getItems().clear();
		}

		Product[] articlesInSearch = null;
		// Kein Suchbegriff eingegeben
		if (MainScreen_txtSearch.getText() == null || MainScreen_txtSearch.getText().isEmpty()
				|| MainScreen_txtSearch.getText().isBlank() || MainScreen_txtSearch.getText() == "") {
			currentSearchEvent = true;
			lastSearchResult = null;
			categoryChangedEvent(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedIndex());
		}

		// Suchbegriff eingegeben
		else {
			HashMap<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("SearchString", MainScreen_txtSearch.getText());

			ClientRequest req = new ClientRequest(Request.FetchProducts, requestMap);
			Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);

			// wenn es keine Probleme gibt, kann Suche starten
			if (queryResponse.getResponseType() != null) {
				// Verkapselte Suche (Kategorie und Suchbegriff)
				articlesInSearch = (Product[]) queryResponse.getResponseMap().get("Products");

				Product[] articlesInSearchAndCategory = null;

				if (articlesInSearch != null) {
					if (MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedIndex() == 0) {
						// Alle Kategorien ausgewählt
						articlesInSearchAndCategory = articlesInSearch;
					} else {

						// bestimmte Kategorie ausgewählt
						int i = 0;
						for (Product p : articlesInSearch) {
							if (p.getCategory()
									.equals(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedItem())) {
								i++;
							}
						}
						if (i > 0) {
							articlesInSearchAndCategory = new Product[i];
							int ii = 0;
							for (Product p : articlesInSearch) {
								if (p.getCategory()
										.equals(MainScreen_ChoiceBox_Category.getSelectionModel().getSelectedItem())) {
									articlesInSearchAndCategory[ii] = p;
									ii++;
								}
							}
						}
					}
				}

				// Artikel in Katalog anzeigen
				if (articlesInSearchAndCategory != null) {
					fetchedObservableProducts = FXCollections
							.observableArrayList(articlesInSearchAndCategory).sorted((o1, o2) -> {
								if (o1.getId() == o2.getId())
									return 0;
								if (o1.getId() > o2.getId())
									return 1;
								else
									return 0;
							});
					fetchedObservableProducts.removeIf(n -> (n == null));

					addDistanceAndShowInCatalog();
				}
				else
				{
					MainScreen_Load_Catalog.setVisible(false);
				}
			}
		}
		lastSearchResult = articlesInSearch;
	}

	private void auctionsSearchChangedEvent(AuctionType auctionType) {
		// Auktionen leeren
		if (MainScreen_ListAuctions.getItems() != null) {
			MainScreen_ListAuctions.getItems().clear();
		}

		if (MainScreen_txtSearchAuctions.getText() == null || MainScreen_txtSearchAuctions.getText().isEmpty()
				|| MainScreen_txtSearchAuctions.getText().isBlank() || MainScreen_txtSearchAuctions.getText() == "") {
			// Wenn kein Text eingegeben, alle laden
			if (radioCurrentAuctions.isSelected()) {
				MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.Active));
			} else if (radioEndedAuctions.isSelected()) {
				MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.Ended));
			} else if (radioFutureAuctions.isSelected()) {
				MainScreen_ListAuctions.setItems(LoadAuctions(AuctionType.Future));
			}
			return;
		}

		// Ansonsten Suche durchfuehren
		Auction[] auctionsInSearch = null;

		HashMap<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("AuctionType", auctionType);
		requestMap.put("SearchString", MainScreen_txtSearchAuctions.getText());

		ClientRequest req = new ClientRequest(Request.FetchAuctions, requestMap);
		Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);

		if (queryResponse.getResponseType() != null) {
			auctionsInSearch = (Auction[]) queryResponse.getResponseMap().get("Auctions");
			if (auctionsInSearch != null) {
				ObservableList<Auction> ObservableAuctions = FXCollections.observableArrayList(auctionsInSearch);

				MainScreen_ListAuctions.setItems(ObservableAuctions);
			}
		}
	}

	@FXML
	private ChoiceBox<String> MainScreen_ChoiceBox_Category;

	@FXML
	private Label MainScreen_LabelWallet;

	@FXML
	private Button MainScreen_ButtonEditAccount;

	@FXML
	private Button MainScreen_ButtonAddWallet;

	@FXML
	private Button MainScreen_ButtonLogout;

	@FXML
	private Button MainScreen_ButtonSellProduct;

	@FXML
	private Button MainScreen_ButtonBuyProduct;

	@FXML
	private Button MainScreen_ButtonMyMessages;

	@FXML
	private Button MainScreen_btnSearchOK;

	@FXML
	private Button MainScreen_ButtonPurchases;

	@FXML
	private Button MainScreen_ButtonSales;

	@FXML
	private Button MainScreen_ButtonMyRatings;

	@FXML
	private Button MainScreen_ButtonCreateAuction;

	@FXML
	private Button MainScreen_ButtonShowRatings;

	@FXML
	private Label MainScreen_LabelLoggedInAs;

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab tabArticles;

	@FXML
	private AnchorPane AnchorPaneArticleDetails;

	@FXML
	private AnchorPane AnchorPaneAuctionDetails;

	@FXML
	private TextField MainScreen_txtSearch;

	@FXML
	private WebView MainScreen_WebViewProductDescription;

	@FXML
	private TableView<Product> MainScreen_ListLastViewed;

	@FXML
	private TableView<Product> MainScreen_ListCatalog;

	@FXML
	private TableColumn<Product, Integer> lastviewedIdColumn;

	@FXML
	private TableColumn<Product, String> lastviewedProductColumn;

	@FXML
	private TableColumn<Product, Double> lastviewedPriceColumn;

	@FXML
	private TableColumn<Product, String> lastviewedSellerColumn;

	@FXML
	private TableColumn<Product, String> lastviewedCategoryColumn;

	@FXML
	private TableColumn<Product, Double> lastviewedDistanceColumn;

	@FXML
	private TableColumn<Product, Integer> catalogIdColumn;

	@FXML
	private TableColumn<Product, String> catalogProductColumn;

	@FXML
	private TableColumn<Product, Double> catalogPriceColumn;

	@FXML
	private TableColumn<Product, String> catalogSellerColumn;

	@FXML
	private TableColumn<Product, String> catalogCategoryColumn;

	@FXML
	private TableColumn<Product, Double> catalogDistanceColumn;

	@FXML
	private Tab tabLiveAuctions;

	@FXML
	private TextField MainScreen_txtSearchAuctions;

	@FXML
	private Button MainScreen_btnAuctionsSearchOK;

	@FXML
	private Button MainScreen_ButtonSendMessage;

	@FXML
	private Button MainScreen_ButtonSendMessageAuction;

	@FXML
	private Button MainScreen_ButtonUpdatePrice;

	@FXML
	private Button MainScreen_ShowAuctionsOnMap;

	@FXML
	private TableView<Auction> MainScreen_ListAuctions;

	@FXML
	private TableColumn<Auction, Integer> auctionsCatalogIdColumn;

	@FXML
	private TableColumn<Auction, String> auctionsCatalogTitleColumn;

	@FXML
	private TableColumn<Auction, Double> auctionsCatalogCurrentBidColumn;

	@FXML
	private TableColumn<Auction, Double> auctionsCatalogMinBidColumn;

	@FXML
	private TableColumn<Auction, LocalDateTime> auctionsCatalogStartColumn;

	@FXML
	private TableColumn<Auction, LocalDateTime> auctionsCatalogEndColumn;

	@FXML
	private TableView<Auction> MainScreen_ListAlsoBought;

	@FXML
	private TableColumn<Auction, Integer> lastboughtIdColumn;

	@FXML
	private TableColumn<Auction, String> lastBoughtProductColumn;

	@FXML
	private TableColumn<Auction, Double> lastBoughtPriceColumn;

	@FXML
	private RadioButton radioAllAuctions;

	@FXML
	private RadioButton radioMyBids;

	@FXML
	private RadioButton radioMyAuctions;

	@FXML
	private RadioButton radioSavedAuctions;

	@FXML
	private RadioButton radioCurrentAuctions;

	@FXML
	private RadioButton radioEndedAuctions;

	@FXML
	private RadioButton radioFutureAuctions;

	@FXML
	private Label MainScreen_LabelProductTitle;

	@FXML
	private Label MainScreen_LabelProductSeller;

	@FXML
	private Label MainScreen_LabelProductPrice;

	@FXML
	private Label MainScreen_LabelAlsoBought;

	@FXML
	private Label MainScreen_LabelReducedPrice;

	@FXML
	private Label MainScreen_LabelProductCategory;

	@FXML
	private Label MainScreen_lblAverageRating;

	@FXML
	private Label MainScreen_txtRatingCount;

	@FXML
	private Label MainScreen_txtAverageRating;

	@FXML
	private Label MainScreen_txtDollarBidAmount;

	@FXML
	private Label MainScreen_LabelProductTitleAuction;

	@FXML
	private Label MainScreen_LabelStartPriceAuction;

	@FXML
	private Label MainScreen_LabelAuctionSeller;

	@FXML
	private Button MainScreen_ButtonBidAuction;

	@FXML
	private WebView MainScreen_WebViewAuctionDescription;

	@FXML
	private Label MainScreen_txtRatingCountAuction;

	@FXML
	private Label MainScreen_txtAverageRatingAuction;

	@FXML
	private Button MainScreen_ButtonShowRatingsAuction;

	@FXML
	private Label MainScreen_LabelMinBidAuction;

	@FXML
	private Label MainScreen_LabelCurrentBidAuction;

	@FXML
	private Label MainScreen_LabelShippingAuction;

	@FXML
	private Label MainScreen_LabelTimeAuction;

	@FXML
	private ImageView MainScreen_ImgAuction;

	@FXML
	private ImageView MainScreen_Load_Catalog;

	@FXML
	private ImageView MainScreen_Load_LastViewed;

	@FXML
	private TextField MainScreen_TextboxBidAmount;

	@FXML
	private Button MainScreen_ButtonSaveAuction;

	@FXML
	private Button MainScreen_ButtonRefresh;

	@FXML
	private ImageView MainScreen_ImgProfilePicture;

	@FXML
	void MainScreen_CloseButtonMenuClick(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void MainScreen_RefreshButtonMenuClick(ActionEvent event) {
		MainScreen_ButtonRefresh.fire();
	}

	@FXML
	void MainScreen_InfoButtonMenuClick(ActionEvent event) {

		FXMLHandler.ShowMessageBox(
				"© 'Super-E-commerce-Platform' wurde entwickelt von Denis Artjuch, Yannis Bromby, Marcel Just und Hannah Kalker. Gruppe B, Modul Software Entwicklung & Programmierung, Universität Duisburg-Essen, 2020/21.",
				"Super-E-commerce-Platform", "Super-E-commerce-Platform", AlertType.INFORMATION, true, false);
	}

	@FXML
	void MainScreen_CreditsButtonMenuClick(ActionEvent event) {
		FXMLHandler.ShowMessageBox(
				"net.coobird.thumbnailator - 0.4.13" + System.lineSeparator()
						+ "com.dlsc.GMapsFX - 11.0.1, https://github.com/dlsc-software-consulting-gmbh/GMapsFX"
						+ System.lineSeparator() + "com.google.maps:google-maps-services - 0.15.0, https://github.com/googlemaps/google-maps-services-java"
						+ System.lineSeparator() + "org.slf4j:slf4j-simple - 1.7.25"
						+ System.lineSeparator() + "JavaFX - 15.0.1" + System.lineSeparator() + "JUnit - 4"
						+ System.lineSeparator() + "mysql.mysql-connector-java - 8.0.22" + System.lineSeparator()
						+ "com.sun.mail.javax.mail - 1.6.2" + System.lineSeparator()
						+ "Maps Icons Collection, https://mapicons.mapsmarker.com" + System.lineSeparator()
						+ "FreeLogoDesign, https://de.freelogodesign.org",
				"Super-E-commerce-Platform", "Super-E-commerce-Platform", AlertType.INFORMATION, true, false);
	}

	@FXML
	void MainScreen_GMapsFXMenuClick(ActionEvent event) throws IOException, URISyntaxException {
		java.awt.Desktop.getDesktop().browse(new URI("https://github.com/dlsc-software-consulting-gmbh/GMapsFX"));
	}

	@FXML
	void MainScreen_MapsIconCollectionMenuClick(ActionEvent event) throws IOException, URISyntaxException {
		java.awt.Desktop.getDesktop().browse(new URI("https://mapicons.mapsmarker.com"));
	}

	@FXML
	void MainScreen_FreeLogoDesignMenuClick(ActionEvent event) throws IOException, URISyntaxException {
		java.awt.Desktop.getDesktop().browse(new URI("https://de.freelogodesign.org"));
	}
	
	@FXML
	void MainScreen_GoogleMapsMenuClick(ActionEvent event) throws IOException, URISyntaxException {
		java.awt.Desktop.getDesktop().browse(new URI("https://github.com/googlemaps/google-maps-services-java"));
	}

	@FXML
	void MainScreen_btnAddWalletClick(ActionEvent event) {
		WalletController.setUser(user);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonAddWallet.getScene().getWindow(), "Wallet",
				"Guthaben aufladen", false, true);
	}

	@FXML
	void MainScreen_btnEditAccountClick(ActionEvent event) {
		EditAccountController.setUser(user);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonEditAccount.getScene().getWindow(), "EditAccount",
				"Konto bearbeiten", false, true);
	}

	@FXML
	void MainScreen_btnLogoutClick(ActionEvent event) {
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonLogout.getScene().getWindow(), "Start",
				"Super-E-commerce-Platform", false, true);
	}

	@FXML
	void MainScreen_ButtonCreateAuctionClick(ActionEvent event) {
		CreateAuctionController.setCustomer((Customer) user); // nur für Customer enabled
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonCreateAuction.getScene().getWindow(), "CreateAuction",
				"Auktion erstellen", false, true);
	}

	@FXML
	void MainScreen_ButtonMyRatingsClick(ActionEvent event) {
		ShowRatingsController.setUser(user);
		ShowRatingsController.setViewOwnRatings(true);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonMyRatings.getScene().getWindow(), "ShowRatings",
				"Meine Bewertungen", false, true);
	}

	@FXML
	void MainScreen_btnMyMessagesClick(ActionEvent event) {
		ShowReceivedMessagesController.setUser(user);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonMyMessages.getScene().getWindow(), "ShowReceivedMessages",
				"Meine erhaltenen Nachrichten", false, true);
	}

	@FXML
	void MainScreen_btnMyPurchasesClick(ActionEvent event) {
		MyPurchasesController.setCustomer((Customer) user); // Button ist nur für Customer enabled
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonPurchases.getScene().getWindow(), "MyPurchases",
				"Meine Käufe", false, true);
	}

	@FXML
	void MainScreen_ButtonSalesClick(ActionEvent event) {
		MySalesController.setUser(user); // Button ist nur für Customer enabled
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSales.getScene().getWindow(), "MySales", "Meine Verkäufe",
				false, true);
	}

	@FXML
	void MainScreen_btnSellProductClick(ActionEvent event) {
		// OfferProduct oeffnen
		OfferProductController.setUser(user);

		// ggf. Kategorien mit übergeben
		if (MainScreen_ChoiceBox_Category.getItems() != null) {
			OfferProductController.setCategoryList(MainScreen_ChoiceBox_Category.getItems());
		}
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSellProduct.getScene().getWindow(), "OfferProduct",
				"Produkt(e) anbieten", false, true);
	}

	@FXML
	void MainScreen_ButtonShowRatingsClick(ActionEvent event) {
		// Alle Bewertungen eines Verkäufers anzeigen
		if (MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null) {
			ShowRatingsController.setUser(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller());
		} else if (MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null) {
			ShowRatingsController.setUser(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller());
		}
		ShowRatingsController.setViewOwnRatings(false);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonShowRatings.getScene().getWindow(), "ShowRatings",
				"Bewertungen des Verkäufers", false, true);
	}

	@FXML
	void MainScreen_ButtonShowRatingsAuctionClick(ActionEvent event) {
		// Alle Bewertungen eines Verkäufers anzeigen
		if (MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null) {
			ShowRatingsController.setUser(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller());
		}
		ShowRatingsController.setViewOwnRatings(false);
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonShowRatingsAuction.getScene().getWindow(), "ShowRatings",
				"Bewertungen des Verkäufers", false, true);
	}

	@FXML
	void MainScreen_SaveAuctionClick(ActionEvent event) {
		saveAuction();
	}

	@FXML
	void MainScreen_ButtonRefresh_Click(ActionEvent event) {
		// Ansicht aktualisieren - aufrufbar mit MainScreen_ButtonRefresh.fire();
		isRefresh=true;
		refreshUserDetails();
		if (tabPane.getSelectionModel().getSelectedItem() == tabArticles) {
			refreshViewArticles();
		} else if (tabPane.getSelectionModel().getSelectedItem() == tabLiveAuctions) {
			AuctionType auctionType = AuctionType.Active;
			if (radioAllAuctions.isSelected() && radioCurrentAuctions.isSelected())
				auctionType = AuctionType.Active;
			else if (radioAllAuctions.isSelected() && radioEndedAuctions.isSelected())
				auctionType = AuctionType.Ended;
			else if (radioAllAuctions.isSelected() && radioFutureAuctions.isSelected())
				auctionType = AuctionType.Future;
			else if (radioMyAuctions.isSelected())
				auctionType = AuctionType.MyAuctions;
			else if (radioMyBids.isSelected())
				auctionType = AuctionType.MyBids;
			else if (radioSavedAuctions.isSelected())
				auctionType = AuctionType.SavedAuctions;

			refreshViewAuctions(auctionType);
		}
	}

	@FXML
	void MainScreen_BuyProductClick(ActionEvent event) {
		Product productToBuy = null;

		// Zu kaufendes Produkt festlegen
		if (MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null) {
			productToBuy = MainScreen_ListCatalog.getSelectionModel().getSelectedItem();
		} else if (MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null) {
			productToBuy = MainScreen_ListLastViewed.getSelectionModel().getSelectedItem();
		} else {
			FXMLHandler.ShowMessageBox("Es ist kein Produkt ausgewählt.", "Fehler", "Fehler", AlertType.WARNING, true,
					false);
			return;
		}

		// clienseitig Prüfen, ob genug Guthaben vorhanden ist
		if (user.getWallet() < productToBuy.getPrice()) {
			FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das ausgewählte Produkt zu kaufen.", "Fehler",
					"Fehler", AlertType.ERROR, true, false);
			return;
		}

		// Client BuyItem Request senden

		// Es wird bei dieser Request automatisch das Käuferkonto um den Produktpreis
		// verringert
		// und das Verkäuferkonto um den Produktpreis erhöht
		// In der Request wird geprüft, ob genug Guthaben vorhanden ist.

		HashMap<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("User", user);
		requestMap.put("Product", productToBuy);
		ClientRequest req = new ClientRequest(Request.BuyItem, requestMap);
		Client client = Client.getClient();
		ServerResponse queryResponse = client.sendClientRequest(req);

		// Antwort auslesen
		if (queryResponse.getResponseType() == Response.NoDBConnection) {
			FXMLHandler.ShowMessageBox(
					"Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher kein Kauf durchgeführt.",
					"Fehler", "Fehler", AlertType.ERROR, true, false);
			return;
		} else if (queryResponse.getResponseType() == Response.InsufficientBalance) {
			FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das ausgewählte Produkt zu kaufen.", "Fehler",
					"Fehler", AlertType.ERROR, true, false);
			return;
		} else if (queryResponse.getResponseType() == Response.Failure) {
			FXMLHandler.ShowMessageBox("Beim Kaufen des Artikels ist ein unbekannter Fehler aufgetreten.", "Fehler",
					"Fehler", AlertType.ERROR, true, false);
			return;
		} else if (queryResponse.getResponseType() == Response.Success) {
			FXMLHandler.ShowMessageBox(
					"Sie haben den Artikel '" + productToBuy.getName() + "' erfolgreich für "
							+ productToBuy.getPriceString() + " gekauft.",
					"Kauf erfolgreich", "Kauf erfolgreich", AlertType.CONFIRMATION, true, false);
			// MainScreen oeffnen
			user.setWallet(user.getWallet() - productToBuy.getPrice());
			MainScreenController.setUser(user);
			refreshUserDetails();
			refreshViewArticles();
		}
	}

	@FXML
	void MainScreen_BidAuctionClick(ActionEvent event) {
		// Zugriff nur fuer Privatkunden und ob der aktuelle User der Seller ist wird
		// vorher beim Selektieren eprueft.
		if (MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null) {
			Auction selectedAuction = MainScreen_ListAuctions.getSelectionModel().getSelectedItem();
			String bidAmountString = MainScreen_TextboxBidAmount.getText().trim();
			double bidAmount;

			// Prüfen ob Gebotsbetrag eingegeben ist
			if (bidAmountString == "" || bidAmountString == null) {
				FXMLHandler.ShowMessageBox("Bitte geben Sie einen Gebotsbetrag ein.", "Fehler", "Fehler",
						AlertType.WARNING, true, false);
				return;
			}

			// Prüfen ob Preis double ist und in Variable speichern (vorher ggf. , durch .
			// ersetzen)
			try {
				bidAmount = Double.parseDouble(bidAmountString.replace(",", "."));
			} catch (NumberFormatException e) {
				FXMLHandler.ShowMessageBox(
						"Bitte geben Sie Ihr Gebot im folgenden Format ein: ##,##" + System.lineSeparator()
								+ "(Ohne Währungszeichen und mit . oder ,)",
						"Fehler", "Fehler", AlertType.ERROR, true, false);
				MainScreen_TextboxBidAmount.setText("");
				return;
			}

			// wird serverseitig geprueft
			// clienseitig prüfen, ob genug Guthaben vorhanden ist
			// if(user.getWallet()<bidAmount)
			// {
			// FXMLHandler.ShowMessageBox("Ihr Guthaben reicht nicht aus, um das Gebot
			// abzugeben. Bitte laden Sie Ihr Guthaben auf.", "Fehler", "Fehler",
			// AlertType.ERROR, true, false);
			// MainScreen_TextboxBidAmount.setText("");
			// return;
			// }

			// SendBid Request senden

			HashMap<String, Object> requestMap = new HashMap<String, Object>();
			requestMap.put("Auction", selectedAuction);
			requestMap.put("Bidder", user);
			requestMap.put("Bid", bidAmount);
			ClientRequest req = new ClientRequest(Request.SendBid, requestMap);
			Client client = Client.getClient();
			ServerResponse queryResponse = client.sendClientRequest(req);

			// Antwort auslesen
			if (queryResponse.getResponseType() == Response.NoDBConnection) {
				FXMLHandler.ShowMessageBox(
						"Es konnte keine Verbindung zur Datenbank hergestellt werden, es wurde daher kein Gebot abgegeben.",
						"Fehler", "Fehler", AlertType.ERROR, true, false);
				MainScreen_TextboxBidAmount.setText("");
				return;
			} else if (queryResponse.getResponseType() == Response.InsufficientBalance) {
				FXMLHandler.ShowMessageBox(
						"Ihr Guthaben reicht nicht aus, um das Gebot abzugeben. Bitte laden Sie Ihr Guthaben auf.",
						"Fehler", "Fehler", AlertType.ERROR, true, false);
				MainScreen_TextboxBidAmount.setText("");
				return;
			} else if (queryResponse.getResponseType() == Response.BidTooLow) {
				FXMLHandler.ShowMessageBox("Ihr eingegebenes Gebot ist zu niedrig.", "Fehler", "Fehler",
						AlertType.ERROR, true, false);
				MainScreen_TextboxBidAmount.setText("");
				return;
			} else if (queryResponse.getResponseType() == Response.AuctionNotStartedYet) {
				FXMLHandler.ShowMessageBox(
						"Die Auktion, auf die Sie bieten wollen, ist noch nicht gestartet. Bitte bieten Sie zu einem späteren Zeitpunkt.",
						"Fehler", "Fehler", AlertType.ERROR, true, false);
				MainScreen_TextboxBidAmount.setText("");
				return;
			} else if (queryResponse.getResponseType() == Response.AuctionAlreadyEnded) {
				FXMLHandler.ShowMessageBox("Die Auktion, auf die Sie bieten wollen, wurde bereits beendet.", "Fehler",
						"Fehler", AlertType.ERROR, true, false);
				MainScreen_TextboxBidAmount.setText("");
				return;
			} else if (queryResponse.getResponseType() == Response.Failure) {
				FXMLHandler.ShowMessageBox("Beim Abgeben des Gebots ist ein unbekannter Fehler aufgetreten.", "Fehler",
						"Fehler", AlertType.ERROR, true, false);
				MainScreen_TextboxBidAmount.setText("");
				return;
			} else if (queryResponse.getResponseType() == Response.Success) {
				FXMLHandler.ShowMessageBox(
						"Ihr Gebot wurde erfolgreich abgegeben. Sie sind aktuell der Höchstbieter. Zum Zeitpunkt des Auktionsendes muss Ihr Konto genügend Guthaben aufweisen, wenn Sie Höchstbieter sind, ansonsten wird die Auktion abgebrochen.",
						"Gebot erfolgreich", "Gebot erfolgreich", AlertType.CONFIRMATION, true, false);

				// Guthaben wird clientseitig nicht reduziert, da es erst nach einer Auktion
				// abgezogen wird.
				// user.setWallet(user.getWallet() - bidAmount);
				// MainScreenController.setUser(user);
				MainScreen_TextboxBidAmount.setText("");
				refreshViewAuctions(AuctionType.Active);
				updateAuctionInfo();
			}
		}
	}

	void tabArticles_Select() {
		alreadyLoadedLastViewed = false;
		refreshViewArticles();
	}

	void tabLiveAuctions_Select() {
		MainScreen_Load_Catalog.setVisible(false);
		MainScreen_Load_LastViewed.setVisible(false);
		refreshViewAuctions(AuctionType.Active);
	}

	@FXML
	void MainScreen_btnSearchOKClick(ActionEvent event) {
		searchChangedEvent();
	}

	@FXML
	void MainScreen_txtSearch_KeyPressed(KeyEvent event) {
		// Taste wird gedrückt
		// Bei Enter: Button Search Klick simulieren
		if (event.getCode().equals(KeyCode.ENTER)) {
			MainScreen_btnSearchOK.fire();
		}
	}
	
	@FXML
	void txtBid_keyPressed(KeyEvent event) {
		// Taste wird gedrückt
		// Bei Enter: Bid Klick simulieren
		if (event.getCode().equals(KeyCode.ENTER)) {
			MainScreen_ButtonBidAuction.fire();
		}
	}

	@FXML
	void MainScreen_btnAuctionsSearchOK_Click(ActionEvent event) {
		// Suche kann bei aktiven, beendeten, zukünftigen Auktionen aufgerufen werden.
		if (radioCurrentAuctions.isSelected()) {
			auctionsSearchChangedEvent(AuctionType.Active);
		} else if (radioEndedAuctions.isSelected()) {
			auctionsSearchChangedEvent(AuctionType.Ended);
		} else if (radioFutureAuctions.isSelected()) {
			auctionsSearchChangedEvent(AuctionType.Future);
		}
	}

	@FXML
	void MainScreen_txtSearchAuctions_KeyPressed(KeyEvent event) {
		// Taste wird gedrückt
		// Bei Enter: Button Auction Search Klick simulieren
		if (event.getCode().equals(KeyCode.ENTER)) {
			MainScreen_btnAuctionsSearchOK.fire();
		}
	}

	@FXML
	void radioAllAuctions_Click(ActionEvent event) {
		refreshViewAuctions(AuctionType.Active);
	}

	@FXML
	void radioCurrentAuctions_Click(ActionEvent event) {
		refreshViewAuctions(AuctionType.Active);
		;
	}

	@FXML
	void radioEndedAuctions_Click(ActionEvent event) {
		refreshViewAuctions(AuctionType.Ended);
	}

	@FXML
	void radioFutureAuctions_Click(ActionEvent event) {
		refreshViewAuctions(AuctionType.Future);
	}

	@FXML
	void radioMyAuctions_Click(ActionEvent event) {
		refreshViewAuctions(AuctionType.MyAuctions);
	}

	@FXML
	void radioMyBids_Click(ActionEvent event) {
		refreshViewAuctions(AuctionType.MyBids);
	}

	@FXML
	void radioSavedAuctions_Click(ActionEvent event) {
		refreshViewAuctions(AuctionType.SavedAuctions);
	}

	@FXML
	void MainScreen_ButtonSendMessage_Click(ActionEvent event) {
		SendMessageController.setSender(user);
		if (MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null) {
			SendMessageController.setReceiver(MainScreen_ListCatalog.getSelectionModel().getSelectedItem().getSeller());
		} else if (MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null) {
			SendMessageController
					.setReceiver(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem().getSeller());
		}
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSendMessage.getScene().getWindow(), "SendMessage",
				"Nachricht senden", false, true);
	}

	@FXML
	void MainScreen_ButtonSendMessageAuction_Click(ActionEvent event) {
		SendMessageController.setSender(user);
		if (MainScreen_ListAuctions.getSelectionModel().getSelectedItem() != null) {
			SendMessageController
					.setReceiver(MainScreen_ListAuctions.getSelectionModel().getSelectedItem().getSeller());
		}
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonSendMessage.getScene().getWindow(), "SendMessage",
				"Nachricht senden", false, true);
	}

	@FXML
	void MainScreen_ButtonUpdatePrice_Click(ActionEvent event) {
		UpdatePriceController.setSeller((Seller) user);
		if (MainScreen_ListCatalog.getSelectionModel().getSelectedItem() != null) {
			UpdatePriceController.setProduct(MainScreen_ListCatalog.getSelectionModel().getSelectedItem());
		} else if (MainScreen_ListLastViewed.getSelectionModel().getSelectedItem() != null) {
			UpdatePriceController.setProduct(MainScreen_ListLastViewed.getSelectionModel().getSelectedItem());
		}
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ButtonUpdatePrice.getScene().getWindow(), "UpdatePrice",
				"Preis anpassen", false, true);
	}

	@FXML
	void MainScreen_ShowAuctionsOnMap_Click(ActionEvent event) {
		AuctionsMapController.setUser(user);
		AuctionsMapController.setAllAuctions(MainScreen_ListAuctions.getItems());
		FXMLHandler.OpenSceneInStage((Stage) MainScreen_ShowAuctionsOnMap.getScene().getWindow(), "AuctionsMap",
				"Auktionen auf der Karte", true, true);
	}
}
