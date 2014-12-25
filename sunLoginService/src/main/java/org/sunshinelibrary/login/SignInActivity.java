package org.sunshinelibrary.login;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 13-7-30
 * Time: 下午3:34
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.sunshinelibrary.login.SunLoginService.LocalBinder;

import java.util.Timer;
import java.util.TimerTask;


public class SignInActivity extends FragmentActivity implements CanclableObserver,AdapterView.OnItemSelectedListener, View.OnClickListener{

    private SunLoginService mService;
    private boolean mBound = false;
    private ProgressDialog checkLoginDialog;


    private RelativeLayout accountTypeSeletor,studentsOnlyInput;
    private ImageButton btnAccountTeacher,btnAccountStudent;
    private LinearLayout signInForm;
    private Spinner spGrade,spClass;
    private EditText etName,etApiServerAddress;
    private ImageButton btnSignIn,easterEgg;

    private String userType = null;
    private String userClass = null;
    private String userGrade = null;
    private String[] gradeStrings,classStrings;

    private int count;
    private long firstTime;
    private Timer delayTimer;
    private Handler handler;
    private TimerTask task;
    private long interval = 300;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;


    private String apiAddressNow;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        initialUI();
        initComponents();
    }

    @Override
    protected void onResume(){
        super.onResume();
        signInForm.setVisibility(View.GONE);
        count = 0;
        firstTime = 0;

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (count == 2) {
                    if(alertDialog==null){
                        alertDialog = alertDialogBuilder.show();
                    }else {
                        alertDialog.show();
                    }
                } else if (count == 3) {
                    Toast.makeText(SignInActivity.this,"Bazinga!!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(SignInActivity.this,Sunshine.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SignInActivity.this.startActivity(intent);
                }
                delayTimer.cancel();
                count = 0;
                super.handleMessage(msg);
            }
        };
    }

    private void initialUI(){
        accountTypeSeletor = (RelativeLayout)findViewById(R.id.account_type_selector);
        btnAccountTeacher = (ImageButton)findViewById(R.id.signin_account_teacher);
        btnAccountStudent = (ImageButton)findViewById(R.id.signin_account_student);
        easterEgg = (ImageButton)findViewById(R.id.easter_egg);

        signInForm = (LinearLayout)findViewById(R.id.signin_form);
        spGrade = (Spinner)signInForm.findViewById(R.id.signin_user_grade);
        spClass = (Spinner)signInForm.findViewById(R.id.signin_user_class);
        etName = (EditText)signInForm.findViewById(R.id.signin_user_name);
        btnSignIn = (ImageButton)signInForm.findViewById(R.id.signin_btn);
        studentsOnlyInput = (RelativeLayout)signInForm.findViewById(R.id.students_only);
    }

    public void initComponents(){
        //Dialog initial
        checkLoginDialog = new ProgressDialog(this);
        checkLoginDialog.setMessage("正在检查登陆状态，请稍候...");
        checkLoginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        checkLoginDialog.setProgress(0);
        checkLoginDialog.setCancelable(false);
        checkLoginDialog.show();

        // grade selection
        gradeStrings = getResources().getStringArray(R.array.grade_array);
        spGrade.setAdapter(getAdapterForStrings(gradeStrings));
        spGrade.setOnItemSelectedListener(this);

        // class selection
        classStrings = getResources().getStringArray(R.array.class_array);
        spClass.setAdapter(getAdapterForStrings(classStrings));
        spClass.setOnItemSelectedListener(this);

        btnAccountTeacher.setOnClickListener(this);
        btnAccountStudent.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);

        easterEgg.setOnClickListener(this);

        LayoutInflater layoutInflater = LayoutInflater.from(SignInActivity.this);
        View dialogView = layoutInflater.inflate(R.layout.dialog, null);
        etApiServerAddress = (EditText)dialogView.findViewById(R.id.api_server_address);
        try{
            SharedPreferences preferences = SignInActivity.this.getSharedPreferences("API_SERVER_ADDRESS",MODE_WORLD_READABLE);
            apiAddressNow = preferences.getString("api_server_address","192.168.3.100");
        }catch (Exception e){
            e.printStackTrace();
        }
        etApiServerAddress.setText(apiAddressNow);
        alertDialogBuilder = new AlertDialog.Builder(SignInActivity.this);
        alertDialogBuilder.setView(dialogView)
                .setTitle("设置API服务器地址")
                .setIcon(R.drawable.icon)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences preferences = SignInActivity.this.getSharedPreferences("API_SERVER_ADDRESS",MODE_WORLD_READABLE);
                        SharedPreferences.Editor editor = preferences.edit();
                        if(!etApiServerAddress.getText().toString().equals("")){
                            editor.putString("api_server_address",etApiServerAddress.getText().toString());
                            editor.commit();
                        }else{
                            editor.putString("api_server_address",SignInActivity.this.getResources().getString(R.string.default_api_server_address));
                            editor.commit();
                            dialogInterface.dismiss();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SunLoginService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setCurrentActivity(SignInActivity.this,SignInActivity.this);
            mService.doCheckSignIn();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void dismissDialog(String situation){
        checkLoginDialog.cancel();

        if(situation.equals("success")){
            Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show();
            this.finish();
        }else if(situation.equals("failure_oncheck")){
            this.finish();
        }else if(situation.equals("failure_onlogin")){

        }else if(situation.equals("failed_school")){
            Toast.makeText(this,"无法获取学校信息，请稍后重试",Toast.LENGTH_SHORT).show();
        }
        else if(situation.equals("no network")){
            Toast.makeText(this,"无网络连接，请稍后重试",Toast.LENGTH_SHORT).show();
            this.finish();
        }else{
            Toast.makeText(this,"未知错误，请重试",Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    @Override
    public void displayLoginWindow(){
        checkLoginDialog.cancel();
        accountTypeSeletor.setVisibility(View.VISIBLE);
        //mService.doLoadSchool();
    }

    private ArrayAdapter<String> getAdapterForStrings(String[] strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    @Override
    public void onClick(View v) {
        if (v == btnAccountStudent) {
            userType = "student";
            accountTypeSeletor.setVisibility(View.GONE);
            signInForm.setVisibility(View.VISIBLE);
            studentsOnlyInput.setVisibility(View.VISIBLE);
        } else if (v == btnAccountTeacher) {
            userType = "teacher";
            accountTypeSeletor.setVisibility(View.GONE);
            signInForm.setVisibility(View.VISIBLE);
            studentsOnlyInput.setVisibility(View.GONE);
        } else if (v == btnSignIn) {
            if(!etName.getText().toString().equals("")){
                String[]info =new String[]{userType,userGrade,userClass,etName.getText().toString()};
                mService.doSignIn(info);
                checkLoginDialog.show();
            }else{
                Toast.makeText(SignInActivity.this,"请输入名字",Toast.LENGTH_LONG).show();
            }
        }else if(v == easterEgg){
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime <= interval) {
                ++count;
            }
            delay();
            firstTime = secondTime;
        }
    }

    // to determine if user has stopped clicking
    private void delay() {
        if (task != null)
            task.cancel();

        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                handler.sendMessage(message);
            }
        };
        delayTimer = new Timer();
        delayTimer.schedule(task, interval);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spGrade) {
            userGrade = String.valueOf(position+1);
        }else if(parent == spClass){
            userClass = String.valueOf(position+1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if(signInForm.getVisibility()==View.VISIBLE){
                signInForm.setVisibility(View.GONE);
                accountTypeSeletor.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}