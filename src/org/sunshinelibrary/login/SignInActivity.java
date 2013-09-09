package org.sunshinelibrary.login;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 13-7-30
 * Time: 下午3:34
 */
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.content.Intent;
import android.os.IBinder;
import android.content.Context;
import android.content.ServiceConnection;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import org.sunshinelibrary.login.SunLoginService.LocalBinder;
import android.content.ComponentName;


public class SignInActivity extends FragmentActivity implements CanclableObserver,AdapterView.OnItemSelectedListener, View.OnClickListener{

    private SunLoginService mService;
    private boolean mBound = false;
    private ProgressDialog checkLoginDialog;


    private RelativeLayout accountTypeSeletor,studentsOnlyInput;
    private ImageButton btnAccountTeacher,btnAccountStudent;
    private LinearLayout signInForm;
    private Spinner spGrade,spClass;
    private EditText etName;
    private ImageButton btnSignIn;

    private String userType = null;
    private String userClass = null;
    private String userGrade = null;

    private String[] gradeStrings,classStrings;

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
    }

    private void initialUI(){
        accountTypeSeletor = (RelativeLayout)findViewById(R.id.account_type_selector);
        btnAccountTeacher = (ImageButton)findViewById(R.id.signin_account_teacher);
        btnAccountStudent = (ImageButton)findViewById(R.id.signin_account_student);

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

        //
        btnAccountTeacher.setOnClickListener(this);
        btnAccountStudent.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
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
        }else if(situation.equals("failure")){

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
        }
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