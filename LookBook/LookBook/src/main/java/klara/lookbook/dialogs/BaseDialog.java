package klara.lookbook.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public abstract class BaseDialog extends DialogFragment {

    protected IDialogHandler listener;
    protected int dialogId;

    protected static Bundle baseNewInstance(int dialogId, String fragmentTag) {
        Bundle arg = new Bundle();
        arg.putString("fragmentTag", fragmentTag);
        arg.putInt("dialogId", dialogId);
        return arg;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle arg = getArguments();
        String fragmentTag = arg.getString("fragmentTag");
        dialogId = arg.getInt("dialogId");

        if(fragmentTag != null) {
            listener = (IDialogHandler) getFragmentManager().findFragmentByTag(fragmentTag);
        }else {
            listener = (IDialogHandler) getActivity();
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    public interface IDialogHandler {
        public static final int POSITIVE_BUTTON = 1;
        public static final int NEGATIVE_BUTTON = 2;
        public static final int NEUTRAL_BUTTON = 3;
        public void onBtnClick(int dialogId, BaseDialog dialog, int which);
        public void onBtnClickWithAsyncTaskClassName(int dialogId, BaseDialog dialog,
                                                     int which, String asyncClassName);
    }
}
