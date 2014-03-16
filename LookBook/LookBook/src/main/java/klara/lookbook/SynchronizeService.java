package klara.lookbook;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import klara.lookbook.exceptions.DownloadException;
import klara.lookbook.exceptions.UnauthorizedException;
import klara.lookbook.model.BaseDbObject;
import klara.lookbook.model.Item;
import klara.lookbook.model.Shop;
import klara.lookbook.utils.UriUtil;

public class SynchronizeService extends IntentService {

    private static final String ACTION_SYNCHRONIZE = "klara.lookbook.action.synchronize";

    public static void startActionSynchronize(Context context) {
        Intent intent = new Intent(context, SynchronizeService.class);
        intent.setAction(ACTION_SYNCHRONIZE);
        context.startService(intent);
    }

    public SynchronizeService() {
        super("SynchronizeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNCHRONIZE.equals(action)) {
                handleActionSynchronize();
            }
        }
    }

    private void handleActionSynchronize() {
        boolean succes = synchronizeItems();
        if(succes) {
            synchronizeShops();
        }
    }

    private boolean synchronizeItems() {
        boolean succes;
        List<Item> uploadedItems = new ArrayList<Item>();
        List<Item> items = BaseDbObject.getByWhere(getApplicationContext(), Item.class, "");

        if(items != null) {
            for(Item item : items) {
                succes = sendBaseDbObjectToSever(item, UriUtil.URL_SAVE_ITEM);
                if(succes) {
                    uploadedItems.add(item);
                }else {
                    return false;
                }
            }
        }
        for(Item uploadItem : uploadedItems) {
            uploadItem.delete();
        }
        return true;
    }

    private boolean synchronizeShops() {
        boolean succes;
        List<Shop> uploadedShops = new ArrayList<Shop>();
        List<Shop> shops = BaseDbObject.getByWhere(getApplicationContext(), Shop.class, "");

        if(shops != null) {
            for(Shop shop : shops) {
                succes = sendBaseDbObjectToSever(shop, UriUtil.URL_SAVE_SHOP);
                if(succes) {
                    uploadedShops.add(shop);
                }else {
                    return false;
                }
            }
        }
        for(Shop uploadShop : uploadedShops) {
            uploadShop.delete();
        }
        return true;
    }

    private boolean sendBaseDbObjectToSever(BaseDbObject object, String uri){
        UriUtil util = new UriUtil(getApplicationContext());
        JSONObject response;
        try {
            response = util.post(uri, object.getValues());
            return response != null && response.getString("succes").equals(UriUtil.VALUE_OK);
        } catch (DownloadException e) {
            e.printStackTrace();
            return false;
        } catch (UnauthorizedException e) {
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
