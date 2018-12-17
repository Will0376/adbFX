package ru.will0376.adbfx;

import javafx.scene.control.TextArea;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class DownloaderGH {
    TextArea log;
    public void init(TextArea log){
        this.log = log;
        if(!existsTempDirectory("folder"))
            createTempDirectory();
        if(!existsTempDirectory("adb.exe"))
            download("https://github.com/Will0376/adbFX/raw/master/adblibs/adb.exe","adb.exe");
        if(!existsTempDirectory("AdbWinApi.dll"))
            download("https://github.com/Will0376/adbFX/raw/master/adblibs/AdbWinApi.dll","AdbWinApi.dll");
        if(!existsTempDirectory("AdbWinUsbApi.dll"))
            download("https://github.com/Will0376/adbFX/raw/master/adblibs/AdbWinUsbApi.dll","AdbWinUsbApi.dll");
    }
    private static boolean isRedirected( Map<String, List<String>> header ) {
            for(String hv : header.get( null )) {
                if(hv.contains("301") || hv.contains("302"))
                    return true;
            }
            return false;
        }
        public void download(String... args)
        {try {
            String link = args[0];
            String fileName = args[1];
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            Map< String, List< String >> header = http.getHeaderFields();
            while(isRedirected( header )) {
                link = header.get("Location").get(0);
                url  = new URL(link);
                http = (HttpURLConnection)url.openConnection();
                header = http.getHeaderFields();
            }
            InputStream input  = http.getInputStream();
            byte[] buffer = new byte[4096];
            int n  = -1;
            OutputStream output = new FileOutputStream(new File(System.getProperty("user.home") + "\\.adblibs\\" + fileName));
            while ((n = input.read(buffer)) != -1) {
                output.write( buffer, 0, n );
            }
            output.close();
        } catch (IOException e) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter= new PrintWriter(writer);
            e.printStackTrace(printWriter);
            e.printStackTrace();
            log.appendText(writer.toString());
            }
        }
    public File createTempDirectory() {
        File tempfolder = new File(System.getProperty("user.home") + "\\.adblibs");
        if(!tempfolder.exists())
            tempfolder.mkdir();
        return tempfolder;
    }
    public boolean existsTempDirectory(String filename){
        File temp = null;
        if(!filename.equals("folder"))
            temp = new File(System.getProperty("user.home") + "\\.adblibs\\" + filename);
        else
            temp = new File(System.getProperty("user.home") + "\\.adblibs");

        return temp.exists();
    }
}
