package ru.will0376.adbfx;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class ControllerShell implements Initializable {

	   @FXML private TextArea inputShell;
	   private ResourceBundle resources;
	private String ver = "1.0";
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		Controller.getController().printText("Shell module started! Version: "+ver);
	}

		public void sendShell(ActionEvent event) {
		Controller.getController().printText("'"+inputShell.getText()+"':");
			Controller.getController().startProgram("shell", inputShell.getText());
		 }

		 public void doAboutThread(){
			 Main.threadstartprogram.stop();
			 Controller.getController().printText("Aborted!");
		 }
		 public void clearInput(){
		inputShell.clear();
		 }
}
