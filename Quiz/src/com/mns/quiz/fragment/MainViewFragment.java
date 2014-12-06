package com.mns.quiz.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mns.quiz.Debug;
import com.mns.quiz.MainActivity;
import com.mns.quiz.R;
import com.mns.quiz.adapter.PuzzleFileAdapter;
import com.mns.quiz.api.ApiCallback;
import com.mns.quiz.api.ParseApi;
import com.mns.quiz.api.ParseApi.DefaultApiCallback;
import com.mns.quiz.listener.UpdatePuzzleListListener;
import com.mns.quiz.parse.module.Clue;
import com.mns.quiz.parse.module.Puzzle;
import com.mns.quiz.parse.module.PuzzleClues;
import com.mns.quiz.parser.PuzParserTask;
import com.mns.quiz.parser.preference.DatabaseQuery;
import com.mns.quiz.receiver.NetworkUtil;
import com.parse.ParseException;
import com.parse.ParseQuery;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class MainViewFragment extends Fragment implements View.OnClickListener,UpdatePuzzleListListener {

	private String LOG_TAG = MainViewFragment.class.getName();
	ArrayList<Puzzle> mPuzzles = null;

	public MainViewFragment() {

		ParseApi.getInstance().fetchConfigIfNeeded();

	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Debug.print("MainView OnCreate");
		
		
	}


	SharedPreferences sharedPref;
	private ListView mPuzzleListView;
	private PuzzleFileAdapter mAdapter;
	private RelativeLayout mInfoView;
	private TextView mInfoTxt;
	private Button mRefresh;
	
	private void updateParseData(){
		Debug.print("update parse Data");
		 
		if(NetworkUtil.getConnectivityStatusString(getActivity())){
			fetchParsePuzzles(false);
	//		fetchLocalClues();
	//		fetchClues();
		}else{
			fetchLocalPuzzles(false);
			fetchLocalClues();
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Debug.print("MainView OnCreateView");
		View view = inflater.inflate(R.layout.main_view_fragment, container,
				false);

		mPuzzleListView = (ListView) view.findViewById(R.id.listview);
		mInfoView = (RelativeLayout) view.findViewById(R.id.info_view);
		mInfoTxt = (TextView) view.findViewById(R.id.info_msg_txt);
		mRefresh = (Button) view.findViewById(R.id.refresh_btn);
		mRefresh.setOnClickListener(this);
		mInfoView.setVisibility(View.GONE);
		mAdapter = new PuzzleFileAdapter(getActivity(),R.layout.puzzle_file_item);
		if(savedInstanceState!=null){
			mPuzzles=savedInstanceState.getParcelableArrayList("puzzles");
		}
		 System.out.println("onCreate"+mPuzzles);
		mAdapter.setList(mPuzzles);
		
		mPuzzleListView.setAdapter(mAdapter);
//		 updateListAdapter();
		mPuzzleListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Debug.print("id---------------->"+ mPuzzles.get(position).getObjectId());
				((MainActivity)getActivity()).switchToPlayFragment(mPuzzles.get(position).getObjectId());

			}
		});
//		updateMainViewUI();
		Button addBtn = (Button) view.findViewById(R.id.add_buzzle_file_id);
