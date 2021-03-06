package klara.lookbook.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.activities.MainActivity;
import klara.lookbook.dialogs.BaseDialog;
import klara.lookbook.dialogs.ProgressDialog;
import klara.lookbook.model.BaseDbObject;
import klara.lookbook.model.Item;
import klara.lookbook.utils.ImageUtil;
import klara.lookbook.utils.UriUtil;

public class AddItemFragment extends BaseFragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private LocationClient mLocationClient;

    private int reconnectCounter = 0;

    private EditText title;
    private AutoCompleteTextView shopAutoComplete;
    private EditText price;
    private EditText description;
    private ImageView imageView;

    private String mCurrentPhotoPath;
    private String[] shops;
    private int[] shopwIds;

    private int targetW;
    private int targetH;
    private boolean photoTaked = false;
    private Item item;


    public static AddItemFragment newInstance(int sectionNumber) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putInt("sectionNumber", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getActivity(), this, this);
        if(savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
            shops = savedInstanceState.getStringArray("shops");
            shopwIds = savedInstanceState.getIntArray("shopwIds");
            targetW = savedInstanceState.getInt("targetW");
            targetH = savedInstanceState.getInt("targetH");
            photoTaked = savedInstanceState.getBoolean("photoTaked");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("mCurrentPhotoPath", mCurrentPhotoPath);
        outState.putStringArray("shops", shops);
        outState.putIntArray("shopwIds", shopwIds);
        outState.putInt("targetW", targetW);
        outState.putInt("targetH", targetH);
        outState.putBoolean("photoTaked", photoTaked);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_add_item, container, false);

        title = (EditText) mainView.findViewById(R.id.editTextTitle);
        shopAutoComplete = (AutoCompleteTextView) mainView.findViewById(R.id.autoCompleteTextViewShop);
        price = (EditText) mainView.findViewById(R.id.editTextPrice);
        description = (EditText) mainView.findViewById(R.id.editTextDescription);
        imageView = (ImageView) mainView.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetW = imageView.getWidth();
                targetH = imageView.getHeight();
                takeThePicture();
            }
        });

        shopAutoComplete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopAutoComplete.showDropDown();
            }
        });

        mainView.findViewById(R.id.btn_save).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });

        return mainView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt("sectionNumber"));
    }

    @Override
    public void onStart() {
        super.onStart();

        if( isLocationServiceEnabled() && googleServicesConnected() && shops == null) {
            ProgressDialog.newInstance().show(getFragmentManager(), "ProgressDialog");
            mLocationClient.connect();
        }else if(shops != null) {
            initAutoCompleteAdapter();
        }
    }

    private boolean isLocationServiceEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.add_item_frag_gps_not_avaible);
            builder.setMessage(R.string.add_item_frag_gps_not_avaible_text);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    AddItemFragment.this.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton(R.string.no, null);
            builder.create().show();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(photoTaked) {
            ImageUtil.showImageInImgeView(imageView, mCurrentPhotoPath, targetW, targetH);
        }
    }

    public void takeThePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageUtil.createImageFile();
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private boolean googleServicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(),
                        "Location Updates");
            }
        }
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if( resultCode == Activity.RESULT_OK){
                    photoTaked = true;
                    ImageUtil.showImageInImgeView(imageView, mCurrentPhotoPath, targetW, targetH);
                }
                break;
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK :
                        break;
                }
        }
    }

    private void saveItem() {
        if(validInputs()) {
            if(item == null) {
                item = BaseDbObject.newInstance(this.getActivity(), Item.class);
            }
            item.setTitle(title.getText().toString());
            item.setShopId(getShopId());
            item.setPrice( (int)(Float.valueOf(price.getText().toString())* 1000));
            item.setCurrency(0);
            item.setDescription(description.getText().toString());
            item.setImageUri(mCurrentPhotoPath);
            item.save();
            Toast.makeText(getActivity(), getString(R.string.add_item_frag_item_hasbeen_saved), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validInputs() {
        boolean isValid = true;

        if(this.title.getText().toString().isEmpty())
        {
            this.title.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if(this.price.getText().toString().isEmpty()) {
            this.price.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if(getShopId() == -1) {
            this.shopAutoComplete.setError(getString(R.string.error_shop_doesnt_exists));
            isValid = false;
        }

        if(this.mCurrentPhotoPath == null || this.mCurrentPhotoPath.isEmpty())
        {
            Toast.makeText(getActivity(), getString(R.string.error_photo_required), Toast.LENGTH_LONG).show();
            isValid = false;
        }
        return isValid;
    }

    private int getShopId() {
        int id = -1;
        String shop = this.shopAutoComplete.getText().toString();
        if(shop != null && !shop.isEmpty()) {
            boolean isCorrect = false;
            int length = shops.length;
            for(int i = 0; i < length; i++) {
                isCorrect |= shop.contains(shops[i]);
                if(isCorrect) {
                    id = i;
                    break;
                }
            }
        }
        return id;
    }

    @Override
    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = mLocationClient.getLastLocation();
        if(location != null) {
           getNearestShop();
        }else if(reconnectCounter < 3){
            reconnectLocation();
        }else {
            //todo - ukazat hlasku ze se nepovedlo zjiskat soucasnou polohu, pridat moznost vybrat z mapy
        }
    }

    private void getNearestShop() {
        Location location = mLocationClient.getLastLocation();
        float diameter = location.getAccuracy() * 2;
        if(diameter < 300.0) {
            diameter = 300;
        }
        ContentValues values = new ContentValues();
        values.put(UriUtil.PARAM_LAT, String.valueOf(location.getLatitude()));
        values.put(UriUtil.PARAM_LNG, String.valueOf(location.getLongitude()));
        values.put(UriUtil.PARAM_ACCURACY, String.valueOf(diameter));

        GetNearestShopTask task = new GetNearestShopTask();
        task.init(this, UriUtil.URL_GET_NEAREST_SHOP, values, false);
        task.execute();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        myDismissDialog("ProgressDialog");
    }

    private void reconnectLocation() {
        mLocationClient.disconnect();
        mLocationClient = null;

        mLocationClient = new LocationClient(getActivity(), this, this);
        mLocationClient.connect();
        reconnectCounter++;
    }

    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    private void initAutoCompleteAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.autocomplete_item, shops);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        shopAutoComplete.setAdapter(adapter);
    }

    private class GetNearestShopTask extends BaseAsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(data != null) {
                try {
                    JSONArray jsonShops = data.names();
                    int length = jsonShops.length();
                    int shopId;
                    shopwIds = new int[length];
                    shops = new String[length];
                    for(int i = 0; i < length; i++) {
                        shopId = jsonShops.getInt(i);
                        shopwIds[i] = shopId;
                        shops[i] = data.getString(String.valueOf(shopId));
                    }
                    initAutoCompleteAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            myDismissDialog("ProgressDialog");
        }

        @Override
        public void onTryAgainOk(BaseDialog dialog) {
            dialog.dismiss();
            getNearestShop();
        }

        @Override
        public void onTryAgainCancel(BaseDialog dialog) {
            dialog.dismiss();
            myDismissDialog("ProgressDialog");
        }
    }
}
