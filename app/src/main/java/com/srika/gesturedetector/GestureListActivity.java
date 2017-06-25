package com.srika.gesturedetector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class GestureListActivity extends AppCompatActivity {
    private static final String TAG = "GestureListActivity";
    private String mCurrentGestureName, mNewGestureName;
    private ListView mGestureListView;
    private static ArrayList<GestureHolder> mGestureList;
    private GestureListAdapter mGestureAdapter;
    private GestureLibrary gLib;
    //private ImageView mMenuItemView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestures_list);
        Log.d(TAG, getApplicationInfo().dataDir);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mGestureListView = (ListView) findViewById((R.id.gestures_list));
        fetchListOfGestures();
        mGestureAdapter = new GestureListAdapter(mGestureList, GestureListActivity.this);
        mGestureListView.setLongClickable(true);
        mGestureListView.setAdapter(mGestureAdapter);

        // displays the popup context top_menu to either delete or resend measurement
        registerForContextMenu(mGestureListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        fetchListOfGestures();
        mGestureAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gestures_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gesture_add_menu:
                addGesture();
                break;
            case R.id.gesture_test_menu:
                testGesture();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchListOfGestures() {
        try {
            if (mGestureList == null) {
                mGestureList = new ArrayList<>();
            }
            mGestureList.clear();
            gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
            gLib.load();
            Set<String> gestureSet = gLib.getGestureEntries();
            for(String gestureName: gestureSet){
                ArrayList<Gesture> list = gLib.getGestures(gestureName);
                for(Gesture g : list) {
                    mGestureList.add(new GestureHolder(g, gestureName));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populateMenu(View view){
        //ImageView idView = (ImageView) view.findViewById(R.id.gesture_id);
        //Log.d(TAG, "ha ha" + idView.getText().toString());
        LinearLayout vwParentRow = (LinearLayout)view.getParent().getParent();
        TextView tv = (TextView)vwParentRow.findViewById(R.id.gesture_name_ref);
        mCurrentGestureName = tv.getText().toString();
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.gesture_item_options, popup.getMenu());
        popup.show();
    }

    public void addGesture(){
        Intent saveGesture = new Intent(GestureListActivity.this, SaveGestureActivity.class);
        startActivity(saveGesture);
    }

    public void testGesture(){
        Intent testGesture = new Intent(GestureListActivity.this, GestureActivity.class);
        startActivity(testGesture);
    }

    public void deleteButtonClick(MenuItem item){
        gLib.removeEntry(mCurrentGestureName);
        gLib.save();
        mCurrentGestureName = "";
        refreshList();
    }

    public void renameButtonClick(MenuItem item){

        AlertDialog.Builder namePopup = new AlertDialog.Builder(this);
        namePopup.setTitle(getString(R.string.enter_new_name));
        //namePopup.setMessage(R.string.enter_name);

        final EditText nameField = new EditText(this);
        namePopup.setView(nameField);

        namePopup.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameField.getText().toString().isEmpty()) {
                    mNewGestureName = nameField.getText().toString();
                    saveGesture();
                } else {
                    renameButtonClick(null);  //TODO : validation
                    showToast(getString(R.string.invalid_name));
                }
            }
        });
        namePopup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mNewGestureName = "";
                mCurrentGestureName = "";
            }
        });

        namePopup.show();
    }

    private void saveGesture() {
        ArrayList<Gesture> list = gLib.getGestures(mCurrentGestureName);
        if (!list.isEmpty()) {
            gLib.removeEntry(mCurrentGestureName);
            gLib.addGesture(mNewGestureName, list.get(0));
            if (gLib.save()) {
                Log.e(TAG, "gesture renamed!");
                refreshList();
            }
        }
        mNewGestureName = "";
    }
    private void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}