package ru.will0376.adbfx;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class Controller_Wifi_Main implements Initializable  {
	 @FXML private TextField textip;

	ResourceBundle resources;
	String adbfile = Controller.getController().pathtoadb.toString();
	String ver = "1.0";
	@Override
	public void initialize(URL loc, ResourceBundle resources) {
		this.resources = resources;
		textip.setText(getIpFromFile().replaceAll("null", ""));
		Controller.getController().printText("Wi-Fi module started! Version: "+ver);
	}
	public void connect(ActionEvent event) {
		System.out.println("file: "+adbfile);
		Controller.getController().printRes(false,"key.wifi.main.ConnectTo"," "+textip.getText());
		execute("connect",  textip.getText());
}
	public void disconnect(ActionEvent event) {
		Controller.getController().printRes(false,"key.wifi.main.Disconnected");
		execute("kill-server");
	}
	public void reconnect(ActionEvent event) {
		Controller.getController().printRes(false,"key.wifi.main.RecconectTo"," "+textip.getText());
		execute("kill-server");
		execute("connect", textip.getText());
	}
	public void execute(String... command) {
		Controller.getController().startProgram(command);
	}
		private String getIpFromFile() {
			File file = getpath();
			if (file.exists()) {
				try {
					FileReader fr = new FileReader(file); 
				      char [] a = new char[200];
				      fr.read(a);
				      String ret = null;
				      for(char c : a) {
				         ret += c;
				      }
				      fr.close();
				      return ret.replaceAll("Ip:", "");
				} catch ( IOException e) {
					e.printStackTrace();
				}
			}
			return "Error read file";
		}
		public void saveIpToFile() {
			File file = getpath();
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file);
				fw.write("Ip:"+textip.getText());
				System.out.println("Saved! " + "Ip:"+textip.getText());
				fw.flush();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		private File getpath(){
				return new File(Controller.getController().getPath()+"config.cfg");
		}
			
}
