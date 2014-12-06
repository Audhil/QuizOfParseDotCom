package com.mns.quiz.fragment;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mns.quiz.Debug;
import com.mns.quiz.MainActivity;
import com.mns.quiz.R;
import com.mns.quiz.adapter.CluePagerAdapter;
import com.mns.quiz.adapter.CluePagerAdapter.onClueListener;
import com.mns.quiz.api.ParseApi;
import com.mns.quiz.api.ParseApi.DefaultApiCallback;
import com.mns.quiz.parse.module.Clue;
import com.mns.quiz.parser.preference.QuizPreference;
import com.mns.quiz.receiver.NetworkUtil;
import com.mns.quiz.view.PuzzleViewPager;
import com.mns.quiz.view.SoftKeyBoard;
import com.parse.ParseException;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class PlayFragment extends Fragment implements Serializable,onClueListener,OnClickListener{
	private static final long serialVersionUID = 8960606297540589953L;
	private PuzzleViewPager pager;
	private FragmentManager mFragmentManager;
	private CluePagerAdapter mAdapter;
	private ProgressDialog pDialog;
	private MainActivity mContext;
	private LayoutInflater mInflater;
	private SoftKeyBoard mCustomKeyBoard;
	private RelativeLayout mInfoView;
	private TextView mInfoTxt;
	private Button mRefresh;
	private static String puzzleId;

	public static PlayFragment newInstance() {
		PlayFragment fragmentFirst = new PlayFragment();
        Bundle args = new Bundle();
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    
    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        mContext=(MainActivity) getActivity();
        puzzleId = getArguments().getString(getResources().getString(R.string.puzzle_id));
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
    }
 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_player, container,false);
		KeyboardView keyboard=(KeyboardView) rootView.findViewById(R.id.keyboardview);
		mCustomKeyBoard = new SoftKeyBoard(getActivity(),keyboard, R.xml.popup);
		pager = (PuzzleViewPager) rootView.findViewById(R.id.myviewpager);
		mInfoView = (RelativeLayout) rootView.findViewById(R.id.info_view);
		mInfoTxt = (TextView) rootView.findViewById(R.id.info_msg_txt);
		mRefresh = (Button) rootView.findViewById(R.id.refresh_btn);
		mRefresh.setOnClickListener(this);
		mFragmentManager = getActivity().getSupportFragmentManager();
		mAdapter = new CluePagerAdapter(getActivity(),getChildFragmentManager());
		mInfoView.setVisibility(View.GONE);
		updateParseData();

		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		return rootView;
	}
	
	private void updateParseData(){
		 fetchCluesList(puzzleId);
		   /*getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
					updateAdater(null);
				
				
			}
		});*/
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public void fetchCluesList(final String puzzleId){
		 Debug.print("switchToPlayScreeen"+puzzleId);
		 ((MainActivity)getActivity()).onLoadingStart("Fetch clues please wait");
		 ParseApi.getInstance().getLocalClues(new DefaultApiCallback<List<Clue>>() {
				@Override
				public void onCompleted(List<Clue> result) {
					
					((MainActivity)getActivity()).onLoadingFinish();
					if(result!=null&&result.size()>0){
						 
						updateCluesList(result);
					}
					else{
						getCluesFromServer(puzzleId);
					}
				}
				@Override
				public void onException(ParseException ex) {
					
					((MainActivity)getActivity()).onLoadingFinish();
					updateAdater(null);
				}
				
			}) ;
		 
	/* if(puzzleId==null){
		 Debug.print("fetch all data"+puzzleId);
	 } */
	 
	
	}
	
	private   void getCluesFromServer(String  puzzleId){
		((MainActivity)getActivity()).onLoadingStart("Fetch Clues from parse server");
		if(puzzleId!=null){
		ParseApi.getInstance().getClues(puzzleId,new DefaultApiCallback<List<Clue>>() {
			@Override
			public void onCompleted(List<Clue> result) {
				((MainActivity)getActivity()).onLoadingFinish();
					updateCluesList(result);
			}
			
			@Override
			public void onException(ParseException ex) {
				((MainActivity)getActivity()).onLoadingFinish();
			}
		});
		}else{
			ParseApi.getInstance().getParseClues(new DefaultApiCallback<List<Clue>>() {
				@Override
				public void onCompleted(List<Clue> result) {
					((MainActivity)getActivity()).onLoadingFinish();
						updateCluesList(result);
				}
				
				@Override
				public void onException(ParseException ex) {
					((MainActivity)getActivity()).onLoadingFinish();
				}
			});
		}
		
	}
	
	
	private  void updateCluesList(List<Clue> cluesList){
		((MainActivity)getActivity()).onLoadingStart("Update UI");		
		 if(cluesList!=null && cluesList.size()>0){
			 Debug.print("Size:"+cluesList.size());
			 Collections.shuffle(cluesList);
			int i=0;
			 for(;i<cluesList.size();i++){
				 if(!(cluesList.get(i).getAnswer().trim()).equals(cluesList.get(i).getUserSolution().trim())){
					 break;
				 }
			 }
			 
			 Debug.print("item:"+i);
			 QuizPreference.getInstance().setSwipePositionPrefence(i);
//			 	 Debug.print("item:"+i);
			 ((MainActivity)getActivity()).onLoadingFinish();
			 
		 }else{
			 ((MainActivity)getActivity()).onLoadingFinish();
		 }
		 updateAdater(cluesList);
	}
    
	@SuppressWarnings("unused")
	public void updateAdater(List<Clue> cluesList) {
		 if(mAdapter!=null){
		
		pager.setOnPageChangeListener(mAdapter);
		mAdapter.setConfig(mCustomKeyBoard, pager);
		pager.setAdapter(mAdapter);
		mAdapter.setList(cluesList);
		Debug.print("update NotifiData Manager--->");
		mAdapter.notifyDataSetChanged();
		pager.setCurrentItem(0);
		pager.setOffscreenPageLimit(2);
		pager.setPageMargin(0);
		pager.setPagingEnabled(true);
		 }
		if(cluesList==null){
			Debug.print("--> update null");
			pager.setVisibility(View.GONE);
			mInfoView.setVisibility(View.VISIBLE);
			if(NetworkUtil.getConnectivityStatusString(getActivity())){
			 mInfoTxt.setText("Clues are not available");
			}else{
				mInfoTxt.setText("Network connection not avialable");
			}
		}else{
			Debug.print("--> update not null:"+cluesList.size());
			pager.setVisibility(cluesList.size()>0?View.VISIBLE:View.GONE);
			mInfoView.setVisibility(cluesList.size()>0?View.GONE:View.VISIBLE);
		}
		 
		cluesList=null;
		
		((MainActivity)getActivity()).onLoadingFinish();
//		 new setAdapterTask().execute();
	}


	@Override
	public void updateAnswer() {
		// TODO Auto-generated method stub
		
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void updateActionBar() {
		 
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
 
		mContext.mMenuPlay.setVisible(false);
		mContext.mMenuLogout.setVisible(false);
		getActivity().getActionBar().setTitle("Quiz");
	
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh_btn:
			updateParseData();
			break;
		 
		default:
			break;
		}
	}


}
