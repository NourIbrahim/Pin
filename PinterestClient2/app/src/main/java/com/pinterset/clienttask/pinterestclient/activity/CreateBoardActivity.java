package com.pinterset.clienttask.pinterestclient.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
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
import com.pinterest.android.pdk.Utils;
import com.pinterset.clienttask.pinterestclient.ConnectionDetector;
import com.pinterset.clienttask.pinterestclient.R;
import com.pinterset.clienttask.pinterestclient.UserSessionManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateBoardActivity extends ActionBarActivity {

    // User Session Manager Class
    UserSessionManager session;


    @Bind(R.id.board_create_name)
    EditText boardName;

    @Bind(R.id.board_create_desc)
    EditText boardDesc;

    @Bind(R.id.board_response_view)
    TextView responseView;

    @Bind(R.id.save_button)
    Button saveButton;

    @Bind(R.id.layout_log_out)
    LinearLayout logOut;

    @Bind(R.id.layout_all_board)
    LinearLayout all;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
        ButterKnife.bind(this);
// Session class instance
        session = new UserSessionManager(getApplicationContext());
        boardName = (EditText) findViewById(R.id.board_create_name);
        boardDesc = (EditText) findViewById(R.id.board_create_desc);
        responseView = (TextView) findViewById(R.id.board_response_view);
        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveBoard();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateBoardActivity.this , MyBoardsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }



    private void onSaveBoard() {

        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            String bName = boardName.getText().toString();
            if (!Utils.isEmpty(bName)) {
                PDKClient.getInstance().createBoard(bName, boardDesc.getText().toString(), new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        Log.d(getClass().getName(), response.getData().toString());
                        responseView.setText(response.getData().toString());

                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Log.e(getClass().getName(), exception.getDetailMessage());
                        responseView.setText(exception.getDetailMessage());
                    }
                });
            } else {
                Toast.makeText(this, "Board name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(CreateBoardActivity.this, "Connection",
                    "Please check your connection", false);
        }
    }


    private void  onLogout() {
        PDKClient.getInstance().logout();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        session.logoutUser();
        finish();
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
}
