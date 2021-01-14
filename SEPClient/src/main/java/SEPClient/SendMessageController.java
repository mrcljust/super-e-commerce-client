package SEPClient;

import java.io.IOException;

import SEPCommon.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class SendMessageController {

	private static User sender = null;
	private static User receiver = null;
	
	public static void setSender(User _user)
	{
		sender=_user;
	}
	
	public static void setReceiver(User _user)
	{
		receiver=_user;
	}
	
	public void initialize() throws IOException {
		
	}
	
	@FXML
    private Label SendMessage_txtReceiverName;

    @FXML
    private ImageView SendMessage_ImgProfilePicture;

    @FXML
    private TextArea SendMessage_txtMessage;

    @FXML
    private Button SendMessage_ButtonOK;

    @FXML
    private Button SendMessage_ButtonReturn;

    @FXML
    void SendMessage_ButtonOK_Click(ActionEvent event) {

    }

    @FXML
    void SendMessage_ButtonReturn_Click(ActionEvent event) {
    	FXMLHandler.OpenSceneInStage((Stage) SendMessage_ButtonReturn.getScene().getWindow(), "MainScreen", "Super-E-commerce-Platform", true, true);
    }
}
