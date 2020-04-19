package wildfire.volunteers.smokegator.solo.ui.pelengator;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

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
import wildfire.volunteers.smokegator.solo.data.PelengClipboard;
import wildfire.volunteers.smokegator.solo.ui.CompassView;
import wildfire.volunteers.smokegator.solo.viewmodel.PelengViewModel;

import static android.content.Context.MODE_PRIVATE;

public class PelengatorFragment extends Fragment {

    private static final String TAG = "PelengatorFragment";
    private static final String MAP_PREFS_NAME = "mapCameraState";



    // Local geomagnetic inclination object.
    private GeomagneticField mGeomagneticField = new GeomagneticField(
            0,
            0,
            0,
            new Date().getTime());

    // UI Widgets.

    private ToggleButton GPSToggleButton;
    private EditText latitudeView;
    private EditText longitudeView;
    private EditText magBearing;
    private EditText trueBearing;
    private EditText callsignView;
    private TextView inclinationView;
    private CompassView compassView;
    private EditText commentView;
    private Button sendButton;
    private Button clipboardButton;

    private PelengViewModel mViewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences mapStatePrefs;
    private String callsign;
    private float storedLat;
    private float storedLng;
    private Peleng clipboardPeleng;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mapStatePrefs = getActivity().getSharedPreferences(MAP_PREFS_NAME, Context.MODE_PRIVATE);
        callsign = sharedPreferences.getString("callsign", "default");
        storedLat = mapStatePrefs.getFloat("latitude", 0f);
        storedLng = mapStatePrefs.getFloat("longitude", 0f);

        mGeomagneticField = new GeomagneticField(
                storedLat,
                storedLng,
                0,
                new Date().getTime());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.fragment_pelengator, container, false);

        latitudeView = rootView.findViewById(R.id.editTextLat);
        longitudeView = rootView.findViewById(R.id.editTextLng);
        magBearing = rootView.findViewById(R.id.editTextMBearing);
        trueBearing = rootView.findViewById(R.id.editTextTBearing);
        inclinationView = rootView.findViewById(R.id.inclinationTextView);
        compassView = rootView.findViewById(R.id.compassView);
        callsignView = rootView.findViewById(R.id.callsignEditText);
        commentView = rootView.findViewById(R.id.commentEditText);
        sendButton = (Button) rootView.findViewById(R.id.sendButton);
        clipboardButton = rootView.findViewById(R.id.clipboardButton);

        mViewModel = new ViewModelProvider(this).get(PelengViewModel.class);

        // Callsign default from preferences
        callsignView.setText(callsign);

        // Initial inclination from stored position
        inclinationView.setText(String.valueOf(mGeomagneticField.getDeclination()));

        // Check latitude input
        latitudeView.setText(String.valueOf(storedLat));
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
        longitudeView.setText(String.valueOf(storedLng));
        longitudeView.addTextChangedListener(new TextWatcher() {
            double latitude;
            double longitude;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (latitudeView.getText().toString().isEmpty()) latitude = 0;
                if (longitudeView.getText().toString().isEmpty()) longitude = 0;
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
                if (magbearing + inclination < 0)
                    truebearing = magbearing + inclination + 360f;
                else {
                    if (magbearing + inclination > 360)
                        truebearing = magbearing + inclination - 360f;
                    else truebearing = magbearing + inclination;
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
                float bearing;
                if (trueBearing.getText().toString().isEmpty()) bearing = 0;
                else bearing = Float.parseFloat(trueBearing.getText().toString());

                mViewModel.insert(new Peleng(
                        0,
                        Double.parseDouble(latitudeView.getText().toString()),
                        Double.parseDouble(longitudeView.getText().toString()),
                        bearing,
                        callsignView.getText().toString(),
                        new Date(),
                        commentView.getText().toString(),
                        true
                ));

                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();

                Navigation.findNavController(rootView).navigate(R.id.action_global_nav_map);
            }
        });

        clipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try { clipboardPeleng = new PelengClipboard(getContext()).getPeleng(); }
                catch (Exception e){ Toast.makeText(getActivity(), "Wrong data in clipboard!", Toast.LENGTH_SHORT).show(); }

                try { latitudeView.setText(String.valueOf(clipboardPeleng.getLat()));}
                catch (Exception e) { Toast.makeText(getActivity(), "Wrong data in clipboard! \n Lat not found.", Toast.LENGTH_SHORT).show(); }

                try { longitudeView.setText(String.valueOf(clipboardPeleng.getLng()));}
                catch (Exception e) { Toast.makeText(getActivity(), "Wrong data in clipboard! \n Lng not found.", Toast.LENGTH_SHORT).show(); }

                try { trueBearing.setText(String.valueOf(clipboardPeleng.getBearing()));}
                catch (Exception e) { Toast.makeText(getActivity(), "Wrong data in clipboard! \n Bearing not found.", Toast.LENGTH_SHORT).show(); }

                try { callsignView.setText(clipboardPeleng.getCallsign());}
                catch (Exception e) { Toast.makeText(getActivity(), "Wrong data in clipboard! \n Callsign not found.", Toast.LENGTH_SHORT).show(); }

                try { if (clipboardPeleng.getComment().equals("no comment")) {commentView.setText("");}
                    else {commentView.setText(clipboardPeleng.getComment());}}
                catch (Exception e) { Toast.makeText(getActivity(), "Wrong data in clipboard! \n Comment parsing error. ", Toast.LENGTH_SHORT).show(); }

            }
        });

        if (new PelengClipboard(getContext()).isReady()){
            clipboardButton.setClickable(true);
            clipboardButton.setEnabled(true);
        } else {
            clipboardButton.setClickable(false);
            clipboardButton.setEnabled(false);
        }

        //testTextView.setText(new PelengClipboard(getContext()).getString());


        return rootView;
    }
}