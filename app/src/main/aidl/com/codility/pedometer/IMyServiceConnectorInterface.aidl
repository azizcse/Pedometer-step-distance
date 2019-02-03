// IMyServiceConnectorInterface.aidl
package com.codility.pedometer;

// Declare any non-default types here with import statements
import com.codility.pedometer.IActivity;
interface IMyServiceConnectorInterface {
     void setForeground(in boolean value);
     void registerActivityCallback(in IActivity callback);
}
