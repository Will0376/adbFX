package ru.will0376.adbfx;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ControllerAbout implements Initializable {
	 
	  
	   @FXML
	   private TextArea textAbout;
	   @FXML
	   private Button button;
	   
	   private ResourceBundle resources;
		@Override
		public void initialize(URL location, ResourceBundle resources) {	
			this.resources = resources;
			printText("		  ~~~~AdbFX~~~~	");
			printText(this.resources.getString("key.About"));
			printText("Version: " + Main.ver);
			printText("By Will0376 =)");
			textAbout.setWrapText(true);
		}
		public void openGithub(ActionEvent event) {
					try {
						URI uri = new URI("https://github.com/Will0376/adbFX");
						 Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
						    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
						    	  desktop.browse(uri);
						    	}
						}
						  catch (URISyntaxException | IOException e1) {
							  	e1.printStackTrace();
							}
		}
		public void printText(String text) {
			textAbout.appendText(text + System.getProperty("line.separator"));
		 }
		
}
