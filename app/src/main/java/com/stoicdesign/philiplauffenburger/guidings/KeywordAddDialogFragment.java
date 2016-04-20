package com.stoicdesign.philiplauffenburger.guidings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by philiplauffenburger on 3/7/16.
 */
public class KeywordAddDialogFragment extends DialogFragment {
    CharSequence[] keywordArrayChar;
    KeywordAddDialogListener mListener;
    static KeywordAddDialogFragment newInstance(CharSequence[] charSequence) {
        KeywordAddDialogFragment f = new KeywordAddDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putCharSequenceArray("keywordArrayChar", charSequence);
        f.setArguments(args);

        return f;
    }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        keywordArrayChar = getArguments().getCharSequenceArray("keywordArrayChar");
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose A Keyword to Follow")
                .setItems(keywordArrayChar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String keyword = keywordArrayChar[which].toString();
                        mListener.onSelectKeyword(keyword);
                    }
                });

        return builder.create();
    }

    public interface KeywordAddDialogListener{
        public void onSelectKeyword(String keyword);
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (KeywordAddDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


}
