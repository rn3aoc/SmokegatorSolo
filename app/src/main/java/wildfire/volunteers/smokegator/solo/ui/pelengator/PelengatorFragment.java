package wildfire.volunteers.smokegator.solo.ui.pelengator;

import android.hardware.GeomagneticField;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.Date;
import java.util.Locale;

import wildfire.volunteers.smokegator.solo.R;
import wildfire.volunteers.smokegator.solo.data.Peleng;
import wildfire.volunteers.smokegator.solo.ui.CompassView;
import wildfire.volunteers.smokegator.solo.viewmodel.PelengViewModel;

public class PelengatorFragment extends Fragment {

    private static final String TAG = "PelengatorFragment";

    // Code used in requesting runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // Constant used in the location settings dialog.
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    //The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    //Possible Azimuth values
    private static final float MIN_BEARING = 0f;
    private static final float MAX_BEARING = 360f;

    // Provides access to the Fused Location Provider API.
    private FusedLocationProviderClient mFusedLocationClient;
    // Provides access to the Location Settings API.
    private SettingsClient mSettingsClient;
    // Stores parameters for requests to the FusedLocationProviderApi.
    private LocationRequest mLocationRequest;
    // Stores the types of location services the client is interested in using. Used for checking
    // settings to determine if the device has optimal location settings.
    private LocationSettingsRequest mLocationSettingsRequest;
    // Callback for Location events.
    private LocationCallback mLocationCallback;
    //Represents a geographical location.
    private Location mCurrentLocation;
    // Tracks the status of the location updates request. Value changes when the user presses the
    // Start Updates and Stop Updates buttons.
    private Boolean mRequestingLocationUpdates;
    // Time when the location was updated represented as a String.
    private String mLastUpdateTime;

    private float mInclination;

    // Local geomagnetic inclination object.
    private GeomagneticField mGeomagneticField = new GeomagneticField(
            0,
            0,
            0,
            new Date().getTime());

    // UI Widgets.
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;
    private ToggleButton mToggleButton;
    private EditText latitudeView;
    private EditText longitudeView;
    private EditText magBearing;
    private EditText trueBearing;
    private EditText callsignView;
    private TextView accuracy;
    private TextView inclinationView;
    private CompassView compassView;
    private Button sendButton;

    private PelengViewModel mViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_pelengator, container, false);

        mToggleButton = rootView.findViewById(R.id.GPStoggleButton);
        latitudeView = rootView.findViewById(R.id.editTextLat);
        longitudeView = rootView.findViewById(R.id.editTextLng);
        magBearing = rootView.findViewById(R.id.editTextMBearing);
        trueBearing = rootView.findViewById(R.id.editTextTBearing);
        accuracy = rootView.findViewById(R.id.AccuracyTextView);
        inclinationView = rootView.findViewById(R.id.inclinationTextView);
        compassView = rootView.findViewById(R.id.compassView);
        callsignView = rootView.findViewById(R.id.callsignEditText);
        sendButton = (Button) rootView.findViewById(R.id.sendButton);

        mViewModel = ViewModelProviders.of(this).get(PelengViewModel.class);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

        // Check latitude input
        latitudeView.addTextChangedListener(new TextWatcher() {
            double latitude;
            double longitude;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (latitudeView.getText().toString().isEmpty()) latitude = 0;
                else latitude = Float.parseFloat(latitudeView.getText().toString());

                if (Math.abs(latitude) > 90d)
                    latitudeView.setError(getString(R.string.errormsg_lat));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (latitudeView.getText().toString().isEmpty()) latitude = 0;
                else latitude = Float.parseFloat(latitudeView.getText().toString());
                if (longitudeView.getText().toString().isEmpty()) longitude = 0;
                else longitude = Float.parseFloat(longitudeView.getText().toString());

                mGeomagneticField = new GeomagneticField(
                        (float) latitude,
                        (float) longitude,
                        0f,
                        new Date().getTime());

                inclinationView.setText(String.format(Locale.US, "Incl. %.2f°", mGeomagneticField.getDeclination()));
            }
        });

        // Check longitude input
        longitudeView.addTextChangedListener(new TextWatcher() {
            double latitude;
            double longitude;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (latitudeView.getText().toString().isEmpty()) longitude = 0;
                else longitude = Float.parseFloat(longitudeView.getText().toString());  //ToDo crash on "empty String"

                if (Math.abs(longitude) > 180d)
                    longitudeView.setError(getString(R.string.errormsg_lng));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (latitudeView.getText().toString().isEmpty()) latitude = 0;
                else latitude = Float.parseFloat(latitudeView.getText().toString());
                if (longitudeView.getText().toString().isEmpty()) longitude = 0;
                else longitude = Float.parseFloat(longitudeView.getText().toString());
                mGeomagneticField = new GeomagneticField(
                        (float) latitude,
                        (float) longitude,
                        0f,
                        new Date().getTime());

                inclinationView.setText(String.format(Locale.US, "Incl. %.2f°", mGeomagneticField.getDeclination()));
            }
        });

        // Mag and True bearings EditText recounting
        magBearing.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float magbearing;
                float truebearing;
                float inclination;
                // in case of editText field is cleaned
                if (magBearing.getText().toString().isEmpty()) magbearing = 0;
                else magbearing = Float.parseFloat(magBearing.getText().toString());

                if(magbearing > 360)
                    trueBearing.setError(getString(R.string.errormsg_bearing));
                if(magbearing < 0)
                    trueBearing.setError(getString(R.string.errormsg_bearing_neg));

                // recalculate truebearing
                inclination = mGeomagneticField.getDeclination();
                if (magbearing - inclination < 0)
                    truebearing = magbearing - inclination + 360f;
                else {
                    if (magbearing - inclination > 360)
                        truebearing = magbearing - inclination - 360f;
                    else truebearing = magbearing - inclination;
                }

                trueBearing.setText(String.valueOf(truebearing));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Check azimuth, update compass indicator
        trueBearing.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float bearing;
                // in case of editText field is cleaned
                if (trueBearing.getText().toString().isEmpty()) bearing = 0;
                else bearing = Float.parseFloat(trueBearing.getText().toString());

                if(bearing > 360)
                    trueBearing.setError(getString(R.string.errormsg_bearing));
                if(bearing < 0)
                    trueBearing.setError(getString(R.string.errormsg_bearing_neg));

                compassView.updateAzimuth(bearing);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*PelengEntity nEntity = mViewModel.NewPelengEntity(
                        Double.parseDouble(latitudeView.getText().toString()),
                        Double.parseDouble(longitudeView.getText().toString()),
                        Float.parseFloat(trueBearing.getText().toString()));
                 */

                mViewModel.insert(new Peleng(
                        0,
                        Double.parseDouble(latitudeView.getText().toString()),
                        Double.parseDouble(longitudeView.getText().toString()),
                        Float.parseFloat(trueBearing.getText().toString()),
                        callsignView.getText().toString(),
                        new Date()
                ));


                    Toast.makeText(getActivity(), "Pressed", Toast.LENGTH_SHORT).show();


            }

        });



        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        //createLocationCallback();
        //createLocationRequest();
        //buildLocationSettingsRequest();


        return rootView;
    }



}