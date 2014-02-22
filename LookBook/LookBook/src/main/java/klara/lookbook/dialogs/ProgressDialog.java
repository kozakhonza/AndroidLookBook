package klara.lookbook.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import klara.lookbook.R;

public class ProgressDialog extends BaseDialog {

    public static ProgressDialog newInstance() {
        Bundle arg = BaseDialog.baseNewInstance(0, null);
        ProgressDialog dialog = new ProgressDialog();
        dialog.setArguments(arg);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyProgressDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_progress, null);
    }
}
