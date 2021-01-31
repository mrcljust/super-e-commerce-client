package SEPClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import SEPCommon.ClientRequest;
import SEPCommon.Message;
import SEPCommon.Request;
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
		
		TableMessages_ColumnBy.setCellValueFactory(new PropertyValueFactory<Message, String>("senderName"));
		TableMessages_ColumnMessage.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));
		
		TableMessages.setItems(loadMessages());
		
		showMessageListener();
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
	
	private void showMessageListener() {
		
		TableMessages.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				if (TableMessages.getSelectionModel().getSelectedItem().getMessage() != null) {
					ShowMessages_AnswerButton.setDisable(false);
					TextAreaMessage.setText(TableMessages.getSelectionModel().getSelectedItem().getMessage());
				} else {
					ShowMessages_AnswerButton.setDisable(true);
					TextAreaMessage.setText("");
				}
			} else {
				ShowMessages_AnswerButton.setDisable(true);
				TextAreaMessage.setText("");
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
    	if (TableMessages.getSelectionModel().getSelectedItem() != null) {
    		SendMessageController.setSender(user);
    		SendMessageController.setReceiver(TableMessages.getSelectionModel().getSelectedItem().getSender());
    		FXMLHandler.OpenSceneInStage((Stage) ShowMessages_AnswerButton.getScene().getWindow(), "SendMessage",
    				"Nachricht senden", false, true);
    	}
    }

    @FXML
    void ShowMessages_RefreshButton_Click(ActionEvent event) {
		TableMessages.setItems(loadMessages());
    	ShowMessages_AnswerButton.setDisable(true);
		TextAreaMessage.setText("");
    }

    @FXML
    void ShowMessages_ReturnButton_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) ShowMessages_ReturnButton.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
