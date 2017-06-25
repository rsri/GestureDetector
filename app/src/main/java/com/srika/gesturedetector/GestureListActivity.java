package com.srika.gesturedetector;

import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class GestureListActivity extends AppCompatActivity {
    private static final String TAG = "GestureListActivity";
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
            Collections.sort(mGestureList, new Comparator<GestureHolder>() {
                @Override
                public int compare(GestureHolder o1, GestureHolder o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.gestures_list) {
            menu.add(0, v.getId(), 0, R.string.rename);
            menu.add(0, v.getId(), 0, R.string.delete);
        }
    }

    public void addGesture(){
        Intent saveGesture = new Intent(GestureListActivity.this, SaveGestureActivity.class);
        startActivity(saveGesture);
    }

    public void testGesture(){
        Intent testGesture = new Intent(GestureListActivity.this, GestureActivity.class);
        startActivity(testGesture);
    }

    public void deleteButtonClick(GestureHolder holder) {

        gLib.removeGesture(holder.getName(), holder.getGesture());
        gLib.save();
        refreshList();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG, ""+item.getMenuInfo());
        if (item.getTitle().toString().equals(getString(R.string.rename))) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            GestureHolder holder = mGestureList.get(info.position);
            renameButtonClick(holder);
            return true;
        } else if (item.getTitle().toString().equals(getString(R.string.delete))) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            GestureHolder holder = mGestureList.get(info.position);
            deleteButtonClick(holder);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void renameButtonClick(final GestureHolder holder){
        AlertDialog.Builder namePopup = new AlertDialog.Builder(this);
        namePopup.setTitle(getString(R.string.enter_new_name));
        //namePopup.setMessage(R.string.enter_name);

        final EditText nameField = new EditText(this);
        namePopup.setView(nameField);

        namePopup.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameField.getText().toString().isEmpty()) {
                    String gestureName = nameField.getText().toString();
                    Gesture gesture = holder.getGesture();
                    gLib.removeGesture(holder.getName(), gesture);
                    gLib.addGesture(gestureName, gesture);
                    if (gLib.save()){
                        Log.e(TAG, "gesture renamed!");
                        refreshList();
                    }
                } else {
                    renameButtonClick(holder);  //TODO : validation
                    showToast(getString(R.string.invalid_name));
                }
            }
        });
        namePopup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        namePopup.show();
    }

    private void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}