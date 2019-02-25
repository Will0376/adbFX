package ru.will0376.adbfx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerAppManager implements Initializable {

	@FXML
	ListView<String> list;
	@FXML
	CheckBox k;
	@FXML
	CheckBox user0;
	@FXML
	CheckBox rootEnabler;
	@FXML
	TextField filterfield;

	private List<String> PkgList = new ArrayList<>();
	private String ver = "1.1";
	private File backupApk = new File(Vars.c.getPath()+"/backupAPK");
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Vars.c.printText("Apk Manager module started! Version: " + ver);
		checkFolder();
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	filling();
		filterfield.textProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> filterList((String) oldValue, (String) newValue));
	}

	private void filterList(String oldValue, String newValue) {

		ObservableList<String> filteredList = FXCollections.observableArrayList();
		if(filterfield == null || (newValue.length() < oldValue.length()) || newValue == null) {
			list.setItems(FXCollections.observableArrayList(PkgList));
		}
		else {
			newValue = newValue.toUpperCase();
			for(String plants : list.getItems()) {
				if(plants.toUpperCase().contains(newValue)) {
					filteredList.add(plants);
				}
			}
			list.setItems(filteredList);
		}
	}
	public void filling(){
		list.setItems(FXCollections.observableArrayList(""));

	for(String pkg : getPkg()){
		if(!pkg.equals("") && !pkg.equals(" "))
			PkgList.add(pkg);

		}
	list.setItems(FXCollections.observableArrayList(PkgList));
	}
	public void delete() {
		List<String> start = new ArrayList<String>();
		start.add("shell");
		if(rootEnabler.isSelected()){
			start.add("su");
			start.add("-c");
		}
		start.add("pm");
		start.add("uninstall");
		if (k.isSelected())
			start.add("-k");
		if (user0.isSelected()) {
			start.add("--user");
			start.add("0");
		}

		for (String path : list.getSelectionModel().getSelectedItems()) {
			start.add(path);
			String[] tmp = (String[]) start.stream().toArray(String[]::new);

			Platform.runLater(() -> {
				Vars.c.printText(Vars.c.printRes(true, "appman.deleting") + path + ":");
				Vars.c.startProgram(tmp);
				while (Vars.threadstartprogram.isAlive()) {
				}
			});
			start.remove(start.size() - 1);
		}
		filling();
	}
	public void backup(){
		/**
		 * backup:
		 * adb shell pm path com.example.someapp
		 adb pull /data/app/com.example.someapp-2.apk path/to/desired/destination
		 */
		Platform.runLater(() -> {
			List<String> start = new ArrayList<String>();
			start.add("shell");
			if(rootEnabler.isSelected()){
				start.add("su");
				start.add("-c");
			}
			start.add("pm");
			start.add("path");
			for (String path : list.getSelectionModel().getSelectedItems()) {
				start.add(path);
				String[] tmp = (String[]) start.stream().toArray(String[]::new);

				Vars.c.printText(path + ":");
				Vars.c.startProgram(tmp);
				while (Vars.threadstartprogram.isAlive()) { }
				start.remove(start.size() - 1);
				///
				List<String> start2 = new ArrayList<>();
				start2.add("pull");
				start2.add(Vars.c.oldlog.split("package:")[1].trim());
				start2.add(backupApk.getAbsolutePath());
				String[] tmp2 = (String[]) start2.stream().toArray(String[]::new);
				Vars.c.startProgram(tmp2);
				while (Vars.threadstartprogram.isAlive()){}

				if(new File(backupApk.getAbsolutePath()+"/base.apk").exists()){
					new File(backupApk.getAbsolutePath() + "/base.apk").renameTo(new File(backupApk.getAbsolutePath()+"/" + path+".apk"));
				}
			}
		});
	}
	private void checkFolder(){
		if(backupApk.exists()){ }
		else backupApk.mkdir();
	}
	public void reloadList(){
		filling();
	}
	private List<String> getPkg(){
		Vars.c.startProgram(false,"shell","\"pm list packages\"|cut -f 2 -d \":\"");
		while(Vars.threadstartprogram.isAlive()){ } //wait :D
		List<String> pkg = Arrays.asList(Vars.c.getOldlog().split("\\s"));
		return pkg;
	}
}
