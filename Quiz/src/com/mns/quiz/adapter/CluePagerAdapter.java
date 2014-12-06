package com.mns.quiz.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewConfiguration;

import com.mns.quiz.Debug;
import com.mns.quiz.api.ParseApi;
import com.mns.quiz.listener.ConfigPagerListener;
import com.mns.quiz.parse.module.Clue;
import com.mns.quiz.parser.preference.QuizPreference;
import com.mns.quiz.uitity.Constants;
import com.mns.quiz.view.PuzzleViewPager;
import com.mns.quiz.view.SoftKeyBoard;

public class CluePagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener,ConfigPagerListener,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2845823928872696544L;
	private Activity context;
	private ArrayList<Clue> mCluesList = new ArrayList<com.mns.quiz.parse.module.Clue>();
	private com.mns.quiz.view.SoftKeyBoard mCustomKeyboard;
	private int mCurrentPos=0;
	private PuzzleViewPager mViewPager;
	private int mLimitPostion=0;
	
	 

	public CluePagerAdapter(Activity activity, FragmentManager fm) {
		super(fm);
		this.context = activity;
		try {
			fm.executePendingTransactions();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public Fragment getItem(int position) {
		ClueFragment fragment = (ClueFragment) new ClueFragment();
		Bundle args = new Bundle();
		args.putParcelable(Constants.CLUE, mCluesList.get(position));
		args.putInt(Constants.POSITION,position);
		fragment.setArguments(args);
		fragment.setKeyboard(mCustomKeyboard);
		fragment.setListener(this);
		return fragment;
	}

	@Override
	public int getCount() {
		return mCluesList.size();
	}

	@Override
	public void onPageSelected(int position) {
		Debug.print(QuizPreference.getInstance().getSwipePositionPrefence()+":onPageSelected:"+position);
		
		if(QuizPreference.getInstance().getSwipePositionPrefence()<position){
			mCurrentPos=QuizPreference.getInstance().getSwipePositionPrefence();
			Debug.print("===============>"+mCurrentPos);
			mHandler.post(mRun);
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		 mCurrentState = state;
         
	}

	public void setList(List<Clue> cluesList) {
		if(cluesList!=null){
			mCluesList = new ArrayList<Clue>(cluesList.size());
			mCluesList.addAll(cluesList);
		}
		else
			mCluesList=new ArrayList<Clue>();
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	public interface onClueListener {

		public void updateAnswer();

	}

	public void setConfig(SoftKeyBoard customKeyBoard,PuzzleViewPager pager) {
		mCustomKeyboard = customKeyBoard;
		mViewPager=pager;
	}

	@Override
	public void swipePagermode(int item, Clue move) {
		Debug.print("swipemode");

		UpdateSwipePreference();
		int old = QuizPreference.getInstance().getSwipePositionPrefence();

		int dif = QuizPreference.getInstance().getSwipePositionPrefence()- item;
		if (dif == 1) {
			mCurrentPos = item + 1;
			Debug.print("update" + mCurrentPos);
			mHandler.post(mRun);
		} else {

		}
		
	}
	
	private void UpdateSwipePreference() {
		try{
			for (int pos = QuizPreference.getInstance().getSwipePositionPrefence(); pos < mCluesList.size(); pos++) {
				Clue clue = mCluesList.get(pos);
				if (clue != null) {
					clue=ParseApi.getInstance().getClue(clue.getObjectId());
					if (!clue.getAnswer().equals(clue.getUserSolution())){
						QuizPreference.getInstance().setSwipePositionPrefence(pos);
						break;
					}
				}
				
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	 
	private int mCurrentState = -1;
	private static int INTERVAL = ViewConfiguration.getLongPressTimeout();
	private Handler mHandler = new Handler();
	private Runnable mRun = new Runnable() {

	    @Override
	    public void run() {
	    mViewPager.setCurrentItem(mCurrentPos);
	            mHandler.removeCallbacks(mRun);// or always set it to run
	    }
	};
	
	
	public int getUserEnd(){
		int i=0;
		for (; i < mCluesList.size(); i++) {
//			Debug.print(""+i);
			Clue clue=ParseApi.getInstance().getClue(mCluesList.get(i).getObjectId());
			if (!clue.getAnswer().equals(clue.getUserSolution())) {
				break;
			}  
		}
		return i;
		
	}
	
	

	 

}
