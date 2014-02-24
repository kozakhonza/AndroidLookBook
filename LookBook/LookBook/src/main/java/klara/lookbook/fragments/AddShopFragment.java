package klara.lookbook.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import klara.lookbook.R;
import klara.lookbook.model.BaseDbObject;
import klara.lookbook.model.Shop;

public class AddShopFragment extends BaseFragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private LocationClient mLocationClient;
    private Location location;

    private int reconnectCounter = 0;

    private String mCurrentPhotoPath = "";
    private EditText title;
    private EditText shopingCenter;
    private EditText city;
    private EditText street;
    private ImageView imageView;

    public static AddShopFragment newInstance() {
        AddShopFragment fragment = new AddShopFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getActivity(), this, this);
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
    public void onStart() {
        super.onStart();
        if(googleServicesConnected()) {
            mLocationClient.connect();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void takeThePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
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
                    resultCode, // todo tady tim si nejsem jisty
                    getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(),
                        "Location Updates");
            }else {
                // todo ukazat dialog ze google play service neni dostupna
            }
        }
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if( resultCode == Activity.RESULT_OK){
                    setPic();
                }
                break;
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK :
                        break;
                }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private void saveShop() {
        if(validInputs()) {
            Shop shop = BaseDbObject.newInstance(this.getActivity(), Shop.class);
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
        }
    }

    private boolean validInputs() {
        return true; // todo validace
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = mLocationClient.getLastLocation();
        if (location == null && reconnectCounter < 3) {
            reconnectLocation();
        }else {
            //todo - ukazat hlasku ze se nepovedlo zjiskat soucasnou polohu ...
        }

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
        } else {
//            showErrorDialog(connectionResult.getErrorCode()); todo
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
