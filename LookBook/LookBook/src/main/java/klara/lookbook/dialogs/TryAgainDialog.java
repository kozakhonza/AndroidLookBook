package klara.lookbook.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import klara.lookbook.R;

public class TryAgainDialog extends android.support.v4.app.DialogFragment {

    public static final String TAG = "klara.lookbook.dialogs.TryAgainDialog";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyAppCompatDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_tryagain, null);
        rootView.findViewById(R.id.btn_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TryAgainDialogI) getActivity()).onTryAgainBtnClick();
                TryAgainDialog.this.dismiss();
            }
        });

        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TryAgainDialogI) getActivity()).onCancelBtnClick();
                TryAgainDialog.this.dismiss();
            }
        });

        return rootView;
    }


    public interface TryAgainDialogI {
        public void onTryAgainBtnClick();
        public void onCancelBtnClick();
    }
}
