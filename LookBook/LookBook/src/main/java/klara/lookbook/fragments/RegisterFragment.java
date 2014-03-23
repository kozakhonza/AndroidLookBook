package klara.lookbook.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.activities.AutoLoginActivity;
import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.model.Item;
import klara.lookbook.utils.AppPref;
import klara.lookbook.utils.UriUtil;

public class RegisterFragment extends BaseFragment {

    private EditText nickView;
    private EditText emailView;
    private EditText passwordView;
    private EditText passwordControlView;

    private String nick;
    private String email;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        rootView.findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        nickView = (EditText) rootView.findViewById(R.id.editTextNick);
        emailView = (EditText) rootView.findViewById(R.id.editTextEmail);
        passwordView = (EditText) rootView.findViewById(R.id.editTextPassword);
        passwordControlView = (EditText) rootView.findViewById(R.id.editTextPasswordControl);

        return rootView;
    }

    private boolean validateError() {
        boolean isValid = true;

        nick = nickView.getText().toString();
        email = emailView.getText().toString();
        password = passwordView.getText().toString();
        String passwordControl = passwordControlView.getText().toString();

        nickView.setError(null);
        emailView.setError(null);
        passwordView.setError(null);
        passwordControlView.setError(null);

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (password.length() < 4) {
            passwordView.setError(getString(R.string.error_invalid_password));
            isValid = false;
        } else if(!password.equals(passwordControl)) {
            passwordControlView.setError(getString(R.string.error_password_not_match));
            isValid = false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!email.contains("@")) {
            emailView.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }

        // Check for a valid nick name.
        if (TextUtils.isEmpty(nick)) {
            nickView.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        return isValid;
    }

    private ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(UriUtil.PARAM_NICK, nick);
        values.put(UriUtil.PARAM_EMAIL, email);
        values.put(UriUtil.PARAM_PASSWORD, password);
        return values;
    }

    private void register() {
        if(validateError()) {
            RegisterTask task = new RegisterTask();
            task.init(this, UriUtil.URL_REGISTER, getValues(), true);
            task.execute();
        }
    }

    private class RegisterTask extends BaseAsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            try {
                if(data.getString("succes").equals(UriUtil.VALUE_OK)) {
                    AppPref.put(getActivity(), AppPref.KEY_EMAIL, email);
                    AppPref.put(getActivity(), AppPref.KEY_PASSWORD, password);
                    startActivity(new Intent(getActivity(), AutoLoginActivity.class));
                    getActivity().finish();
                }else {
                    emailView.setError(getString(R.string.error_email_exists));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) {
            dialog.dismiss();
            register();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
        }

    }
}
