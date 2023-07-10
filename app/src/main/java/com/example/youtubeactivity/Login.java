package com.example.youtubeactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;




public class Login extends AppCompatActivity {

    private EditText editTextLoginEmail,editTextLoginPwd;
    CheckBox rememberMe ;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    //private FirebaseAuth mAuth;
    //SessionManager sessionManager;
    private static final String TAG = "LoginActivity";
    TextView register ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        this.getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView (R.layout.activity_login);
        getSupportActionBar ().hide ();
        //getSupportActionBar().setTitle("Login");
        editTextLoginEmail = findViewById(R.id.loginEmail);
        editTextLoginPwd = findViewById(R.id.loginPass);
        progressBar = findViewById(R.id.progressbBar);
        register = findViewById(R.id.register) ;
        rememberMe = findViewById(R.id.remember_me) ;
        authProfile = FirebaseAuth.getInstance();
        //login user
        Button buttonLogin = findViewById(R.id.submitButton);

        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedin", false);
        // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        if(isLoggedIn){
            //Toast.makeText(this, "Already exists !!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this,MainActivity.class));
        }

/*        sessionManager = new SessionManager(Login.this, SessionManager.REMEMBER_ME_SESSION);
        if(sessionManager.checkRememberMe()){
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailsFromSession();
            editTextLoginEmail.setText(rememberMeDetails.get(SessionManager.KEY_REMEMBER_ME_EMAIL));
            editTextLoginPwd.setText(rememberMeDetails.get(SessionManager.KEY_REMEMBER_ME_PASSWORD));
        }*/


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = editTextLoginEmail.getText().toString();
                String userPass = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Toast.makeText(Login.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is not valid");
                    editTextLoginEmail.requestFocus();
                } else if(TextUtils.isEmpty(userPass)) {
                    Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password is required");
                    editTextLoginPwd.requestFocus();
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    if (rememberMe.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedin", true);
                        editor.commit();
                        //Toast.makeText(Login_Page.this, "Checked !!!!!!11" , Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedin",false);
                        editor.commit();
                        //Toast.makeText(Login_Page.this, "NOT    Checked !!!!!!11" , Toast.LENGTH_SHORT).show();
                    }
                    loginUser(userEmail,userPass);
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,SignUp.class));
            }
        });
    }
    private void loginUser(String userEmail, String userPass) {

        if (!isConnected(this)) {
            showNoInternetDialog();
        }
        authProfile.signInWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance() ;
                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        Toast.makeText(Login.this, "You are logged in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(Login.this,"You have not verified your Gmail ID \n Verify Your Gmail ID to Login" , Toast.LENGTH_SHORT).show();
                    }
                } else{
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        editTextLoginEmail.setError("User does not exists");
                        editTextLoginEmail.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(Login.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private boolean isConnected(Login login_page) {

        ConnectivityManager connectivityManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            connectivityManager = (ConnectivityManager) login_page.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        return (wifiInfo != null && wifiInfo.isConnected()) || mobileInfo != null && mobileInfo.isConnected();
    }


    private void showNoInternetDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
        dialog.setMessage("Please connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

/*    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null && user.isEmailVerified()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            this.finish();
        }
    }*/
}
