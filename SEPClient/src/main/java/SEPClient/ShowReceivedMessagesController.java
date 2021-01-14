package SEPClient;

import java.io.IOException;
import java.time.LocalDateTime;

import SEPCommon.Message;
import SEPCommon.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ShowReceivedMessagesController {

	private static User user = null;
	
	public static void setUser(User _user)
	{
		user=_user;
	}
	
	public void initialize() throws IOException {
		
	}
	
	private ObservableList<Message> loadMessages()
	{
		return null;
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
