
package klara.lookbook;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.dialogs.ProgressDialog;
import klara.lookbook.dialogs.TryAgainDialog;
import klara.lookbook.exceptions.DownloadException;
import klara.lookbook.exceptions.UnauthorizedException;
import klara.lookbook.utils.UriUtil;

public abstract class BaseAsyncTask extends AsyncTask {

    protected String url;
    protected ArrayList<NameValuePair> params;
    protected boolean showDialog;
    protected IAsyncTaskHandler handler;
    protected BaseDialog dialog;
    protected JSONObject data;

    private boolean wasInitCall = false;

    public void init(IAsyncTaskHandler handler, String url, ArrayList<NameValuePair> params, boolean showDialog) {
        this.url = url;
        this.params = params;
        this.showDialog = showDialog;
        this.handler = handler;
        this.wasInitCall = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(wasInitCall) {
            if(showDialog) {
                dialog = ProgressDialog.newInstance();
                dialog.show(handler.myGetFragmentManager(), "progressDialog");
            }
        }else {
            throw new ClassCastException(this.getClass().getName()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        UriUtil uriUtil = new UriUtil(handler.myGetContext());
        try {
            data = uriUtil.post(url, params);
        } catch (DownloadException e) {
            showTryAgainDialog();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onCancelled() {
        if(dialog != null) {
            dialog.dismiss();
        }
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Object o) {
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    protected void showTryAgainDialog() {
        TryAgainDialog.newInstance(1, handler.myGetTag(), this.getClass().getName()).show(handler.myGetFragmentManager(), "tryAgain");
    }

    public abstract void onTryAgainOk();
    public abstract void onTryAgainCancel();
}
