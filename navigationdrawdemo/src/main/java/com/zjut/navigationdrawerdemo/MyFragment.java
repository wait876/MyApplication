package com.zjut.navigationdrawerdemo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MyFragment extends Fragment {
	private Context mContext;
	private static final String TAG="MyFragment";
	private Button button;
	public MyFragment() {
        // Empty constructor required for fragment subclasses
		mContext=getActivity();
		Log.i(TAG, "START");   
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.i(TAG, "onCreateView");   
        View rootView = inflater.inflate(R.layout.mylayout, container, false);
        //Button button=(Button) rootView.findViewById(R.id.button1);
       // button.setOnClickListener(this);
        //int i = getArguments().getInt(ARG_PLANET_NUMBER);
        //String planet = getResources().getStringArray(R.array.planets_array)[i];

        //int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()), "drawable", getActivity().getPackageName());
        //((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
        getActivity().setTitle("MyPage");
        //mContext=(Context) getArguments().get("context");
        //Log.i(TAG, getActivity().toString());
        return rootView;
    }

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onActivityCreated");   
		super.onActivityCreated(savedInstanceState);
		button=(Button) getView().findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
				//button.setText("s");
			}
		});
        
	}
	@Override 
	public void onAttach(Activity activity) {   
	super.onAttach(activity);   
	        Log.d(TAG, "onAttach");   
	    }   
	@Override 
	public void onCreate(Bundle savedInstanceState) {   
	super.onCreate(savedInstanceState);   
	        Log.d(TAG, "onCreate");   
	    }   
	
	@Override 
	public void onStart() {   
	super.onStart();   
	        Log.d(TAG, "onStart");   
	    }   
	@Override 
	public void onResume() {   
	super.onResume();   
	        Log.d(TAG, "onResume");   
	    }   
	@Override 
	public void onPause() {   
	super.onPause();   
	        Log.d(TAG, "onPause");   
	    }   
	@Override 
	public void onStop() {   
	super.onStop();   
	        Log.d(TAG, "onStop");   
	    }   
	@Override 
	public void onDestroyView() {   
	super.onDestroyView();   
	        Log.d(TAG, "onDestroyView");   
	    }   
	@Override 
	public void onDestroy() {   
	super.onDestroy();   
	        Log.d(TAG, "onDestroy");   
	    }   
	@Override 
	public void onDetach() {   
	super.onDetach();   
	        Log.d(TAG, "onDetach");   
	    
	}   
}
