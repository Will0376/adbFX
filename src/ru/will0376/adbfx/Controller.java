package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import com.tananaev.adblib.AdbBase64;
import com.tananaev.adblib.AdbConnection;
import com.tananaev.adblib.AdbCrypto;
import com.tananaev.adblib.AdbStream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.managers.PackageManager;

public class Controller implements Initializable {
	 public String n = System.getProperty("line.separator");
	   @FXML
	   private Button button;
	  
	   @FXML
	   private TextArea TextField;
	   @FXML
	   private TextField textFwifi;
	   
	   @FXML
	   private BorderPane v;
   
	   
	   @SuppressWarnings("unused")
	private boolean localhost = false;
	   AdbConnection devices = null;
	  
		@Override
		public void initialize(URL location, ResourceBundle resources) {
			TextField.setWrapText(true);
			TextField.setEditable(false);
			TextField.clear();
			TextField.setText("AdbFX Version: "+Main.ver+n);
			startUP();
		}
	 
	   public void сlearFl(ActionEvent event) {
		   TextField.clear();
	   }
	   
	   public void refleshDevices(ActionEvent event) {
		   startUP();
	   }
	   
	   protected void startUP() {
			List<JadbDevice> devices = connectToPhone();
			if (devices != null) {
				System.out.println(devices.get(0));
				TextField.appendText(devices.get(0).toString()+n);
			}
		}
	   public void wifiConnect() throws JadbException {
		   String ipport = textFwifi.getText();
		  int port;
		  String ip;
		  boolean valid;
		  boolean ipp;
		  if(ipport.contains(":")) {
			  valid = validIpPort(ipport, true);
			  ipp = true;
		  }
		  else {
			  valid = validIpPort(ipport, false);
			  ipp = false;
		  }
		  if(valid) {
			  if(ipp) {
				  String[] isbnParts = ipport.split(":");
				  ip = isbnParts[0];
				  port = Integer.parseInt(isbnParts[1]);
			  }
			  else {
				  ip = ipport;
				  port = 5555;
			  }
				this.devices = connectToPhoneWifi(ip,port);
				try {
					this.devices.connect();
					System.out.println("Connected!");
					TextField.appendText("Connected!"+n);
					AdbStream stream = this.devices.open("shell:uname -a");
					
					System.out.println(stream.read());
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			  /*else {
			  List<JadbDevice> devices = connectToPhone();
			  if (devices != null) {
				System.out.println(devices.get(0));
				TextField.appendText(devices.get(0).toString()+n);
			  }
			 }*/
				
		  }
		  else {
			  System.out.println("It is not ip");
			  TextField.appendText("It is not ip"+n);
		  }
		   
	   }
	   public void disconnect() {
		   if(this.devices != null) {
			   try {
				this.devices.close();
				System.out.println("Disconnected!");
				TextField.appendText("Disconnected!"+n);
			} catch (IOException e) {
				e.printStackTrace();
			}
		   }
	   }
	   
	   public boolean validIpPort(String ipp,boolean port) {
		   if(port) {
		   Pattern pattern = Pattern.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+");
			  Matcher m = pattern.matcher(ipp);
			  return m.matches();  
		   }
		   else {
			   Pattern pattern = Pattern.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
				  Matcher m = pattern.matcher(ipp);
				  return m.matches();  
		   }
		  }
	   public void pullUname() {
			try {
				 
				 List<JadbDevice> devices = connectToPhone();
					 if(devices != null) {
					 String text = null;
						try (BufferedReader br = new BufferedReader(new InputStreamReader(devices.get(0).executeShell("uname", "-a"), "UTF-8"))) {
							text = br.lines().collect(Collectors.joining(System.lineSeparator()));
						}
						System.out.println(text);
						TextField.appendText(text+n);
					 }
			} catch (IOException | JadbException e) {
				e.printStackTrace();
			}
			
	   }
	   public void saveToFile() {
		   DateFormat dateFormat = new SimpleDateFormat("dd.MM.HH.mm.ss");
			Date date = new Date();
		   File log = new File("./Log"+dateFormat.format(date)+".log");
		   if(!log.exists()) {
			   try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 if(log.exists()) {
				 try {
					FileWriter writer = new FileWriter(log);
					writer.write(TextField.getText());
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
			   
		   }
	   }
	   public void openFiles(ActionEvent event) {
		    FileChooser fileChooser = new FileChooser();
                List<File> files = fileChooser.showOpenMultipleDialog((Window) Main.ps);
                if (files != null) {
                	installToPhone(files);
                }          
		    
	   }
	   public void installToPhone(List<File> files) {
			  
			   Thread install = new Thread(new Runnable() {
				
				@Override
				public void run() {
					 List<JadbDevice> devices = connectToPhone();
					for(int i = 0;i< files.size();i++) {
						String pt = files.get(i).toPath().toString();
						String end = pt.substring(pt.lastIndexOf("."), pt.length());
						if(!end.equals(".apk")) {
							System.out.println(files.get(i)+" is not apk");
							TextField.appendText(files.get(i)+" is not apk"+n);
						}
						else {
						TextField.appendText("Install("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString()+n);
						System.out.println("Install("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
						
						
					try {
						new PackageManager(devices.get(0)).install(files.get(i));
					} catch (IOException | JadbException | NullPointerException e) {
						StringWriter writer = new StringWriter();
			            PrintWriter printWriter= new PrintWriter(writer);
			            e.printStackTrace(printWriter);
						System.out.println("~~~~FAIL!!!~~~~");
						e.printStackTrace();
						TextField.appendText("~~~~FAIL!!!~~~~"+n);
						TextField.appendText(writer.toString());
					}
					System.out.println("Success");
					TextField.appendText("Success"+n);
					
				}
			  }
			 }
			});
			   install.start();		   
	   }
	   
	   public List<JadbDevice> connectToPhone() {
		   try {
				JadbConnection jadb = new JadbConnection();
				List<JadbDevice> devices = jadb.getDevices();
				if(devices.size() == 0) {
					System.out.println("No devices found(Reconnect phone)");
					TextField.appendText("No devices found(Reconnect phone)"+n);
				}
				else if(devices.size() >= 1) {
					return devices;
					
				}
			} catch (IOException | JadbException e) {
				e.printStackTrace();
			}
		  return null;
	   }
	  public AdbConnection connectToPhoneWifi(String ip,int port) {
		try {
			Socket socket = new Socket(ip, port);
			AdbCrypto crypto = AdbCrypto.generateAdbKeyPair(new AdbBase64() {
			      @Override
			      public String encodeToString(byte[] data) {
			          return DatatypeConverter.printBase64Binary(data);
			      }
			      
			  });
			System.out.println(crypto.getAdbPublicKeyPayload());
			//AdbCrypto cc = AdbCrypto.generateAdbKeyPair(base64);
			  AdbConnection connection = AdbConnection.create(socket, crypto);
			  return connection;
			  //connection.connect();
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
		

		  

	  }
	   public void openAbout(ActionEvent event) {
		   try {
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/About.fxml"));
	            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
	            Parent root1 = fxmlLoader.load();
	            Stage stage1 = new Stage();
	            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
	            stage1.setTitle("About");
	            stage1.setScene(new Scene(root1, 400, 270));
	            stage1.setResizable(false);
	            stage1.show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
	   public void adbShell(ActionEvent event) {
		   try {
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/Shell.fxml"));
	            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
	            Parent root1 = fxmlLoader.load();
	            Stage stage1 = new Stage();
	            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
	            stage1.setTitle("Shell(Beta!)");
	            stage1.setScene(new Scene(root1, 668, 378));
	            stage1.setResizable(false);
	            stage1.show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
	   public void doExit(ActionEvent event) {
	        Platform.exit();
	        System.exit(0);
	   }
}
	  