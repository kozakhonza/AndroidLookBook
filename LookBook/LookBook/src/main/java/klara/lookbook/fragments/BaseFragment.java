package klara.lookbook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.IAsyncTaskHandler;
import klara.lookbook.dialogs.BaseDialog;

public abstract class BaseFragment extends Fragment implements BaseDialog.IDialogHandler,
        IAsyncTaskHandler{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

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

            if(which == BaseDialog.IDialogHandler.POSITIVE_BUTTON) {
                ((BaseAsyncTask)instanceOfMyClass).onTryAgainOk(dialog);
            }else {
                ((BaseAsyncTask)instanceOfMyClass).onTryAgainCancel(dialog);
            }


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

    public String myGetTag() {
        return this.getClass().getName();
    }

    @Override
    public Context myGetContext() {
        return this.getActivity();
    }
}
