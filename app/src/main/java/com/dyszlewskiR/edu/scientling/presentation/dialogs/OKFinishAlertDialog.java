package com.dyszlewskiR.edu.scientling.presentation.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.dyszlewskiR.edu.scientling.R;

/**
 * Dialog pokazujący pewną wiadomośc i zamykacjący przekazaną aktywność
 */
public class OKFinishAlertDialog extends AlertDialog {
    public OKFinishAlertDialog(final Context context, String title, String message) {
        super(context);
        this.setTitle(title);
        this.setMessage(message);
        this.setCancelable(true);
        this.setButton(BUTTON_NEGATIVE, context.getString(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
            }
        });
    }
}