addBtn.setOnClickListener(this);
updateParseData();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	Handler mHandler;
	HashMap<String, Integer> ips = new HashMap<String, Integer>();

	public void useHandler() {
		mHandler = new Handler();
		mHandler.postDelayed(mRunnable, 1000);
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			
			mHandler.postDelayed(mRunnable, 1000);
		}
	};
	private ArrayList<String> mPuzFiles;
	private ArrayList<PuzzleClues> puzzleCluesList;

	@Override
	public void onResume() {
		super.onResume();
		Debug.print("MainView OnResume");
		
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
//		updateMainViewUI();
	}
	@Override
	public void onPause() {
		super.onPause();
	
		Debug.print("MainView OnPause");
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("puzzles", mPuzzles);
		Debug.print("onSavedInstance");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Debug.print("MainView OnDestroy");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh_btn:
			updateParseData();
			break;
		case R.id.add_buzzle_file_id:
		/*
		 * Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		 * intent.setType("file/*"); startActivityForResult(intent,1);
		 */

		((MainActivity) getActivity()).getPuzFiles();
		break;
		default:
			break;
		}
	}
	
	private void fetchLocalPuzzles(final boolean isClueUpdate) {
		((MainActivity)getActivity()).onLoadingStart("Load puzzles form local storage");
		
		ParseApi.getInstance().getLocalPuzzles(
				new DefaultApiCallback<List<Puzzle>>() {
					@Override
					public void onCompleted(List<Puzzle> result) {
						Debug.print("Local puzzles-->"+result.size());
						((MainActivity)getActivity()).onLoadingFinish();
						if (result != null && result.size() > 0) {
							updatePuzzlesList(result, isClueUpdate);
						}else{
							  updateMainViewUI();
						}
					}

					@Override
					public void onException(ParseException ex) {
						((MainActivity)getActivity()).onLoadingFinish();
//						fetchParsePuzzles(isClueUpdate);
					}

				});

	}

	private void fetchLocalClues() {
		((MainActivity)getActivity()).onLoadingStart("Fetch clues from local");
		ParseApi.getInstance().getLocalClues(
				new DefaultApiCallback<List<Clue>>() {
					@Override
					public void onCompleted(List<Clue> result) {
						((MainActivity)getActivity()).onLoadingFinish();
						if (result != null && result.size() > 0) {
							Debug.print("Local clues-->"+result.size());
						} else {
//							fetchClues();
						}
					}

					@Override
					public void onException(ParseException ex) {
//						fetchClues();
						((MainActivity)getActivity()).onLoadingFinish();
					}
				});
	}

	protected void fetchParsePuzzles(final boolean isClueUpdate) {
		Debug.print("Fetch Parse Puzzles--->"+isClueUpdate);
		((MainActivity)getActivity()).onLoadingStart("Fetch Puzzles from parse server");
		
		ParseApi.getInstance().getPuzzles(
				new DefaultApiCallback<List<Puzzle>>() {
					@Override
					public void onCompleted(List<Puzzle> result) {
						Debug.print("From parse------------->"+result);
						
						((MainActivity)getActivity()).onLoadingFinish();
						if (result != null && result.size() > 0) {
							updatePuzzlesList(result, isClueUpdate);
						} else {
							fetchLocalPuzzles(false);
						}
						
						
					}

					@Override
					public void onException(ParseException ex) {
						Debug.print("Exception:"+ex);
						((MainActivity)getActivity()).onLoadingFinish();
						fetchLocalPuzzles(false);
					}

				});

	}

	protected void fetchClues() {
		((MainActivity)getActivity()).onLoadingStart("Get clues from server");
		ParseApi.getInstance().getClues(null,
				new DefaultApiCallback<List<Clue>>() {
					@Override
					public void onCompleted(List<Clue> result) {
						if(result!=null&&result.size()>0){
							((MainActivity)getActivity()).onLoadingFinish();
						}else{
							fetchLocalClues();
						}
					}

					@Override
					public void onException(ParseException ex) {
						((MainActivity)getActivity()).onLoadingFinish();
						fetchLocalClues();
					}
				});
	}

	protected void updatePuzzlesList(List<Puzzle> result, boolean isClueUpdate) {
		Debug.print("updatePuzzlesList--->"+result.size()+":isClue-->"+isClueUpdate);
		 
		if (result != null && result.size() > 0) {
			Debug.print("Result===>" + result.size());
			if (mPuzzles == null) {
				mPuzzles = new ArrayList<Puzzle>();
			 
			}
			Debug.print("Result mPuzzle===>" + mPuzzles.size());
				for (Puzzle puzzle : result) {
					 
					
					if(isNewPuzzle(puzzle)){
						mPuzzles.add(puzzle);
					}
					
				}
				if(mPuzzles.size()==0){
					mPuzzles=new ArrayList<Puzzle>(result);
				}
				
				
			
			if (isClueUpdate) {
				Debug.print("Go to update clue list");
				updateClueList();
			}

		} else {
			Debug.print("No puzzles----------->");
			if (isClueUpdate) {
//				updateClueList();
			}
		}
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				
				
				updateMainViewUI();
				mAdapter.setList(mPuzzles);
				mAdapter.notifyDataSetChanged();
			}
		});

	}
	
	private boolean isNewPuzzle(Puzzle puzzle_){
		for(Puzzle puzzle:mPuzzles){
			if(puzzle_.getName().equals(puzzle.getName())){
				return false;
			}
		}
		return true;
	}
	

