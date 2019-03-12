package ru.will0376.adbfx.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
	public void unZip(File file,String output){
		try(ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(output));
			FileInputStream fis= new FileInputStream(file))
		{
			ZipEntry entry1=new ZipEntry("notes.txt");
			zout.putNextEntry(entry1);
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			zout.write(buffer);
			zout.closeEntry();
		}
		catch(Exception ex){

			System.out.println(ex.getMessage());
		}
	}
}
