package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/MazeUI.fxml"));
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/image/MazeImage.jpg")));
		primaryStage.setTitle("MAZE");
		
	    Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("application/application.css").toExternalForm());
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
