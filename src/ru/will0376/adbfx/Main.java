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
	public static String ver = "1.9";
	public static Locale locale = new Locale(Locale.getDefault().getLanguage());
	public static boolean debug = false; //includes the local copy of the FAQ
	public static Scene scene;
	public static Controller c;
	public static Process pr;
	
	@Override
	public void start(Stage primaryStage) {
		String name = "adbFX";
		if(debug) {
			ver += ".debug";
			name += " Debug";
		}
		try {
			FXMLLoader fxmlload = new FXMLLoader();
			fxmlload.setLocation(getClass().getResource("Fxml/Main.fxml"));
			fxmlload.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", locale));
            Parent root = fxmlload.load();
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
            primaryStage.setTitle(name);
            primaryStage.setScene(new Scene(root, 710, 320));
           	 scene = primaryStage.getScene();
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
		 ps = primaryStage;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public static Scene getScene() {
		return scene;
	}
	
}