//	ArrayList<Puzzle> fetchCluePuzzles;
	ArrayList<Clue> clueList;

	private void updateClueList() {
		Debug.print("=======================Update CluesList =========================");
		((MainActivity) getActivity()).onLoadingStart("Upload clues to server");

		  clueList = new ArrayList<Clue>();
//		fetchCluePuzzles = new ArrayList<Puzzle>();
if(mPuzzles!=null){
		for (Puzzle puzzle_ : mPuzzles) {
			Debug.print("=====>"+puzzle_.getName());
			if (MainActivity.getPuzzleClues() != null) {
				PuzzleClues puzzleClue = MainActivity.getPuzzleClues().get(puzzle_.getTitle());
				Debug.print("----------------->"+MainActivity.getPuzzleClues().size());
				Debug.print("1----------------->"+puzzleClue);
				if (puzzleClue != null&& puzzleClue.getPuzzle() != null&& puzzleClue.getPuzzle().getTitle()
								.equals(puzzle_.getTitle())) {
					Debug.print("----------------->"+puzzleClue.getPuzzle());
					ParseQuery<Clue> query = ParseQuery.getQuery(Clue.class).fromPin();
					query.whereEqualTo(DatabaseQuery.PUZZLE_REF_ID,
							puzzle_.getObjectId());
					try {
						
						List<Clue> clues = query.find();
						
						if (clues != null && clues.size() > 0) {
							Debug.print("clues Size:"+clues.size());
							
						} else {
//							fetchCluePuzzles.add(puzzle_);
							for (Clue clue : puzzleClue.getClues()) {
								clue.setRefId(puzzle_.getObjectId());
								clue.setTitle(puzzle_.getTitle());
								clueList.add(clue);
							}
						}
					} catch (ParseException e) {

					}finally{
						puzzleClue=null;
					}
					
				}else{
					Debug.print("Else update clues List");
				}
			}
		}
}

		if (clueList.size() > 0) {
			
			((MainActivity) getActivity()).onLoadingFinish();
			Debug.print("Go to add Clues");
			((MainActivity) getActivity()).onLoadingStart("Upload clues to parse server");
			 ParseApi.getInstance().addClues(clueList,
					new ApiCallback<String>() {

						@Override
						public void onException(ParseException ex) {
							
							((MainActivity) getActivity()).onLoadingFinish();
//							fetchCluePuzzles = null;
						}

						@Override
						public void onCompleted(String result) {
							((MainActivity) getActivity()).onLoadingFinish();
							fetchOnlineClues();
						
						}
					}); 
		}else{
			clueList=null;
			((MainActivity) getActivity()).onLoadingFinish();
		}

	}

	protected void fetchOnlineClues() {
//		for (Puzzle puzzle : fetchCluePuzzles) {
//			ParseApi.getInstance().getClues(puzzle.getTitle(),
//					new DefaultApiCallback<List<Clue>>() {
//						@Override
//						public void onCompleted(List<Clue> result) {
//							((MainActivity) getActivity()).onLoadingFinish();
//						}
//
//						@Override
//						public void onException(ParseException ex) {
//							((MainActivity) getActivity()).onLoadingFinish();
//						}
//					});
//			((MainActivity) getActivity()).onLoadingFinish();
//			fetchCluePuzzles = null;
//		}
//		fetchCluePuzzles=null;
	}

	

	public void parseFile(String filePath) {

		mPuzFiles = new ArrayList<String>();
		if (new File(filePath).isFile()) {
			mPuzFiles.add(filePath);
		} else if (new File(filePath).isDirectory()) {
			listFilesAndFilesSubDirectories(filePath);
		}
		Debug.print("List of Files:" + mPuzFiles.size());
		if (mPuzFiles.size() > 0) {
			PuzParserTask task = new PuzParserTask(getActivity());
			task.setUpdateListener(this);
			task.execute(mPuzFiles.toArray(new String[mPuzFiles.size()]));
		} else {
			Toast.makeText(getActivity(), ".puz file not available ",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void parseFile(ArrayList<String> fileList) {

		mPuzFiles = new ArrayList<String>();
		for(String filePath:fileList){
		if (new File(filePath).isFile()&&filePath.contains(".puz")) {
			mPuzFiles.add(filePath);
		} else if (new File(filePath).isDirectory()) {
			listFilesAndFilesSubDirectories(filePath);
		}
		}
		Debug.print("List of Files:" + mPuzFiles.size());
		if (mPuzFiles.size() > 0) {
			PuzParserTask task = new PuzParserTask(getActivity());
			task.setUpdateListener(this);
			task.execute(mPuzFiles.toArray(new String[mPuzFiles.size()]));
		} else {
			Toast.makeText(getActivity(), ".puz file not available ",
					Toast.LENGTH_SHORT).show();
		}
	}
	

	/**
	 * List all files from a directory and its subdirectories
	 * 
	 * @param directoryName
	 *            to be listed
	 */
	public void listFilesAndFilesSubDirectories(String directoryName) {

		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile() && file.getAbsolutePath().contains(".puz")) {
				Debug.print(file.getAbsolutePath());
				mPuzFiles.add(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				listFilesAndFilesSubDirectories(file.getAbsolutePath());
			}
		}
	}

	@Override
	public void updateListAdapter(HashMap<String, PuzzleClues> pClues) {

		 
		Debug.print("Fetch Puzzles"+pClues.size());
		MainActivity.setPuzzleClues(pClues);
	 	puzzleCluesList = new ArrayList<PuzzleClues>();
		ArrayList<Puzzle> list = new ArrayList<Puzzle>();
		for (Map.Entry<String, PuzzleClues> puzzles : MainActivity.getPuzzleClues().entrySet()) {
			Debug.print(puzzles.getKey() + " :: " + puzzles.getValue());
			PuzzleClues puzzleClue = puzzles.getValue();
			Puzzle puzzle = puzzleClue.getPuzzle();
			list.add(puzzle);

		}
 

		Debug.print("list Size:"+list.size());
 
		manipulatePuzzles(list);
		list=null;
		updateMainViewUI();

	}

	public void manipulatePuzzlesandClues(ArrayList<Puzzle> list) {

		ParseApi.getInstance().getLocalPuzzles(
				new DefaultApiCallback<List<Puzzle>>() {
					@Override
					public void onCompleted(List<Puzzle> result) {
						manipulatePuzzles(result);

					}

					@Override
					public void onException(ParseException ex) {
					}

				});

	}

	ArrayList<Puzzle> puzzles;
	
	private boolean existPuzzles(Puzzle puzzle_){
		if (mPuzzles != null&&mPuzzles.size()>0) {
			for (Puzzle puzzle: mPuzzles) {
				Debug.print("Exist===============>"+puzzle_.getTitle()+":"+puzzle.getTitle());
				if (puzzle_.getTitle().equals(puzzle.getTitle())) {
					Debug.print("");
					 return true;
				}
			}
		} 
		
		return false;
	}
	

	protected void manipulatePuzzles(List<Puzzle> result) {
		puzzles = new ArrayList<Puzzle>();
		Debug.print("Manipulate Puzzles"+result.size());
		Debug.print("Main Activity HashMap:---->"+MainActivity.getPuzzleClues().size());
		try {
			if (result != null && result.size() > 0 ) {
				
				for (Puzzle puzzle : result) {
					Debug.print("----------->"+puzzle.getTitle());
					Debug.print("----------->"+mPuzzles);
					if(!existPuzzles(puzzle)){
						Debug.print("Non exist");
						puzzles.add(puzzle);
					}else{
						Debug.print("Already Exist");
					}
					
				}
			}else{
				Debug.print("Error manipulate puzzles-->");
			}
			
			/*else {
				for (Map.Entry<String, PuzzleClues> pClues : MainActivity
						.getPuzzleClues().entrySet()) {

					puzzles.add(pClues.getValue().getPuzzle());

				}
			}*/
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Debug.print("Add Puzzles------------------->" + puzzles.size());
		((MainActivity) getActivity()).onLoadingStart("Upload puzzles to parse server");
		if(puzzles.size()>0){
		ParseApi.getInstance().addPuzzle(puzzles, new ApiCallback<String>() {
			@Override
			public void onException(ParseException ex) {
				((MainActivity) getActivity()).onLoadingFinish();
				ParseApi.getInstance().puzzleSaveInLocal(puzzles);
				puzzles=null;
			}

			@Override
			public void onCompleted(String result) {
				((MainActivity) getActivity()).onLoadingFinish();
				Debug.print("updated---------------->");
				// MainActivity.pDialog.dismiss();
				// MainActivity.fetchPuzzles(puzzles);
				if(NetworkUtil.getConnectivityStatusString(getActivity())){
					((MainActivity) getActivity()).onLoadingStart("Get puzzles from server");
					ParseApi.getInstance().getPuzzles(new DefaultApiCallback<List<Puzzle>>() {
						@Override
						public void onCompleted(List<Puzzle> result) {
							((MainActivity) getActivity()).onLoadingFinish();
							Debug.print("Main Activity HashMap"+MainActivity.getPuzzleClues().size());
							updatePuzzlesList(result, true);
						}
						
						@Override
						public void onException(ParseException ex) {
							// TODO Auto-generated method stub
							((MainActivity) getActivity()).onLoadingFinish();
							updatePuzzlesList(puzzles, true);
							puzzles=null;
						}
						
					});
					
				}else{
					((MainActivity) getActivity()).onLoadingFinish();
					Toast.makeText(getActivity(), "Network not avilable", Toast.LENGTH_SHORT).show();
				}

				
			}
		});
		}else{
			((MainActivity) getActivity()).onLoadingFinish();
			updatePuzzlesList(puzzles, true);
		}

	}

	
	public void updateActionBar() {
		 
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
 
		((MainActivity)getActivity()).mMenuPlay.setVisible(true);
		((MainActivity)getActivity()).mMenuLogout.setVisible(true);
		getActivity().getActionBar().setTitle("Quiz");
	
	}
	
	private void updateMainViewUI(){
		Debug.print("update MainView--->");
		if(mPuzzles==null){
			mPuzzleListView.setVisibility(View.GONE);
			mInfoView.setVisibility(View.VISIBLE);
			if(NetworkUtil.getConnectivityStatusString(getActivity())){
				 mInfoTxt.setText("puzzles are not available");
			}else{
				 mInfoTxt.setText("Network connection not avialable");
			}
		}else{
			mPuzzleListView.setVisibility(mPuzzles.size()>0?View.VISIBLE:View.GONE);
			mInfoView.setVisibility(mPuzzles.size()>0?View.GONE:View.VISIBLE);
			mInfoTxt.setText("puzzles are not available");
		}
	}
  

}
