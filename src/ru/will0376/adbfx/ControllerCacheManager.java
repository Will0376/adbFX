package ru.will0376.adbfx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import ru.will0376.adbfx.utils.Zip;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerCacheManager implements Initializable {
@FXML ListView list;
 //pre-alpha
  String PathToFolder = Vars.c.getPath() +"CacheBackup/";
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Vars.c.printText("Cache Manager pre-Alpha");
		checkFolder();
   		 list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
		String PathToTmpFolder = PathToFolder + "tmp/";
		    for(Object name : list.getItems()){
		    	//new Zip().unZip(new File(PathToFolder + name.toString()),PathToTmpFolder);
		    	//new File(PathToTmpFolder).renameTo(new File(PathToFolder + name.toString()));
   					 List<String> parm = new ArrayList<>();
  						 parm.add("push");
  						 parm.add(PathToFolder + name.toString());
 						  parm.add("/data/data/" + name.toString());
		  			//  Vars.c.startProgram((String[]) parm.stream().toArray(String[]::new));
				}
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
		Vars.c.openFolder(new File(PathToFolder));
	}
	private void checkFolder(){
		File folder = new File(PathToFolder);
		if(!folder.exists()){folder.mkdir();}
	}
}
