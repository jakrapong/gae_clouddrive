package com.jakrapong.clouddriveapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.ListResult;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class GCSUtil {
	private static final String bucketName = AppIdentityServiceFactory.getAppIdentityService().getDefaultGcsBucketName();

	public static List<MyFile> getFileList(String id) throws IOException{
		GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
		
		ListOptions.Builder bd = new ListOptions.Builder();
		bd.setRecursive(false);
		
		ListResult objList = gcsService.list(bucketName, bd.build());
		
		ArrayList<MyFile> fileNameList = new ArrayList<MyFile>();
		
		while(objList.hasNext()){
			ListItem l = objList.next();
		    String name = l.getName();
		    if(name.startsWith(id)){
		    	name = name.replace(id, "");
		    	
		    	MyFile f = new MyFile();
		    	f.name = name;
		    	
		    	f.length = String.format("%.2fMb",(l.getLength()/1000)/1000.0);
		    	
		    	SimpleDateFormat fFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
		    	fFormat.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
		    	f.lastModified = fFormat.format(l.getLastModified());
		    	
		    	fileNameList.add(f);
		    }
		}
		return fileNameList;
	}
	
	public static void saveFile(String id, String fileName, InputStream input) throws IOException{
		GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
		
		//write randon file
		GcsOutputChannel outputChannel = gcsService.createOrReplace(new GcsFilename(bucketName, (id+fileName)), GcsFileOptions.getDefaultInstance());
		
		byte buffer[] = new byte[1024*100];
		int len;
		while((len = input.read(buffer)) > 0){
			outputChannel.write(ByteBuffer.wrap(buffer, 0, len));
		}
		
		outputChannel.close();
	}
	
	public static void readFile(String id, String fileName, OutputStream op) throws IOException{
		GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
				
		GcsFilename f = new GcsFilename(bucketName, (id+fileName));
		
		int fileSize = (int) gcsService.getMetadata(f).getLength();
		ByteBuffer result = ByteBuffer.allocate(fileSize);
		try (GcsInputChannel readChannel = gcsService.openReadChannel(f, 0)) {
		  readChannel.read(result);
		  op.write(result.array());
		}
	}
	
	public static void deleteFile(String id, String fileName) throws IOException{
		GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
		
		gcsService.delete(new GcsFilename(bucketName, (id+fileName)));
	}
	
	public static class MyFile{
		public String name;
		public String length;
		public String lastModified;
	}
}
