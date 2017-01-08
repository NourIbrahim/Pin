package com.pinterset.clienttask.pinterestclient.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

import com.pinterset.clienttask.pinterestclient.ConnectionDetector;
import com.pinterset.clienttask.pinterestclient.R;
import com.pinterset.clienttask.pinterestclient.UserSessionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    @Bind(R.id.sign_up)
    TextView signUpBtn;

    @Bind(R.id.email)
    EditText email;

    @Bind(R.id.password)
    EditText password;

    @Bind(R.id.login)
    Button loginBtn;

    @Bind(R.id.fb)
    Button fb;



    String emailTxt , passwordTxt;


    // User Session Manager Class
    UserSessionManager session;
    //app id
    final String APP_ID ="4877677435383985571";

    //request
    PDKClient pdkClient = new PDKClient();

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if(session.isUserLoggedIn()){
//            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
//            startActivity(intent);
//            finish();
//
//        }
        ButterKnife.bind(this);


        // User Session Manager
        session = new UserSessionManager(getApplicationContext());


        intialize();
    }

    public void intialize() {

        signUpBtn.setPaintFlags(signUpBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginBtn.setOnClickListener(this);


        pdkClient = PDKClient.configureInstance(this, APP_ID);
        pdkClient.onConnect(this);
        pdkClient.setDebugMode(true);
        emailTxt    = email.getText().toString();
        passwordTxt = password.getText().toString();

    }

    private void onLogin() {
        List scopes = new ArrayList<>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);

        pdkClient.login(this, scopes, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), response.getData().toString());
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
                onLoginSuccess();
                Log.i("entered","yes");
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
                Log.i("entered","no");

            }
        });
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.login:



                cd = new ConnectionDetector(getApplicationContext());

                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                    // Get username, password from EditText
                    emailTxt= email.getText().toString();
                    passwordTxt= password.getText().toString();
                    session.createLoginSession(emailTxt,
                            passwordTxt);

                    onLogin();


                }else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(MainActivity.this, "Connection",
                            "Please check your connection", false);
                }
                break;
        }
    }


    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void onLoginSuccess() {
        Intent i = new Intent(this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);
    }
}


