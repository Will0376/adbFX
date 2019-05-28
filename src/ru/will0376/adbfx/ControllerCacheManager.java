package ru.will0376.adbfx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import ru.will0376.adbfx.utils.Zip;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerCacheManager implements Initializable {
@FXML ListView list;
@FXML TextField time;

	String PathToSd = getSd().trim()+"/"; //get path to sd card.
	String PathToFolder = Controller.getController().getPath() +"CacheBackup/";
	String ver = "1.0 Alpha";
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Controller.getController().printText("Cache Manager module started! Version: "+ ver);
		Controller.getController().printText("Root only!");
		checkFolder();
   		 list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		refreshList();
	}
	public void delete(){
			 for(Object name : list.getItems()){
				 File file = new File(PathToFolder+name.toString());
				 file.delete();
			 }
	}

	/**
	 * 0.get path to sd card.
	 * 0.5. mkdir /sd/TempCacheRestone
	 *  1. push <home>/CacheBackup/name /sd/TempCacheRestone
	 *  2. su -c cp -r /sd/TempCacheRestone/name /data/data
	 *  (done)
	 *  3. rm -rf /sd/TempCacheRestone
	 */
	public void restore(){
		mkdirTemp();
		    for(Object name : list.getItems()){
		    	String nametar = name.toString();
		    	String namepkg = name.toString().replaceAll(".tar","");
				pushToTemp(nametar);
				unPackTar(nametar);
				start(namepkg);
				stop(namepkg);
				restoreFromTemp(namepkg);
				chmod(namepkg);
				}
		removeTemp();
		}
		private void unPackTar(String name){
			List<String> parm = new ArrayList<>();
			parm.add("shell");
			parm.add("cd");
			parm.add(PathToSd+"TempCacheRestore");
			parm.add("&&");
			parm.add("su");
			parm.add("-c");
			parm.add("tar");
			parm.add("-xf");
			parm.add(name);
			Controller.getController().startProgram((String[]) parm.toArray(new String[0]));
			while(Main.threadstartprogram.isAlive()){}
		}
		private void pushToTemp(String name){
			List<String> parm = new ArrayList<>();
			parm.add("push");
			parm.add(PathToFolder+name);
			parm.add(PathToSd+"TempCacheRestore/"+name);
			Controller.getController().startProgram((String[]) parm.toArray(new String[0]));
			while(Main.threadstartprogram.isAlive()){}
		}
		private void restoreFromTemp(String name){
			List<String> parm = new ArrayList<>();
			parm.add("shell");
			parm.add("su");
			parm.add("-c");
			parm.add("cp");
			parm.add("-r");
			parm.add(PathToSd+"TempCacheRestore/"+name);
			parm.add("/data/data/");
			Controller.getController().startProgram((String[]) parm.toArray(new String[0]));
			while(Main.threadstartprogram.isAlive()){}
		}
	private void mkdirTemp(){
		List<String> start = new ArrayList<>();
		start.add("shell");
		start.add("mkdir");
		start.add(PathToSd+"TempCacheRestore");
		Controller.getController().startProgram((String[]) start.toArray(new String[0]));
	}
	private void chmod(String name){
		List<String> start = new ArrayList<>();
		start.add("shell");
		start.add("su");
		start.add("-c");
		start.add("chmod");
		start.add("+x");
		start.add("-R");
		start.add("/data/data/"+name);
		Controller.getController().startProgram((String[]) start.toArray(new String[0]));
		while(Main.threadstartprogram.isAlive()){}
		start.clear();
		start.add("shell");
		start.add("su");
		start.add("-c");
		start.add("chmod");
		start.add("-R");
		start.add("771");
		start.add("/data/data/"+name);
		Controller.getController().startProgram((String[]) start.toArray(new String[0]));
		while(Main.threadstartprogram.isAlive()){}
	}
	private void removeTemp(){
		List<String> start = new ArrayList<>();
		start.add("shell");
		start.add("rm -rf");
		start.add(PathToSd+"TempCacheRestore");
		Controller.getController().startProgram((String[]) start.toArray(new String[0]));
	}
	private String getSd(){
		List<String> start = new ArrayList<>();
		start.add("shell");
		start.add("echo");
		start.add("$EXTERNAL_STORAGE");
		Controller.getController().startProgram((String[]) start.toArray(new String[0]));
		while(Main.threadstartprogram.isAlive()){}
		return Controller.getController().getOldlog();
	}
	private void start(String name){
		List<String> start = new ArrayList<>();
		start.add("shell");
		start.add("monkey");
		start.add("-p");
		start.add(name);
		start.add("1");
		Controller.getController().startProgram(false,(String[]) start.toArray(new String[0]));
		while(Main.threadstartprogram.isAlive()){}
		int i = 0;
		while( i <= Integer.valueOf(time.getText()) * 10000){
			i++;
		}
	}
	private void stop(String name){
		List<String> start = new ArrayList<>();
		start.add("shell");
		start.add("am");
		start.add("force-stop");
		start.add(name);
		int i = 0;
		while( i <= Integer.valueOf(time.getText()) * 10000){
			i++;
		}
		Controller.getController().startProgram(false,(String[]) start.toArray(new String[0]));
		while(Main.threadstartprogram.isAlive()){}
	}
	public void refreshList(){
		list.setItems(FXCollections.observableArrayList(""));
		List<String> listarray = new ArrayList<String>();
  			  File[] files = new File(PathToFolder).listFiles();
    			if(files != null) {
					for (File file : files) {
					listarray.add(file.getName());
					}
			list.setItems(FXCollections.observableArrayList(listarray));
		}
	}
	public void openFolder(){
		Controller.getController().openFolder(new File(PathToFolder));
	}
	private void checkFolder(){
		File folder = new File(PathToFolder);
		if(!folder.exists()){folder.mkdir();}
	}
}
