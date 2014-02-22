package klara.lookbook.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import klara.lookbook.R;

public class TryAgainDialog extends BaseDialog {

    public static TryAgainDialog newInstance(int dialogId, String fragmentTag) {
        Bundle arg = BaseDialog.baseNewInstance(dialogId, fragmentTag);
        TryAgainDialog dialog = new TryAgainDialog();
        dialog.setArguments(arg);
        return dialog;
    }

    public static TryAgainDialog newInstance(int dialogId, String fragmentTag, String asyncTaskClasName) {
        Bundle arg = BaseDialog.baseNewInstance(dialogId, fragmentTag);
        arg.putString("asyncTaskClasName", asyncTaskClasName);
        TryAgainDialog dialog = new TryAgainDialog();
        dialog.setArguments(arg);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyAppCompatDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_tryagain, null);
        Bundle arg = getArguments();
        final String asyncTaskClasName = arg.getString("asyncTaskClasName");
        if(asyncTaskClasName == null) {
            rootView.findViewById(R.id.btn_again).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onBtnClick(dialogId, TryAgainDialog.this, IDialogHandler.POSITIVE_BUTTON);
                    }
                }
            });

            rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onBtnClick(dialogId, TryAgainDialog.this, IDialogHandler.NEGATIVE_BUTTON);
                    }
                }
            });

        }else {
            rootView.findViewById(R.id.btn_again).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onBtnClickWithAsyncTaskClassName(dialogId, TryAgainDialog.this,
                                IDialogHandler.POSITIVE_BUTTON, asyncTaskClasName);
                    }
                }
            });

            rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onBtnClickWithAsyncTaskClassName(dialogId, TryAgainDialog.this,
                                IDialogHandler.NEGATIVE_BUTTON, asyncTaskClasName);
                    }
                }
            });

        }

        return rootView;
    }
}
