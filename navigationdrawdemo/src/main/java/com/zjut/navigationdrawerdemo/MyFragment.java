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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MyFragment extends Fragment implements OnClickListener {
	private Context mContext;
	private static final String TAG="MyFragment";
	private Button button;
    private String destPath=null;

    private TextView tvCount=null;
    private EditText etId=null;
    private EditText etAge=null;
    private EditText etName=null;
    private Button btSave=null;
    private Button btFind=null;
    private TextView tvResult=null;
    private Button btShow=null;


    private int id;
    private int age;
    private String name;
    private PersonService personService=null;
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
        tvCount=(TextView)getActivity().findViewById(R.id.tv_count);
        btSave=(Button)getActivity().findViewById(R.id.bt_save);
        btSave.setOnClickListener(this);
        etId=(EditText)getActivity().findViewById(R.id.et_id);
        etAge=(EditText)getActivity().findViewById(R.id.et_age);
        etName=(EditText)getActivity().findViewById(R.id.et_name);
        btFind=(Button)getActivity().findViewById(R.id.bt_find);
        tvResult=(TextView)getActivity().findViewById(R.id.textView1);
        btFind.setOnClickListener(this);
        button.setOnClickListener(this);
        btShow=(Button)getActivity().findViewById(R.id.bt_show);
        btShow.setOnClickListener(this);
        destPath="/data/data/"+getActivity().getPackageName()+"/databases";
        File f=new File(destPath);
        if (!f.exists())
        {
            f.mkdir();

            try {
                CopyDB(getActivity().getResources().openRawResource(R.raw.se),new FileOutputStream(destPath+"/db"));
                Toast.makeText(getActivity(),"copy done",Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else
        {
            Toast.makeText(getActivity(),"exist",Toast.LENGTH_SHORT).show();
            PersonService personService=new PersonService(getActivity());
            tvCount.setText("counts:"+String.valueOf(personService.getCount()));
            etId.setText(String.valueOf(personService.getMaxID()));


        }

        
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

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button1:
                File[] files=getActivity().getFilesDir().getParentFile().listFiles();
                StringBuilder stringBuilder=new StringBuilder();
                for(File file:files)
                {
                    stringBuilder.append(file.getAbsolutePath()+"\r\n");
                }
                Toast.makeText(getActivity(), stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                Log.i(TAG,stringBuilder.toString());
                break;
            case R.id.bt_save:
                id=Integer.valueOf(etId.getText().toString());
                age=Integer.valueOf(etAge.getText().toString());
                name=etName.getText().toString();
                Person p=new Person(id,name,age);
                personService=new PersonService(getActivity());
                personService.save(p);
                clearUI(personService.getMaxID());
                Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_find:
                id=Integer.valueOf(etId.getText().toString());
                personService=new PersonService(getActivity());
                Person person=personService.find(id);
                if (null==person)
                {
                    Toast.makeText(getActivity(), "NULL", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    etAge.setText(String.valueOf(person.getAge()));
                    etName.setText(person.getName());
                }

                break;
            case R.id.bt_show:
                personService=new PersonService(getActivity());
                List<Person> persons=personService.getSScrollData(0,20);
                StringBuilder sb=new StringBuilder();
                for(Person person1:persons)
                {
                    sb.append(person1.getName()).append(" . ");
                }
                tvResult.setText(sb.toString().trim());
                break;
        }

    }

    public void CopyDB(InputStream inputStream,OutputStream outputStream)
    {
        byte[] buffer=new byte[1024];
        int length;

        try {
            while((length=inputStream.read(buffer))>0)
            {
                outputStream.write(buffer,0,length);
            }
            inputStream.close();

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void clearUI(long id)
    {
        etName.setText("");
        etAge.setText("");
        etId.setText(String.valueOf(id));
    }
}
