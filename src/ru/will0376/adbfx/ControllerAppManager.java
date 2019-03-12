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

	@FXML ListView<String> list;
	@FXML CheckBox k;
	@FXML CheckBox user0;
	@FXML CheckBox rootEnabler;
	@FXML CheckBox cache;
	@FXML TextField filterfield;
	@FXML Button deletebutton;
	@FXML Button backupbutton;

	private List<String> PkgList = new ArrayList<>();
	private String ver = "1.1";
	private String vercache = "1.0";
	private File backupApk = new File(Vars.c.getPath()+"/backupAPK");
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Vars.c.printText("Apk Manager module started! Version: " + ver);
		Vars.c.printText("Cache Backup module started! Version: " + vercache);
		checkFolder();
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			filling();
		filterfield.textProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> filterList((String) oldValue, (String) newValue));
		deletebutton.setFocusTraversable(false);
		filterfield.setFocusTraversable(true);

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
			String[] tmp = (String[]) start.toArray(new String[0]);

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
	String PathToSd = getSd(); //get path to sd card.
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
			if(cache.isSelected()){ // mkdir /sd/tempcache
				mkdirTemp();
			}
			for (String path : list.getSelectionModel().getSelectedItems()) {
				start.add(path);
				String[] tmp = (String[]) start.toArray(new String[0]);

				Vars.c.printText(path + ":");
				Vars.c.startProgram(tmp);
				while (Vars.threadstartprogram.isAlive()) { }
				start.remove(start.size() - 1);
				///
				copyApkFromDevice();
				renameApkTmp(path);

				if(cache.isSelected()){
					saveCache(path);
				}
			}
		if(cache.isSelected()){
			removeTemp();
		}
		});
	}
	private void copyApkFromDevice(){
		List<String> start2 = new ArrayList<>();
		start2.add("pull");
		start2.add(Vars.c.oldlog.split("package:")[1].trim());
		start2.add(backupApk.getAbsolutePath());
		String[] tmp2 = (String[]) start2.toArray(new String[0]);
		Vars.c.startProgram(tmp2);
		while (Vars.threadstartprogram.isAlive()){}
	}
	private void renameApkTmp(String name){
		if(new File(backupApk.getAbsolutePath()+"/base.apk").exists()){
			new File(backupApk.getAbsolutePath() + "/base.apk").renameTo(new File(backupApk.getAbsolutePath()+"/" + name+".apk"));
		}
	}
	/**
	 *  0. mkdir /sd/tempcache
	 * 1.su -c cp -r /data/data/pak.name /sd/tempcache
		2. pull /sd/tempcache/pak.name <home>/CacheBackup/pak.name
	 if(done)
	 3. rm -rf /sd/tempcache
	 */
	private void mkdirTemp(){
		List<String> start2 = new ArrayList<String>();
		start2.add("shell");
		start2.add("mkdir");
		start2.add(PathToSd.trim()+"/tempcache");
		Vars.c.startProgram((String[]) start2.toArray(new String[0]));
	}
	private void removeTemp(){
		List<String> start2 = new ArrayList<String>();
		start2.add("shell");
		start2.add("rm -rf");
		start2.add(PathToSd.trim()+"/tempcache");
		Vars.c.startProgram((String[]) start2.toArray(new String[0]));
	}
	private void saveCache(String name){
		List<String> start = new ArrayList<String>();

		start.add("shell");start.add("su");start.add("-c");start.add("cp");start.add("-r");
		start.add("/data/data/"+name);
		start.add(PathToSd.trim()+"/tempcache/");
		Vars.c.startProgram((String[]) start.toArray(new String[0]));
		while (Vars.threadstartprogram.isAlive()){}
		pullCache(name);
	}
	private void pullCache(String name){
		List<String> start = new ArrayList<String>();

		start.add("pull");
		start.add(PathToSd.trim()+"/tempcache/"+name);
		start.add(Vars.c.getPath()+"CacheBackup/"+name);
		Vars.c.startProgram((String[]) start.toArray(new String[0]));
		while (Vars.threadstartprogram.isAlive()){}
	}
	private String getSd(){
		List<String> start = new ArrayList<>();
		start.add("shell");
		start.add("echo");
		start.add("$EXTERNAL_STORAGE");
		Vars.c.startProgram((String[]) start.toArray(new String[0]));
		while(Vars.threadstartprogram.isAlive()){}
		return Vars.c.getOldlog();
	}

	private void checkFolder(){
		if(!backupApk.exists()){ backupApk.mkdir();}
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
