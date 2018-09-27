package ru.will0376.adbfx;
	

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
	public static Object ps;
	public static double x;
	public static double y;
	public static String ver = "0.0.8";

	@Override
	public void start(Stage primaryStage) {
		try {
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            primaryStage.setTitle("adbFX");
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("file:logo.png")); //not work =(
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
		 ps = primaryStage;
		 x = primaryStage.getX();
		 y = primaryStage.getY();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
