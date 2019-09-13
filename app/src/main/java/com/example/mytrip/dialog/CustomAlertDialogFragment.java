package com.example.mytrip.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.mytrip.R;
import com.example.mytrip.custominterface.AlertDialogListener;


public class CustomAlertDialogFragment extends DialogFragment {

    private static final String MSG = "message";
    private static final String BUTTON_TEXT = "buttonText";

    private AlertDialog dialog;
    private View convertView;
    private TextView infoMsg;
    private ImageButton close, infoImg;

    private String message;
    private String btnText;
    // Use this instance of the interface to deliver action events
    private AlertDialogListener mListener = null;

    private View.OnClickListener mCloseDialogListener = (View v) -> dialog.dismiss();

    public static CustomAlertDialogFragment newInstance(String confirmMessage, String btnText) {
        CustomAlertDialogFragment frag = new CustomAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(MSG, confirmMessage);
        args.putString(BUTTON_TEXT, btnText);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        // create view for add item in dialog
        convertView = inflater.inflate(R.layout.custom_dialog_fragment, null);
        infoMsg = convertView.findViewById(R.id.infoMsg);
        close = convertView.findViewById(R.id.ib_close);
        infoImg = convertView.findViewById(R.id.info_img);

        //getting passed message
        assert getArguments() != null;
        message = getArguments().getString(MSG);
        btnText = getArguments().getString(BUTTON_TEXT);
        infoMsg.setText(message);
        close.setOnClickListener(mCloseDialogListener);
        //btnText = getString(R.string.ok);
        infoImg.setImageResource(R.drawable.ic_alert);
        infoImg.setColorFilter(getContext().getColor(R.color.danger));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(convertView)
                .setPositiveButton(btnText, (dialog, id) -> {
                    dialog.cancel();
                    if (mListener != null) {
                        mListener.onConfirmBtnDialogClick();
                    }
                });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            dialog.dismiss();
            if (mListener != null) {
                mListener.onCancelBtnDialogClick();
            }

        });

        // Create the AlertDialog object and return it
        dialog = builder.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getColor(R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getColor(R.color.danger));
        });
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AlertDialogListener) getTargetFragment();
            if (mListener == null) {
                mListener = (AlertDialogListener) context;
            }
        } catch (ClassCastException e) {
            mListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
