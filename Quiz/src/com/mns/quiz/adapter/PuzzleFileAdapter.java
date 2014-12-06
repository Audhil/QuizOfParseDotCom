package com.mns.quiz.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mns.quiz.Debug;
import com.mns.quiz.R;
import com.mns.quiz.parse.module.Puzzle;

public class PuzzleFileAdapter extends ArrayAdapter<Puzzle> {
	public class ViewHolder {

		public TextView puzzleTitle;

	}

	ArrayList<Puzzle> puzzleList=new ArrayList<Puzzle>();
	private TextView text;
	public PuzzleFileAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		
	}
	
	@Override
	public int getCount() {
		return puzzleList.size();
	}
	@Override
	public Puzzle getItem(int position) {
		return puzzleList.get(position);
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	 
		Puzzle puzzle = puzzleList.get(position);
	
		
		// Get the data item for this position
//	       User user = getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       ViewHolder viewHolder; // view lookup cache stored in tag
	       if (convertView == null) {
	          viewHolder = new ViewHolder();
	          LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
	  		convertView = inflater.inflate(R.layout.puzzle_file_item, parent, false);
	  		viewHolder.puzzleTitle = (TextView) convertView.findViewById(R.id.text);
	         
	        	 
	          convertView.setTag(viewHolder);
	       } else {
	           viewHolder = (ViewHolder) convertView.getTag();
	       }
	       // Populate the data into the template view using the data object
	       viewHolder.puzzleTitle.setText(puzzle.getTitle());
	        
	       return convertView;
		
	}

	public void updateList(ArrayList<Puzzle> puzzleList) {
		this.puzzleList = puzzleList;	
		Debug.print("update List"+puzzleList.size());
		notifyDataSetChanged();
	}

	public void setList(ArrayList<Puzzle> fileList) {
		if(fileList!=null)
			puzzleList=fileList;
		else{
			puzzleList=new ArrayList<Puzzle>();
		}
		
		 
	}
	
}