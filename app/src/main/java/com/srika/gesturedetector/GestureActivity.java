package com.srika.gesturedetector;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

public class GestureActivity extends AppCompatActivity {
    private GestureLibrary gLib;
    private static final String TAG = "GestureActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        gLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/" + "gesture.txt");
        gLib.load();

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(handleGestureListener);
        gestures.setGestureStrokeAngleThreshold(90.0f);
    }


    /**
     * our gesture listener
     */
    private OnGesturePerformedListener handleGestureListener = new OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView gestureView,
                                       Gesture gesture) {

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
                    Toast.makeText(GestureActivity.this, maxPrediction.name,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}