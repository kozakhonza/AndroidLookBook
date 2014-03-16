package klara.lookbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.exceptions.DownloadException;
import klara.lookbook.exceptions.UnauthorizedException;
import klara.lookbook.utils.AppPref;
import klara.lookbook.utils.UriUtil;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends BaseActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private String mEmail;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmail = AppPref.get(this, AppPref.KEY_EMAIL, "");
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(mEmail);

        String mPassword = AppPref.get(this, AppPref.KEY_PASSWORD, "");
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mPasswordView.setText(mPassword);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        if(!mPassword.equals("") && !mEmail.equals("")) {
            attemptLogin();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        String mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);

            AppPref.put(this, AppPref.KEY_EMAIL, mEmail);
            AppPref.put(this, AppPref.KEY_PASSWORD, mPassword);

            mAuthTask = new UserLoginTask();
            mAuthTask.init(this,null,null,true);
            mAuthTask.execute();
        }
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
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
            mAuthTask = null;
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) {
            mAuthTask = new UserLoginTask();
            mAuthTask.init(LoginActivity.this,null,null,true);
            mAuthTask.execute();
            dialog.dismiss();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
            mAuthTask = null;
        }
    }
}
