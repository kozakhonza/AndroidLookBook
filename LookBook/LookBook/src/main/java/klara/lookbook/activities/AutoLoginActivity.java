package klara.lookbook.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.exceptions.DownloadException;
import klara.lookbook.exceptions.UnauthorizedException;
import klara.lookbook.utils.AppPref;
import klara.lookbook.utils.UriUtil;

public class AutoLoginActivity extends BaseActivity  {

    private UserLoginTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_login);

        String email = AppPref.get(this, AppPref.KEY_EMAIL, "");
        String password = AppPref.get(this, AppPref.KEY_PASSWORD, "");

        if(!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
            task = new UserLoginTask();
            task.init(this,null,null,true);
            task.execute();
        }else {
            startActivity(new Intent(AutoLoginActivity.this, LoginActivity.class));
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.auto_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends BaseAsyncTask {
        private boolean success = false;

        @Override
        protected Object doInBackground(Object[] objects) {
            UriUtil uriUtil = new UriUtil(handler.myGetContext());
            try {
                success =  uriUtil.login();
            } catch (DownloadException e) {
                this.showTryAgainDialog();
            } catch (UnauthorizedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (success) {
                startActivity(new Intent(AutoLoginActivity.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(AutoLoginActivity.this, LoginActivity.class));
                finish();
            }
            task = null;
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) { // todo vyresit pripad kde se nepovede spojit ze serverem, aby se neukazal error ze jse spatne heslo
            task = new UserLoginTask();
            task.init(AutoLoginActivity.this,null,null,true);
            task.execute();
            dialog.dismiss();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
            task = null;
        }
    }

}
