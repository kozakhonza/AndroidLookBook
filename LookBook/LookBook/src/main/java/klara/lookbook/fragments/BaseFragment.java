package klara.lookbook.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.IAsyncTaskHandler;
import klara.lookbook.dialogs.BaseDialog;

public abstract class BaseFragment extends Fragment implements BaseDialog.IDialogHandler,
        IAsyncTaskHandler{

    public String TAG = "klara.lookbook.fragments.BaseFragment";

    @Override
    public void onBtnClick(int dialogId, BaseDialog dialog, int which) {

    }

    @Override
    public void onBtnClickWithAsyncTaskClassName(int dialogId, BaseDialog dialog, int which, String asyncClassName) {
        Class myClass;
        try {
            myClass = Class.forName(asyncClassName);
            Class[] types = {((Object)this).getClass()};
            Constructor constructor = myClass.getConstructor(types);
            Object[] parameters = {this};
            Object instanceOfMyClass = constructor.newInstance(parameters);

            ((BaseAsyncTask)instanceOfMyClass).onTryAgainOk();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FragmentManager myGetFragmentManager() {
        return getFragmentManager();
    }

    @Override
    public String myGetTag() {
        return TAG;
    }

    @Override
    public Context myGetContext() {
        return this.getActivity();
    }
}
