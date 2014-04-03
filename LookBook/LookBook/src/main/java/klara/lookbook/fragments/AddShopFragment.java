package klara.lookbook.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import java.io.File;
import java.io.IOException;

import klara.lookbook.R;
import klara.lookbook.activities.MainActivity;
import klara.lookbook.dialogs.ProgressDialog;
import klara.lookbook.model.BaseDbObject;
import klara.lookbook.model.Shop;
import klara.lookbook.utils.ImageUtil;

public class AddShopFragment extends BaseFragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private LocationClient mLocationClient;
    private Location location;

    private int reconnectCounter = 0;

    private EditText title;
    private EditText shopingCenter;
    private EditText city;
    private EditText street;
    private ImageView imageView;

    private String mCurrentPhotoPath = "";
    private int targetW;
    private int targetH;
    private boolean photoTaked = false;
    private Shop shop;

    public static AddShopFragment newInstance(int sectionNumber) {
        AddShopFragment fragment = new AddShopFragment();
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
            targetW = savedInstanceState.getInt("targetW");
            targetH = savedInstanceState.getInt("targetH");
            photoTaked = savedInstanceState.getBoolean("photoTaked");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("mCurrentPhotoPath", mCurrentPhotoPath);
        outState.putInt("targetW", targetW);
        outState.putInt("targetH", targetH);
        outState.putBoolean("photoTaked", photoTaked);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_add_shop, container, false);

        title = (EditText) mainView.findViewById(R.id.editTextTitle);
        shopingCenter = (EditText) mainView.findViewById(R.id.editTextShopingCenter);
        city = (EditText) mainView.findViewById(R.id.editTextCity);
        street = (EditText) mainView.findViewById(R.id.editTextStreet);
        imageView = (ImageView) mainView.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                targetW = imageView.getWidth();
                targetH = imageView.getHeight();
                takeThePicture();
            }
        });
        mainView.findViewById(R.id.btn_save).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveShop();
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
        if(isLocationServiceEnabled() && googleServicesConnected() && location == null) {
            ProgressDialog.newInstance().show(getFragmentManager(), "ProgressDialog");
            mLocationClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(photoTaked) {
            ImageUtil.showImageInImgeView(imageView, mCurrentPhotoPath, targetW, targetH);
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
                    AddShopFragment.this.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton(R.string.no, null);
            builder.create().show();
            return false;
        }
        return true;
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

    private void saveShop() {
        if(validInputs()) {
            if(shop == null) {
                shop = BaseDbObject.newInstance(this.getActivity(), Shop.class);
            }
            shop.setTitle(title.getText().toString());
            shop.setShopingCenter(shopingCenter.getText().toString());
            shop.setCity(city.getText().toString());
            shop.setStreet(street.getText().toString());
            if(location != null) {
                shop.setLat(location.getLatitude());
                shop.setLng(location.getLongitude());
            }

            shop.setImageUri(mCurrentPhotoPath);
            shop.save();
            Toast.makeText(getActivity(), getString(R.string.add_shop_frag_shop_hasbeen_saved), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validInputs() {
        boolean isValid = true;

        if(this.title.getText().toString().isEmpty())
        {
            this.title.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if(this.city.getText().toString().isEmpty())
        {
            this.city.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if(this.mCurrentPhotoPath == null || this.mCurrentPhotoPath.isEmpty())
        {
            Toast.makeText(getActivity(), getString(R.string.error_photo_required), Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = mLocationClient.getLastLocation();
        if (location == null && reconnectCounter < 3) {
            reconnectLocation();
        }else if(location == null){
            //todo - ukazat hlasku ze se nepovedlo zjiskat soucasnou polohu ...
        }
        myDismissDialog("ProgressDialog");

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
            myDismissDialog("ProgressDialog");
        }

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
}
