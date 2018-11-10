package com.ecoone.mindfulmealplanner.Profile.Setting;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;


import com.ecoone.mindfulmealplanner.R;

public class LogoutDialogPreference extends DialogPreference {

    private boolean isCheckboxChecked = false;

    private static final String TAG = "testActivity";
    private static final String CLASSTAG = "(LogoutDialogPreference)";

    public interface OnDataPassingListener {
        void passDataFromLogoutDialogToSetting(int input);
    }

    public OnDataPassingListener mOnDataPassingListener;

    public LogoutDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(R.layout.preference_logout_dialog);
        setDialogTitle("Do you want to logout?");
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        CheckBox checkBox = view.findViewById(R.id.logout_dialog_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckboxChecked = isChecked;
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            if (isCheckboxChecked) {
                mOnDataPassingListener.passDataFromLogoutDialogToSetting(1);
            }
            else {
                mOnDataPassingListener.passDataFromLogoutDialogToSetting(0);
            }
        }
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        try {
            mOnDataPassingListener = (OnDataPassingListener) getContext();
        }catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " +e.getMessage() + CLASSTAG);
        }
    }
}
