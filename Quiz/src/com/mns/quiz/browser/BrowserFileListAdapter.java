package com.mns.quiz.browser;

import java.util.HashSet;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mns.quiz.R;
import com.mns.quiz.uitity.Util;

public class BrowserFileListAdapter extends BaseAdapter implements OnCheckedChangeListener {

	public static class ViewHolder 
	{
	  public TextView resName;
	  public ImageView resIcon;
	  public TextView resMeta;
	  public CheckBox resSelected;
	}

	private static final String TAG = BrowserFileListAdapter.class.getName();
	  
	private FileBrowseManager mContext;
	List<FileListEntry> files;
	HashSet<FileListEntry> selectedFiles;
	private LayoutInflater mInflater;
	
	public BrowserFileListAdapter(FileBrowseManager context, List<FileListEntry> files) {
		super();
		mContext = context;
		this.files = files;
		mInflater = mContext.getLayoutInflater();
		selectedFiles=new HashSet<FileListEntry>();
		
	}

	
	@Override
	public int getCount() {
		if(files == null)
		{
			return 0;
		}
		else
		{
			return files.size();
		}
	}

	@Override
	public Object getItem(int arg0) {

		if(files == null)
			return null;
		else
			return files.get(arg0);
	}

	public List<FileListEntry> getItems()
	{
	  return files;
	}
	
	@Override
	public long getItemId(int position) {

		return position;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.file_browser_item, parent, false);
            holder = new ViewHolder();
            holder.resName = (TextView)convertView.findViewById(R.id.explorer_resName);
            holder.resMeta = (TextView)convertView.findViewById(R.id.explorer_resMeta);
            holder.resIcon = (ImageView)convertView.findViewById(R.id.explorer_resIcon);
            holder.resSelected=(CheckBox)convertView.findViewById(R.id.file_select_btn);
            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        FileListEntry currentFile = files.get(position);
        holder.resName.setText(currentFile.getName());
        holder.resIcon.setImageDrawable(Util.getIcon(mContext, currentFile.getPath()));
        String meta = Util.prepareMeta(currentFile, mContext);
        holder.resMeta.setText(meta);
        holder.resSelected.setTag(R.id.POSITION, position);
        if(currentFile.getPath().isDirectory()){
        	holder.resSelected.setVisibility(View.GONE);
        }else{
        	
       	holder.resSelected.setVisibility(View.VISIBLE);
        holder.resSelected.setOnCheckedChangeListener(this);
				holder.resSelected.setChecked(selectedFiles.contains(currentFile)?true:false);
        }
        
        return convertView;
	}


	public void setList(List<FileListEntry> fileList) {
		this.files=fileList;		
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		 int pos=(int) buttonView.getTag(R.id.POSITION);
		 if(isChecked){
			 selectedFiles.add(files.get(pos));
		 }else{
			 selectedFiles.remove(files.get(pos));
		 }
		mContext.updateActionBar(selectedFiles);

		
	}


	public void updateList() {
		selectedFiles=new HashSet<FileListEntry>();
	}

}
