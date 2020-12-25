package controller;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class WinTheGameUIController {
    @FXML
    private TextField textTotalSteps;

    @FXML
    private Pane paneMessage;

    @FXML
    private TextField textTotalTime;
    

    @FXML
    void initialize() {
    	textTotalSteps.setText(Integer.toString(MazeUIController.steps));
    	//System.out.println(MazeUIController.winningTime);
    	textTotalTime.setText(MazeUIController.winningTime);
    	paneMessage.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				Scene scene = null;
				try {
					scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("view/MazeUI.fxml")));
				} catch (IOException e) {
					e.printStackTrace();
				}
		        scene.getStylesheets().add(getClass().getClassLoader().getResource("application/application.css").toExternalForm());
		        Stage stage = ((Stage)paneMessage.getScene().getWindow());
		        stage.setTitle("Maze");
		        stage.setScene(scene);
		        stage.show();
			}
		});
    }
    
}
