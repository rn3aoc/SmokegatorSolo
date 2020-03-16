package wildfire.volunteers.smokegator.solo.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.github.pengrad.mapscaleview.MapScaleView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import wildfire.volunteers.smokegator.solo.R;
import wildfire.volunteers.smokegator.solo.data.Peleng;
import wildfire.volunteers.smokegator.solo.ui.MarkerIcon;
import wildfire.volunteers.smokegator.solo.ui.ScalebarView;
import wildfire.volunteers.smokegator.solo.ui.pelengator.PelengatorFragment;
import wildfire.volunteers.smokegator.solo.viewmodel.PelengViewModel;

import static com.google.maps.android.SphericalUtil.computeOffset;


public class MapFragment extends Fragment {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private PelengViewModel mViewModel;
    private SharedPreferences sharedPreferences;
    private GoogleMap googleMap;
    private MapScaleView mapScaleView;
    private CameraPosition cameraPosition;
    private TextView cameraLatView;
    private TextView cameraLngView;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }

    }

    // empty constructor needed for SupportMapFragment
    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(PelengViewModel.class);

        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapScaleView = rootView.findViewById(R.id.scaleView);

        cameraLatView = rootView.findViewById(R.id.cameraLatTextView);
        cameraLngView = rootView.findViewById(R.id.cameraLngTextView);

        SupportMapFragment googleMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        googleMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {
                googleMap = map;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.clear();
                if (checkLocationPermission()) {
                    map.setMyLocationEnabled(true);
                }
                //googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                switch (sharedPreferences.getString("maptype", "")) {
                    case "1":
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case "2":
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case "3":
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case "4":
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;

                }

                MapStateManager mapStateManager = new MapStateManager(getContext());
                CameraPosition position = mapStateManager.getSavedCameraPosition();
                if (position != null) {
                    CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                    // Toast.makeText(getContext(), "entering Resume State", Toast.LENGTH_SHORT).show();
                    googleMap.moveCamera(update);

                }

                // Store current map position (for pelengator), update scale
                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener(){
                    @Override
                    public void onCameraMove() {
                        MapStateManager mapStateManager = new MapStateManager(getContext());
                        mapStateManager.saveMapState(googleMap);
                        cameraPosition = googleMap.getCameraPosition();
                        mapScaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
                        cameraLatView.setText(String.format(Locale.US,"%f", cameraPosition.target.latitude));
                        cameraLngView.setText(String.format(Locale.US,"%f", cameraPosition.target.longitude));
                    }
                });
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                            MapStateManager mapStateManager = new MapStateManager(getContext());
                            mapStateManager.saveMapState(googleMap);
                            cameraPosition = googleMap.getCameraPosition();
                            mapScaleView.update(cameraPosition.zoom, cameraPosition.target.latitude);
                    }
                });


                mViewModel.getAllPelengs().observe(getActivity(), new Observer<List<Peleng>>() {
                    @Override
                    public void onChanged(List<Peleng> pelengs) {
                        drawMarkers(googleMap, pelengs);
                    }
                });
            }


            public void drawMarkers(GoogleMap googleMap, List<Peleng> mPelengs){

                List<PatternItem> bgPattern = Arrays.<PatternItem>asList(
                        new Gap(3), new Dash(17));
                List<PatternItem> dotPattern = Arrays.<PatternItem>asList(
                        new Dash(3), new Gap(17));


                for (int i = 0; i < mPelengs.size(); i++) {
                    Marker mMarker = googleMap.addMarker(new MarkerOptions()
                            .position(mPelengs.get(i).getLatLng())
                            .anchor(0.5f, 287f/300f) // hardcoded icon size
                            .flat(true)
                            .rotation(mPelengs.get(i).getBearing())
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.peleng_darkred_30px))
                            .icon(BitmapDescriptorFactory.fromBitmap(new MarkerIcon(
                                    mPelengs.get(i).getBearing(),
                                    mPelengs.get(i).getTimestamp(),
                                    mPelengs.get(i).getCallsign()).getBitmap()))
                            .alpha(0.8f));
                    Polyline mPolyline = googleMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .width(3)
                            .color(sharedPreferences.getInt("normal_line_color", Context.MODE_PRIVATE))
                            .add(
                                    mPelengs.get(i).getLatLng(),
                                    pelengToLatLng(mPelengs.get(i).getLatLng(), mPelengs.get(i).getBearing())
                            ));
                    Polyline mPolylineDots = googleMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .width(3)
                            .color(Color.WHITE)
                            .add(
                                    mPelengs.get(i).getLatLng(),
                                    pelengToLatLng(mPelengs.get(i).getLatLng(), mPelengs.get(i).getBearing())
                            ));
                    mPolyline.setPattern(bgPattern);
                    mPolylineDots.setPattern(dotPattern);

                    Polygon mPolygon = googleMap.addPolygon(new PolygonOptions()
                            .add(
                                    mPelengs.get(i).getLatLng(),
                                    pelengToLatLng(mPelengs.get(i).getLatLng(), mPelengs.get(i).getBearing()+2.5f),
                                    pelengToLatLng(mPelengs.get(i).getLatLng(), mPelengs.get(i).getBearing()-2.5f)
                            )
                            .fillColor(sharedPreferences.getInt("normal_sector_color", Context.MODE_PRIVATE))
                            .strokeWidth(0f)
                    );
                }
            }

            private LatLng pelengToLatLng(LatLng mLatLng, float mBearing){
                double distance = sharedPreferences.getInt("pelenglength", 15)*500; // 0-50 km
                return computeOffset(mLatLng, distance, mBearing);
            }
        });


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        MapStateManager mapStateManager = new MapStateManager(getContext());
        mapStateManager.saveMapState(googleMap);
    }


    public class MapStateManager {

        private static final String LONGITUDE = "longitude";
        private static final String LATITUDE = "latitude";
        private static final String ZOOM = "zoom";
        private static final String BEARING = "bearing";
        private static final String TILT = "tilt";
        private static final String PREFS_NAME ="mapCameraState";
        private SharedPreferences mapStatePrefs;

        MapStateManager(Context context) {
            mapStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

        void saveMapState(GoogleMap googleMap) {
            SharedPreferences.Editor editor = mapStatePrefs.edit();
            CameraPosition position = googleMap.getCameraPosition();

            editor.putFloat(LATITUDE, (float) position.target.latitude);
            editor.putFloat(LONGITUDE, (float) position.target.longitude);
            editor.putFloat(ZOOM, position.zoom);
            editor.putFloat(TILT, position.tilt);
            editor.putFloat(BEARING, position.bearing);
            editor.apply();
        }

        public CameraPosition getSavedCameraPosition() {
            double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
            if (latitude == 0) {
                return null;
            }
            double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
            LatLng target = new LatLng(latitude, longitude);

            float zoom = mapStatePrefs.getFloat(ZOOM, 0);
            float bearing = mapStatePrefs.getFloat(BEARING, 0);
            float tilt = mapStatePrefs.getFloat(TILT, 0);

            CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
            return position;
        }

    }



}