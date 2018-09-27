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
	  
		@Override
		public void initialize(URL location, ResourceBundle resources) {	
			textAbout.appendText("		  ~~~~adbFX~~~~	");
			textAbout.appendText(System.getProperty("line.separator")+" By will0376 =)");
			textAbout.appendText(System.getProperty("line.separator")+"Ver: " + Main.ver);
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
		
}
