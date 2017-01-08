package com.pinterset.clienttask.pinterestclient.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
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
import com.pinterest.android.pdk.Utils;
import com.pinterset.clienttask.pinterestclient.R;
import com.pinterset.clienttask.pinterestclient.UserSessionManager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreatePinActivity extends ActionBarActivity {

    @Bind(R.id.pin_create_url)
    EditText imageUrl;

    @Bind(R.id.pin_create_link)
    EditText link;

    @Bind(R.id.pin_create_note)
    EditText note;

    @Bind(R.id.pin_response_view)
    TextView responseView;

    @Bind(R.id.create_pin_board_id)
    EditText boardId;

    @Bind(R.id.save_button)
    Button saveButton;

    @Bind(R.id.layout_log_out)
    LinearLayout logOut;

    @Bind(R.id.layout_all_board)
    LinearLayout all;

    // User Session Manager Class
    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
// Session class instance
        session = new UserSessionManager(getApplicationContext());
        ButterKnife.bind(this);
        imageUrl = (EditText) findViewById(R.id.pin_create_url);
        link = (EditText) findViewById(R.id.pin_create_link);
        note = (EditText) findViewById(R.id.pin_create_note);
        responseView = (TextView) findViewById(R.id.pin_response_view);
        boardId = (EditText) findViewById(R.id.create_pin_board_id);
        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSavePin();
            }
        });


        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatePinActivity.this , MyBoardsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();


            }
        });
    }


    private void  onLogout() {
        PDKClient.getInstance().logout();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        session.logoutUser();
        finish();
    }
    private void onSavePin() {
        String pinImageUrl = imageUrl.getText().toString();
        String board = boardId.getText().toString();
        String noteText = note.getText().toString();
        if (!Utils.isEmpty(noteText) &&!Utils.isEmpty(board) && !Utils.isEmpty(pinImageUrl)) {
            PDKClient
                    .getInstance().createPin(noteText, board, pinImageUrl, link.getText().toString(), new PDKCallback() {
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
            Toast.makeText(this, "Required fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
