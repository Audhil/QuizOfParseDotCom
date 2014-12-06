package com.mns.quiz.uitity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.apache.commons.io.FileUtils;
import com.mns.quiz.R;
import com.mns.quiz.browser.FileListEntry;
import com.mns.quiz.browser.FileBrowseManager;

public class Util {
	
	private static String TAG=Util.class.getName();

	public static boolean isProtected(File path)
	{
		return (!path.canRead() && !path.canWrite());
	}
	
	public static boolean isUnzippable(File path)
	{
		return (path.isFile() && path.canRead() && path.getName().endsWith(".zip"));
	}


	public static boolean isRoot(File dir) {
		
		return dir.getAbsolutePath().equals("/");
	}


	public static boolean isSdCard(File file) {
		
		try {
			return (file.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getCanonicalPath()));
		} catch (IOException e) {
			return false;
		}
		
	}
	

	static boolean isMusic(File file) {

		Uri uri = Uri.fromFile(file);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		
		if(type == null)
			return false;
		else
		return (type.toLowerCase().startsWith("audio/"));

	}

	static boolean isVideo(File file) {

		Uri uri = Uri.fromFile(file);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		
		if(type == null)
			return false;
		else
		return (type.toLowerCase().startsWith("video/"));
	}

	public static boolean isPicture(File file) {
		
		Uri uri = Uri.fromFile(file);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		
		if(type == null)
			return false;
		else
		return (type.toLowerCase().startsWith("image/"));
	}
	
	public static Drawable getIcon(Context mContext, File file) {
		
		if(!file.isFile()) //dir
		{
			if(Util.isProtected(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_sys_dir);
					
			}
			else if(Util.isSdCard(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_sdcard);
			}
			else 
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_dir);
			}
		}
		else //file
		{
			String fileName = file.getName();
			if(Util.isProtected(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_sys_file);
					
			}
			if(fileName.endsWith(".apk"))
			{
//				return mContext.getResources().getDrawable(R.drawable.filetype_apk);
				return mContext.getResources().getDrawable(R.drawable.filetype_generic);
			}
			if(fileName.endsWith(".zip"))
			{
//				return mContext.getResources().getDrawable(R.drawable.filetype_zip);
				return mContext.getResources().getDrawable(R.drawable.filetype_generic);
			}
			else if(Util.isMusic(file))
			{
//				return mContext.getResources().getDrawable(R.drawable.filetype_music);
				return mContext.getResources().getDrawable(R.drawable.filetype_generic);
			}
			else if(Util.isVideo(file))
			{
//				return mContext.getResources().getDrawable(R.drawable.filetype_video);
				return mContext.getResources().getDrawable(R.drawable.filetype_generic);
			}
			else if(Util.isPicture(file))
			{
//				return mContext.getResources().getDrawable(R.drawable.filetype_image);
				return mContext.getResources().getDrawable(R.drawable.filetype_generic);
			}
			else
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_generic);
			}
		}
		
	}

		public static String prepareMeta(FileListEntry file,FileBrowseManager context) {
		
		File f = file.getPath();
		try
		{
			if(isProtected(f))
			{
				return context.getString(R.string.system_path);
			}
			if(file.getPath().isFile())
			{
				return context.getString(R.string.size_is, FileUtils.byteCountToDisplaySize(file.getSize()));
			}
			
		}
		catch (Exception e) {
			Log.e(Util.class.getName(), e.getMessage());
		}
		
		return "";
	}
		
		public static Map<String, Long> getDirSizes(File dir)
		{
//			System.out.println("-----get Dir Sizes:"+dir.getAbsolutePath());
			Map<String, Long> sizes = new HashMap<String, Long>();
			
			try {
				
				Process du = Runtime.getRuntime().exec("/system/bin/du -b -d1 "+dir.getCanonicalPath(), new String[]{}, Environment.getRootDirectory());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(du.getInputStream()));
				String line = null;
				while ((line = in.readLine()) != null)
				{
					String[] parts = line.split("\\s+");
					
					String sizeStr = parts[0];
					Long size = Long.parseLong(sizeStr);
					
					String path = parts[1];
					
					sizes.put(path, size);
				}
				
			} catch (IOException e) {
//				System.out.println("-----Error"+e);
				Log.w(TAG, "Could not execute DU command for "+dir.getAbsolutePath(), e);
			}
//			System.out.println("sizes"+sizes.size());
			return sizes;
			
		}


}
