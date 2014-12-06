package com.mns.quiz.browser;

import java.util.Comparator;

import com.mns.quiz.parser.preference.QuizPreference;
import com.mns.quiz.parser.preference.QuizPreference.SortField;

public class FileListSorter implements Comparator<FileListEntry> {

	private FileBrowseManager mContext;
	
	private boolean dirsOnTop = false;
	private SortField sortField;
	private int dir;
	
	public FileListSorter(FileBrowseManager context){
		
		mContext = context;
		dirsOnTop = QuizPreference.getInstance().isShowDirsOnTop();
		sortField = QuizPreference.getInstance().getSortField();
		dir =  QuizPreference.getInstance().getSortDir();
	}
	
	@Override
	public int compare(FileListEntry file1, FileListEntry file2) {

		if(dirsOnTop)
		{
			if(file1.getPath().isDirectory() && file2.getPath().isFile())
			{
				return -1;
			}
			else if(file2.getPath().isDirectory() && file1.getPath().isFile())
			{
				return 1;
			}
		}
		
		switch (sortField) {
		case NAME:
			return dir * file1.getName().compareToIgnoreCase(file2.getName());
			
		case MTIME:
			return dir * file1.getLastModified().compareTo(file2.getLastModified());

		case SIZE:
			return dir * Long.valueOf(file1.getSize()).compareTo(file2.getSize());
			
		default:
			break;
		}
		
		return 0;
	}
	
	

}
