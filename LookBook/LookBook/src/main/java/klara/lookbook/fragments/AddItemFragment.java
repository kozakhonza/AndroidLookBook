package klara.lookbook.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import klara.lookbook.BaseAsyncTask;
import klara.lookbook.R;
import klara.lookbook.utils.UriUtil;

public class AddItemFragment extends BaseFragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private LocationClient mLocationClient;

    private ImageView imageView;

    private int reconnectCounter = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddItemFragment.
     */
    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        View mainView = inflater.inflate(R.layout.fragment_add_item, container, false);
        imageView = (ImageView) mainView.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeThePicture();
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

    public void takeThePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                }
                break;
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK :
                        break;
                }
        }
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = mLocationClient.getLastLocation();
        if(location != null) {
            ArrayList<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair(UriUtil.PARAM_LAT, String.valueOf(location.getLatitude())));
            values.add(new BasicNameValuePair(UriUtil.PARAM_LNG, String.valueOf(location.getLongitude())));

            GetNearestShopTask task = new GetNearestShopTask();
            task.init(this, UriUtil.URL_GET_NEAREST_SHOP,values, true);
            task.execute();
        }else if(reconnectCounter < 3){
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

    private class GetNearestShopTask extends BaseAsyncTask {

        @Override
        public void onTryAgainOk() {
        }

        @Override
        public void onTryAgainCancel() {
        }
    }
}
