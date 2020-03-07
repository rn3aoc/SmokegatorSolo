package wildfire.volunteers.smokegator.solo.ui.arpelengator;


import android.hardware.GeomagneticField;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import java.util.Date;

import wildfire.volunteers.smokegator.solo.R;
import wildfire.volunteers.smokegator.solo.viewmodel.PelengViewModel;

public class ViewfinderFragment extends Fragment {

    private int REQUEST_CODE_PERMISSIONS = 10; //arbitrary number, can be changed accordingly

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"}; //array w/ permissions from manifest

    TextureView txView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        return rootView;
}
