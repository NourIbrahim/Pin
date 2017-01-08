package com.pinterset.clienttask.pinterestclient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pinterest.android.pdk.PDKBoard;
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

public class MyBoardsActivity extends ActionBarActivity {

    @Bind(R.id.listView)
    ListView _listView;

    @Bind(R.id.layout_create)
    LinearLayout create;

    @Bind(R.id.layout_all_board)
    LinearLayout all;

    @Bind(R.id.layout_log_out)
    LinearLayout logOut;


    // User Session Manager Class
    UserSessionManager session;
    private PDKCallback myBoardsCallback;
    private PDKResponse myBoardsResponse;
    private BoardsAdapter _boardsAdapter;
    private boolean _loading = false;
    private static final String BOARD_FIELDS = "id,name,description,creator,image,counts,created_at";


    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_boards);
        ButterKnife.bind(this);
        // Session class instance
        session = new UserSessionManager(getApplicationContext());
        _boardsAdapter = new BoardsAdapter(this);

        _listView.setAdapter(_boardsAdapter);
        _listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu_boards, menu);
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionDetector(getApplicationContext());

                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                    createNewBoard();

                }else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(MyBoardsActivity.this, "Connection",
                            "Please check your connection", false);
                }
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchBoards();
            }
        });
        myBoardsCallback = new PDKCallback() {



            @Override
            public void onSuccess(PDKResponse response) {
                _loading = false;
                myBoardsResponse = response;
                _boardsAdapter.setBoardList(response.getBoardList());
            }

            @Override
            public void onFailure(PDKException exception) {
                _loading = false;
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        };
        _loading = true;


    }

    private void fetchBoards() {
        _boardsAdapter.setBoardList(null);
        PDKClient.getInstance().getMyBoards(BOARD_FIELDS, myBoardsCallback);
    }

    private void loadNext() {
        if (!_loading && myBoardsResponse.hasNext()) {
            _loading = true;
            myBoardsResponse.loadNext(myBoardsCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
        fetchBoards();
        }else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(MyBoardsActivity.this, "Connection",
                    "Please check your connection", false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_board_delete:

                cd = new ConnectionDetector(getApplicationContext());

                // get Internet status
                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                deleteBoard(info.position);

                }else {
                    // Internet connection is not present
                    // Ask user to connect to Internet
                    showAlertDialog(MyBoardsActivity.this, "Connection",
                            "Please check your connection", false);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }




    private void  onLogout() {
        PDKClient.getInstance().logout();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        session.logoutUser();
        finish();
    }
    private void createNewBoard() {
        Intent i = new Intent(this, CreateBoardActivity.class);
        startActivity(i);
    }

    private void deleteBoard(int position) {
        PDKClient.getInstance().deleteBoard(_boardsAdapter.getBoardList().get(position).getUid(), new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), "Response: " + response.getStatusCode());
                fetchBoards();
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), "error: " + exception.getDetailMessage());
            }
        });
    }

    private class BoardsAdapter extends BaseAdapter {

        private List<PDKBoard> _boardList;
        private Context _context;
        public BoardsAdapter(Context c) {
            _context = c;
        }

        public void setBoardList(List<PDKBoard> list) {
            if (_boardList == null) _boardList = new ArrayList<PDKBoard>();
            if (list == null) _boardList.clear();
            else _boardList.addAll(list);
            notifyDataSetChanged();
        }

        public List<PDKBoard> getBoardList() {
            return _boardList;
        }
        @Override
        public int getCount() {
            return _boardList == null ? 0 : _boardList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolder;

            //load more pins if about to reach end of list
            if (_boardList.size() - position < 5) {
                loadNext();
            }

            if (convertView == null){
                LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

                viewHolder = new ViewHolderItem();
                viewHolder.textViewItem = (TextView) convertView.findViewById(android.R.id.text1);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolderItem) convertView.getTag();
            }

            PDKBoard boardItem = _boardList.get(position);
            if (boardItem != null) {
                viewHolder.textViewItem.setText(boardItem.getName());
            }

            return convertView;
        }

        private class ViewHolderItem {
            TextView textViewItem;
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

}
