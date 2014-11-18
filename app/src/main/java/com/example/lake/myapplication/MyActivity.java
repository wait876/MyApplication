package com.example.lake.myapplication;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Scanner;


public class MyActivity extends Activity implements View.OnClickListener {

    private final String TAG = "LJP";
    private Button button = null;
    private TextView textView = null;
    private Button intentButton = null;
    private Button preferenceButton = null;
    private Button contactsButton = null;
    private Button smsButton = null;
    private Cursor cursor = null;
    private Button actionBarButton=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_main);
        setContentView(R.layout.activity_my2);
        button = (Button) this.findViewById(R.id.button);
        intentButton = (Button) this.findViewById(R.id.button2);
        textView = (TextView) this.findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MyActivity.this, MyActivity2.class);
                startActivityForResult(intent, 111);
                Toast.makeText(MyActivity.this, "TEST", Toast.LENGTH_SHORT).show();

            }
        });
        intentButton.setOnClickListener(this);

        preferenceButton = (Button) this.findViewById(R.id.button3);
        preferenceButton.setOnClickListener(this);
        contactsButton = (Button) this.findViewById(R.id.button4);
        contactsButton.setOnClickListener(this);

        Uri allContacts = Uri.parse("content://contacts/people");
        CursorLoader cursorLoader = new CursorLoader(this, allContacts, null, null, null, null);
        cursor = cursorLoader.loadInBackground();
        smsButton = (Button) this.findViewById(R.id.button5);
        smsButton.setOnClickListener(this);

        actionBarButton=(Button)this.findViewById(R.id.button6);
        actionBarButton.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_1:
                showToast("Action1");
                break;
            case R.id.action_settings:
                showToast("action_settings");
                break;
        }
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111) {
            if (resultCode == 222) {
                if (null != data.getStringExtra("data"))
                    textView.setText(data.getStringExtra("data"));
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button2:
                Intent iii = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
                startActivity(iii);
                break;
            case R.id.button3:
                Intent ii = new Intent(this, com.example.lake.usingpreferences.AppPreferenceActivity.class);
                startActivity(ii);
                /*Scanner scanner=new Scanner(this.getResources().openRawResource(R.raw.dulinklist));
                while (scanner.hasNext())
                {
                    Log.i(TAG,scanner.next().toString());
                }*/
                break;
            case R.id.button4:
                if (cursor.moveToFirst()) {
                    do {
                        String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Log.i(TAG, contactID + "--" + name + "--");
                    }
                    while (cursor.moveToNext());
                }
                break;
            case R.id.button5:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra("address", "660691");
                i.putExtra("sms_body", "Hello");
                i.setType("vnd.android-dir/mms-sms");
                startActivity(i);
                break;

            case R.id.button6:
                if (getActionBar().isShowing())
                    getActionBar().hide();
                else
                    getActionBar().show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            Toast.makeText(this,"LANDSCAPE",Toast.LENGTH_SHORT).show();
        }
        else if (getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        {
            Toast.makeText(this,"PORTRAIT",Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG,"onResume");*/
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "ORIENTATION_LANDSCAPE");
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i(TAG, "ORIENTATION_PORTRAIT");
        }


    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        Toast.makeText(this, "系统的屏幕方向发生改变", Toast.LENGTH_LONG).show();
        int o=getRequestedOrientation();//获取手机的朝向
        switch (o) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                //editText.setText("当前屏幕朝向为： 横屏");
                Log.i(TAG,"横屏");
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                //editText.setText("当前屏幕朝向为： 竖屏");
                Log.i(TAG,"竖屏");
                break;
        }
        //不能省略，否则会报android.app.SuperNotCalledException: Activity OrientationActivity did not
        //call through to super.onConfigurationChanged()异常
        super.onConfigurationChanged(newConfig);

    }*/

    private void showToast(String string)
    {
        Toast.makeText(this, string,Toast.LENGTH_SHORT).show();
    }
}
