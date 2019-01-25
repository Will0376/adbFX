package ru.will0376.adbfx;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import ru.will0376.adbfx.Locales.Vars;

public class ControllerShell implements Initializable {

	   @FXML
	   private TextArea inputShell;
	   ResourceBundle resources;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		Vars.c.printText("Shell module started!");
	}

		public void sendShell(ActionEvent event) {
		Vars.c.printText("'"+inputShell.getText()+"':");
			Vars.c.startProgram("shell", inputShell.getText());
		 }

		 public void doAboutThread(){
			 Vars.threadstartprogram.stop();
			 Vars.c.printText("Aborted!");
		 }
		 public void clearInput(){
		inputShell.clear();
		 }
}
