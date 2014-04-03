package klara.lookbook.utils;

import android.content.ContentValues;
import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import klara.lookbook.exceptions.DownloadException;
import klara.lookbook.exceptions.UnauthorizedException;

public class UriUtil {
    public static final String URL_SERVER = "http://192.168.1.104/klarka/php";
//    public static final String URL_SERVER = "http://klarka-itnerds.rhcloud.com";

    public static final String URL_LOGIN = "/users/loginMb";
    public static final String URL_REGISTER = "/users/addMb";
    public static final String URL_GET_NEAREST_SHOP = "/shops/nearestMb";
    public static final String URL_SAVE_SHOP = "/shops/saveMb";
    public static final String URL_VIEW_SHOPS = "/shops/viewMb";
    public static final String URL_SAVE_ITEM = "/items/saveMb";
    public static final String URL_VIEW_ITEMS = "/items/viewMb";
    public static final String URL_VIEW_ITEM_DETAILS = "/items/viewDetailsMb";
    public static final String URL_VIEW_LEADERS_ITEMS = "/users/viewLeadersItemsMb";
    public static final String URL_VIEW_MESSAGES = "/messages/viewMb";
    public static final String URL_ADD_MESSAGE = "/messages/addMb";
    public static final String URL_FAVORITE_TOGGLE = "/favoritLists/toggleMb";

    public static final String PARAM_IS_MOBILE_REQUEST = "an_is_mobile";
    public static final String PARAM_EMAIL = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_NICK = "nick";
    public static final String PARAM_LAT = "lat";
    public static final String PARAM_LNG = "lng";
    public static final String PARAM_ACCURACY = "accuracy";
    public static final String PARAM_ITEM_ID = "item_id";
    public static final String PARAM_LAST_MESSAGE_ID = "last_message_id";
    public static final String PARAM_MESSAGE_TEXT = "text";
    public static final String PARAM_ID = "id";


    public static final String VALUE_OK = "1";
    public static final String VALUE_FAIL = "0";

    private static DefaultHttpClient client;

    private Context context;

    public UriUtil(Context context) {
        this.context = context;
    }
    /**
     * @param email
     * @param password
     * @return boolean - true if credentials are correct
     * @throws DownloadException
     */
    public boolean login(String email, String password) throws DownloadException, UnauthorizedException {
        ContentValues values = new ContentValues();
        values.put(PARAM_PASSWORD, password);
        values.put(PARAM_EMAIL, email);
        JSONObject jsonObj = _post(URL_LOGIN, values);
        if(jsonObj != null) {
            try {
                if(jsonObj.getString("succes").equals(VALUE_OK)) {
                    AppPref.put(context, AppPref.KEY_USER_ID, jsonObj.getInt("userId"));
                    AppPref.put(context, AppPref.KEY_USER_NICK, jsonObj.getString("userNick"));
                    return true;
                }else {
                    return false;
                }
            } catch (JSONException e) {
                throw new DownloadException();
            }
        }else {
            throw new DownloadException();
        }
    }

    public boolean login() throws DownloadException, UnauthorizedException {
        String email = AppPref.get(context, AppPref.KEY_EMAIL, "");
        String pass = AppPref.get(context, AppPref.KEY_PASSWORD, "");
        return !(email.isEmpty() || pass.isEmpty()) && login(email, pass);
    }

    public JSONObject get(String url, BasicHttpParams params) throws DownloadException, UnauthorizedException {
        try {
            return  _get(url, params);
        } catch (UnauthorizedException e) {
            if(login()) { // try relogin
                return _get(url, params);
            }else {
                throw new UnauthorizedException();
            }
        }
    }

    public JSONObject post(String url, ContentValues values) throws DownloadException, UnauthorizedException {
        try {
            return  _post(url, values);
        } catch (UnauthorizedException e) {
            if(login()) { // try relogin
                return _post(url, values);
            }else {
                throw new UnauthorizedException();
            }
        }
    }

    private JSONObject _get(String url, BasicHttpParams params) throws DownloadException, UnauthorizedException {
        // parametr, oznacujici danny request jako pozadavek odeslany s mobilni aplikace
        params.setParameter(PARAM_IS_MOBILE_REQUEST, "1");

        if(client == null) {
            client = new DefaultHttpClient();
        }

        HttpGet get = new HttpGet(URL_SERVER+url);
        HttpResponse response;
        JSONObject json;
        try {
            get.setParams(params);
            response = client.execute(get);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                throw new UnauthorizedException();
            }
            HttpEntity entity = response.getEntity();
            String stringResponse = EntityUtils.toString(entity);
            if(!stringResponse.equals("[]")){
                json = new JSONObject(stringResponse);
            }else {
                json = null;
            }
        } catch (UnsupportedEncodingException e) {
            throw new DownloadException();
        } catch (IOException e) {
            throw new DownloadException();
        } catch (JSONException e) {
            throw new DownloadException();
        }
        return json;
    }

    private JSONObject _post(String url,ContentValues values) throws DownloadException, UnauthorizedException {
        // parametr, oznacujici danny request jako pozadavek odeslany s mobilni aplikace
        values.put(PARAM_IS_MOBILE_REQUEST, "1");
        if(client == null) {
            client = new DefaultHttpClient();
        }

        HttpPost post = new HttpPost(URL_SERVER+url);
        HttpContext localContext = new BasicHttpContext();
        HttpResponse response;
        JSONObject json;

        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for(Map.Entry<String, Object> entry : values.valueSet()) {
                if(entry.getKey().toLowerCase().contains("file")) {
                    File file = new File(entry.getValue().toString());
                    FileBody fb = new FileBody(file);
                    builder.addPart(entry.getKey(), fb);
                }else {
                    builder.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), ContentType.APPLICATION_FORM_URLENCODED) );
                }
            }
            HttpEntity postEntity = builder.build();
            post.setEntity(postEntity);

            response = client.execute(post, localContext);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                throw new UnauthorizedException();
            }
            HttpEntity entity = response.getEntity();
            String stringResponse = EntityUtils.toString(entity);
            if(!stringResponse.equals("[]")){
                json = new JSONObject(stringResponse);
            }else {
                json = null;
            }

        } catch (UnsupportedEncodingException e) {
            throw new DownloadException();
        } catch (IOException e) {
            throw new DownloadException();
        } catch (JSONException e) {
            throw new DownloadException();
        }
        return json;
    }

}
