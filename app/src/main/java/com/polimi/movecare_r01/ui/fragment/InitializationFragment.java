package com.polimi.movecare_r01.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.polimi.movecare_r01.R;

public class InitializationFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.init_title)
                .setMessage(R.string.init_request);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
