package com.srika.gesturedetector;

import android.gesture.Gesture;

/**
 * Created by manan on 2/23/2015.
 */
public class GestureHolder {
    private Gesture gesture;
    private String gestureName;

    public GestureHolder(Gesture gesture, String naam){
        this.gesture = gesture;
        this.gestureName = naam;
    }

    public Gesture getGesture(){
        return gesture;
    }

    public void setGesture(Gesture gesture){
        this.gesture = gesture;
    }

    public String getName(){
        return gestureName;
    }

    public void setName(String naam){
        this.gestureName = naam;
    }

}
