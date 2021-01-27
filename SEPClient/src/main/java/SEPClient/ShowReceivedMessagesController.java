package SEPClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import SEPCommon.ClientRequest;
import SEPCommon.Message;
import SEPCommon.Rating;
import SEPCommon.Request;
import SEPCommon.Response;
import SEPCommon.ServerResponse;
import SEPCommon.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ShowReceivedMessagesController {

	private static User user = null;
	
	public static void setUser(User _user)
	{
		user=_user;
	}
	
	public void initialize() throws IOException {
		TableMessages_ColumnDate.setCellValueFactory(new PropertyValueFactory<Message, LocalDateTime>("date"));
		TableMessages_ColumnDate.setCellFactory(tc -> new TableCell<Message, LocalDateTime>() {
    	    @Override
    	    protected void updateItem(LocalDateTime date, boolean empty) {
    	        super.updateItem(date, empty);
    	        if (empty || date==null) {
    	            setText(null);
    	        } else {
    	            setText(date.format(SEPCommon.Constants.DATEFORMAT));
    	        }
    	    }
    	});
		
		TableMessages_ColumnBy.setCellValueFactory(new PropertyValueFactory<Message, String>("sender"));
		TableMessages_ColumnMessage.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));
		
		TableMessages.setItems(loadMessages());
		
		answerListener();
	}
	
	private ObservableList<Message> loadMessages()
	{
		if (TableMessages.getItems() != null) {
			TableMessages.getItems().clear();
		}
		
		HashMap <String,Object> requestMap = new HashMap<String, Object>();
    	requestMap.put("User", user);
    	
    	ClientRequest req = new ClientRequest (Request.FetchMessages, requestMap);
    	Client client = Client.getClient();
    	ServerResponse queryResponse = client.sendClientRequest(req);
		
    	if (queryResponse != null && queryResponse.getResponseMap() != null && queryResponse.getResponseMap().get("Messages") != null) {
    		
    		Message [] messages = (Message[])queryResponse.getResponseMap().get("Messages");
    		ObservableList<Message> ObservableMessages = FXCollections.observableArrayList(messages);
    		ObservableMessages.removeIf(n -> (n == null));
    		
    		return ObservableMessages;	
    	}
		
		return null;
	}
	
	private void answerListener() {
		
		TableMessages.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				if (TableMessages.getSelectionModel().getSelectedItem().getMessage() != null) {
					ShowMessages_AnswerButton.setDisable(false);
					TextAreaMessage.setEditable(true);
				} else {
					ShowMessages_AnswerButton.setDisable(true);
				}
			} else {
				ShowMessages_AnswerButton.setDisable(true);
			}
		});
		
		
	}
	
	
	@FXML
    private TableView<Message> TableMessages;

    @FXML
    private TableColumn<Message, LocalDateTime> TableMessages_ColumnDate;

    @FXML
    private TableColumn<Message, String> TableMessages_ColumnBy;

    @FXML
    private TableColumn<Message, String> TableMessages_ColumnMessage;

    @FXML
    private Button ShowMessages_AnswerButton;

    @FXML
    private Button ShowMessages_RefreshButton;

    @FXML
    private Button ShowMessages_ReturnButton;
    
    @FXML
    private TextArea TextAreaMessage;

    @FXML
    void ShowMessages_AnswerButton_Click(ActionEvent event) {
    	String message = TextAreaMessage.getText();
    	
    	if (message == null) {
    		FXMLHandler.ShowMessageBox("Sie haben noch keine Nachricht verfasst.", "Fehler", "Fehler", AlertType.ERROR, true, false);			
			return;
    	}
    	
//    	Message newMessage = new Message(user, receiver, message);
//    	
//    	
//    	HashMap <String, Object> requestMap = new HashMap<String, Object>();
//    	requestMap.put("Message", newMessage);
//    	
//    	
//    	
//    	ClientRequest req = new ClientRequest(Request.SendMessage, requestMap);
//    	Client client = Client.getClient();
//    	ServerResponse queryResponse = client.sendClientRequest(req);
//    	
//    	if(queryResponse.getResponseType() == Response.NoDBConnection) {
//			FXMLHandler.ShowMessageBox("Es konnte keine Verbindung zur Datenbank hergestellt werden, Ihre Nachricht wurde daher nicht abgeschickt.",
//					"Fehler", "Fehler", AlertType.ERROR, true, false);
//			return;
//		}
//
//    	else if (queryResponse.getResponseType() == Response.Failure) {
//    		FXMLHandler.ShowMessageBox("Es ist ein Fehler beim Verarbeiten Ihrer Anfrage aufgetreten, Ihre Nachricht wurde daher nicht abgeschickt.",
//					"Fehler", "Fehler", AlertType.ERROR, true, false);
//			return;
//		}
//    	
//    	else if (queryResponse.getResponseType() == Response.Success) {
//			FXMLHandler.ShowMessageBox("Ihre Nachricht wurde erfolgreich übermittelt.",
//					"Nachricht abgeschickt", "Nachricht abgeschickt", AlertType.INFORMATION, true, false);
//
//	        FXMLHandler.OpenSceneInStage((Stage) ShowMessages_ReturnButton.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
//
//    	
//    }
    	
    }

    @FXML
    void ShowMessages_RefreshButton_Click(ActionEvent event) {
    	loadMessages();
    	ShowMessages_AnswerButton.setDisable(true);
    }

    @FXML
    void ShowMessages_ReturnButton_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) ShowMessages_ReturnButton.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
