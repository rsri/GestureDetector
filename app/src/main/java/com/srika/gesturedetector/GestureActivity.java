package com.srika.gesturedetector;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStore;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class GestureActivity extends AppCompatActivity {
    private GestureLibrary gLib;
    private static final String TAG = "GestureActivity";
    private GestureOverlayView mGestureOverlayView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        gLib.setSequenceType(GestureStore.SEQUENCE_INVARIANT);
        gLib.load();

        mGestureOverlayView = (GestureOverlayView) findViewById(R.id.gestures);
        mGestureOverlayView.addOnGesturingListener(mGesturingListener);
//        gestureOverlayView.addOnGestureListener(mGestureListener);
//        gestureOverlayView.addOnGesturePerformedListener(handleGestureListener);
        mGestureOverlayView.setGestureStrokeAngleThreshold(90.0f);
//        gestureOverlayView.setGestureStrokeLengthThreshold(20);
    }

    GestureOverlayView.OnGesturingListener mGesturingListener = new GestureOverlayView.OnGesturingListener() {
        private boolean started = false;

        private Runnable clearRunnable = new Runnable() {
            @Override
            public void run() {
                if (!started && !mGestureOverlayView.isGesturing()) {
                    mGestureOverlayView.clear(false);
                }
            }
        };

        @Override
        public void onGesturingStarted(GestureOverlayView overlay) {
            overlay.removeCallbacks(clearRunnable);
            started = true;
        }

        @Override
        public void onGesturingEnded(final GestureOverlayView gestureView) {
            predict(gestureView.getGesture());
            started = false;
            gestureView.postDelayed(clearRunnable, 2000);
        }
    };

    /**
     * our gesture listener
     */
    private OnGesturePerformedListener handleGestureListener = new OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView gestureView,
                                       Gesture gesture) {
            predict(gesture);
        }
    };

    private void predict(Gesture gesture) {
        ArrayList<Prediction> predictions = gLib.recognize(gesture);

        if (!predictions.isEmpty()) {
            double maxScore = Double.MIN_VALUE;
            Prediction maxPrediction = null;
            for (Prediction prediction : predictions) {
                double currentMaxScore = maxScore;
                maxScore = Math.max(maxScore, prediction.score);
                if (currentMaxScore != maxScore) {
                    maxPrediction = prediction;
                }
            }
            if (maxPrediction != null && maxPrediction.score > 1.0) {
                Log.d(TAG, maxPrediction.name);
                Toast.makeText(GestureActivity.this, maxPrediction.name,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}