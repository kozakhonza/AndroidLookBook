package klara.lookbook.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemHome extends Item {

    private String userTitle;

    @Override
    protected void initFromJsonObject(JSONObject object) {
        super.initFromJsonObject(object);
        try {
            userTitle = object.isNull("user_nick") ?
                    "" : object.getString("user_nick");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserTitle() {
        return userTitle;
    }
}
