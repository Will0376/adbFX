package ru.will0376.adbfx;
	

import java.util.Locale;
import java.util.ResourceBundle;

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
	public static String ver = "0.1.1";
	public static Locale locale;
	@Override
	public void start(Stage primaryStage) {
		try {
			locale = new Locale(Locale.getDefault().getLanguage());
			FXMLLoader fxmlload = new FXMLLoader();
			fxmlload.setLocation(getClass().getResource("Fxml/Main.fxml"));
			fxmlload.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", locale));
            Parent root = fxmlload.load();
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
            primaryStage.setTitle("adbFX");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
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
