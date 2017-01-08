package com.pinterset.clienttask.pinterestclient.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;
import com.pinterest.android.pdk.Utils;
import com.pinterset.clienttask.pinterestclient.ConnectionDetector;
import com.pinterset.clienttask.pinterestclient.R;
import com.pinterset.clienttask.pinterestclient.UserSessionManager;
import com.pinterset.clienttask.pinterestclient.fragmenet.RawResponseDialogFragment;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {


    // User Session Manager Class
    UserSessionManager session;
    @Bind(R.id.name_textview)
    TextView nameTv;

    @Bind(R.id.profile_imageview)
    ImageView profileIv;

    @Bind(R.id.pins_button)
    Button pinsButton;

    @Bind(R.id.boards_button)
    Button boardsButton;

    @Bind(R.id.layout_log_out)
    LinearLayout logOut;

    @Bind(R.id.layout_all_board)
    LinearLayout all;

    private static boolean DEBUG = true;
    private final String USER_FIELDS = "id,image,counts,created_at,first_name,last_name,bio";
    PDKUser user;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_board);
// Session class instance
        session = new UserSessionManager(getApplicationContext());

        ButterKnife.bind(this);

        pinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyPins();
            }
        });
        boardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyBoards();
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
                Intent intent = new Intent(HomeActivity.this , MyBoardsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {

            getMe();
        }else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(HomeActivity.this, "Connection",
                    "Please check your connection", false);
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
    private void setUser() {
        nameTv.setText(user.getFirstName() + " " + user.getLastName());
        Picasso.with(this).load(user.getImageUrl()).into(profileIv);
    }

    private void  getMe() {
        PDKClient.getInstance().getMe(USER_FIELDS, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                if (DEBUG) log(String.format("status: %d", response.getStatusCode()));
                user = response.getUser();
                setUser();
            }
            @Override
            public void onFailure(PDKException exception) {
                if (DEBUG)  log(exception.getDetailMessage());
                Toast.makeText(HomeActivity.this, "/me Request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void  onMyPins() {
        Intent i = new Intent(this, MyPinsActivity.class);
        startActivity(i);
    }

    private void  onMyBoards() {
        Intent i = new Intent(this, MyBoardsActivity.class);
        startActivity(i);
    }





    private void  onLogout() {
        PDKClient.getInstance().logout();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        session.logoutUser();
        finish();
    }

//

    private void log(String msg) {
        if (!Utils.isEmpty(msg))
            Log.d(getClass().getName(), msg);
    }
}
