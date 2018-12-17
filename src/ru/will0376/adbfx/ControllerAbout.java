package ru.will0376.adbfx;


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
			printText("		  ~~~~AdbFX~~~~	");
			printText(resources.getString("key.About"));
			printText("Version: " + Main.ver);
			printText("By Will0376 =)");
			textAbout.setWrapText(true);
		}
		public void openGithub(ActionEvent event) {
			new Controller().openBrw("https://github.com/Will0376/adbFX");
		}
		private void printText(String text) {
			textAbout.appendText(text + System.getProperty("line.separator"));
		 }
		
}
