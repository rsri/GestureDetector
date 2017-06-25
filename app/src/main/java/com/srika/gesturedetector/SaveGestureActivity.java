package com.srika.gesturedetector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

public class SaveGestureActivity extends AppCompatActivity {
    private GestureLibrary mGLib;
    private static final String TAG = "SaveGestureActivity";
    private boolean mGestureDrawn;
    private Gesture mCurrentGesture;
    private String mGestureName;
    private GestureOverlayView mGestureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_gesture);
        Log.d(TAG, "path = " + Environment.getExternalStorageDirectory().getAbsolutePath());

        mGLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        mGLib.load();

        mGestureView = (GestureOverlayView) findViewById(R.id.gesture_overlay);
        mGestureView.addOnGestureListener(mGestureListener);

        resetEverything();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gesture_options, menu);
        return true;
    }

    /**
     * our gesture listener
     */
    private GestureOverlayView.OnGestureListener mGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mGestureDrawn = true;
            Log.d(TAG, "andar");
        }

        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
            mCurrentGesture = overlay.getGesture();
        }

        @Override
        public void onGestureEnded(GestureOverlayView gestureView, MotionEvent motion) {
            Log.d(TAG, "bahar");
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            mGestureDrawn = false;
            Log.d(TAG, "cancel");
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_gesture:
                reDrawGestureView();
                break;

            case R.id.save_gesture:
                if(mGestureDrawn){
                   getName();
                } else{
                    showToast(getString(R.string.no_gesture));
                }

                //TODO : Save gesture as image, dont delete this code
                /*
                String pattern = "mm ss";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                String time = formatter.format(new Date());
                String path = ("/d-codepages" + time + ".png");

                File file = new File(Environment.getExternalStorageDirectory()
                        + path);

                try {
                    //DrawBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    //new FileOutputStream(file));
                    Toast.makeText(this, "File Saved ::" + path, Toast.LENGTH_SHORT)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(this, "ERROR" + e.toString(), Toast.LENGTH_SHORT)
                            .show();
                }   */
        }
        return super.onOptionsItemSelected(item);
    }

    private void getName() {
        AlertDialog.Builder namePopup = new AlertDialog.Builder(this);
        namePopup.setTitle(getString(R.string.enter_name));
        //namePopup.setMessage(R.string.enter_name);

        final EditText nameField = new EditText(this);
        namePopup.setView(nameField);
        namePopup.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!nameField.getText().toString().isEmpty()) {
                    mGestureName = nameField.getText().toString();
                    saveGesture();
                } else {
                    getName();  //TODO : set name field with old name string user added
                    showToast(getString(R.string.invalid_name));
                }
            }
        });
        namePopup.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mGestureName = "";
            }
        });

        namePopup.show();

    }

    private void showToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void saveGesture() {
        mGLib.addGesture(mGestureName, mCurrentGesture);
        if (!mGLib.save()) {
            Log.e(TAG, "gesture not saved!");
        }else {
            showToast(getString(R.string.gesture_saved) + getExternalFilesDir(null) + "/gesture.txt");
            finish();
        }
    }

    private void resetEverything(){
        mGestureDrawn = false;
        mCurrentGesture = null;
        mGestureName = "";
    }

    private void reDrawGestureView() {
        mGestureView.clear(false);
        mGestureView.removeAllOnGestureListeners();
        mGestureView.addOnGestureListener(mGestureListener);
        resetEverything();
    }
}