package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ControllerFAQ implements Initializable {
	String locale = null;
	
	@FXML
	TextArea flFAQ;
	@FXML
	Button reloadJs;
	
	ResourceBundle resources;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		flFAQ.setWrapText(true);
		if(!Main.debug)
			reloadJs.setVisible(false);
		getLang();
		getText();
		printToFAQ();
	}
	public void getLang() {
		locale = new Locale(Locale.getDefault().getLanguage()).toString();
	}
	public void changeLangRU() throws IOException {
		locale = "ru";
		flFAQ.clear();
		printToFAQ();
	}
	public void changeLangEN() throws IOException {
		locale = "en";
		flFAQ.clear();
		printToFAQ();
	}
	public void reloadJSON() {
		flFAQ.clear();
		printToFAQ();
	}
	
	
	public void printToFAQ() {
	ArrayList<String> text = getText();
	if(!netIsAvailable() || Main.debug) {
		printRes("key.faq.lcopy");
	}
		System.out.println("size: "+text.size());
			for(int i = 0;i < text.size();i++) {
				printText(text.get(i));
				printRes("key.all.Tire");
			}
	}
	
	private ArrayList<String> getText() {
		ArrayList<String> text = new ArrayList<String>();
		try {
			if(locale != null) {
				if(!netIsAvailable() || Main.debug) {
					JSONObject js = reanJsonFromFile("ru/will0376/adbfx/Faq/"+locale+"_FAQ.json");
					text = patseText(js);
					
				}
				else {
					JSONObject json = readJsonFromUrl("https://raw.githubusercontent.com/Will0376/adbFX/master/src/ru/will0376/adbfx/Faq/"+locale+"_FAQ.json");
					text = patseText(json);
				}
			}
		}
			catch(IOException | JSONException e) {
				e.printStackTrace();
			}
		return text;
	}
	private ArrayList<String> patseText(JSONObject json) {
		ArrayList<String> text = new ArrayList<String>();
		try {
		text.add("FAQ version: "+(String)json.get("versionFaq"));
		}
		catch(JSONException e) {
		}
		for(int i = 0; i < json.length();i++) {
			try {
				text.add((String)json.get((i + 1) + ""));
			}
			catch(JSONException e) {
				break;
			}
		}
		return text;
		
	}
	
	
	
	///
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		 try {
		   BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		   String jsonText = readAll(rd);
		   JSONObject json = new JSONObject(jsonText);
		   return json;
		}
		 finally {
		   is.close();
		}
	}
	public static JSONObject reanJsonFromFile(String file) throws IOException, JSONException {
		@SuppressWarnings("static-access")
		InputStream inputStream = ClassLoader.getSystemClassLoader().getSystemResourceAsStream(file);
		try {
		InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
		BufferedReader in = new BufferedReader(streamReader);
		String jsonText = readAll(in);
		   JSONObject json = new JSONObject(jsonText);
		   return json;
		}
		 finally {
			 inputStream.close();
		}
	}
	
	public static boolean ret;
	private static boolean netIsAvailable() {
		
		Thread th = new Thread(new Runnable(){ 
			public void run() {
	    try {
	        final URL url = new URL("http://www.google.com");
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
	        ret = true;
	    } catch (MalformedURLException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	    	ret = false;
	    }
			}
		});
		th.start();
		while(th.isAlive()) {
		}
		return ret;
	}
	
	public void printText(String text) {
		flFAQ.appendText(text + System.getProperty("line.separator"));
	 }
	
	public void printRes(String... key) {
		try {
		if(key.length == 2)
			printText(resources.getString(key[0]) + key[1]);
		else
			printText(resources.getString(key[0]));
		}
		catch(MissingResourceException e) {
			e.printStackTrace();
			printText("Error! Key: "+ key[0] +" not found! Locale:" + resources.getLocale() );
		}
	}
}
