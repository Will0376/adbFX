package ru.will0376.adbfx;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class ControllerShell implements Initializable {

	   @FXML
	   private TextArea inputShell;
	   ResourceBundle resources;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		Main.c.printText("Shell module started!");
	}

		public void sendShell(ActionEvent event) {
		Main.c.printText("'"+inputShell.getText()+"':");
			Main.c.startProgram("shell", inputShell.getText());
		 }

		 public void doAboutThread(){
		Main.threadstartprogram.stop();
		Main.c.printText("Aborted!");
		 }
		 public void clearInput(){
		inputShell.clear();
		 }
}
