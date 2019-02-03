// IActivity.aidl
package com.codility.pedometer;

// Declare any non-default types here with import statements

interface IActivity {
   void onUiUpdate(in long steps, in float distance);
}
