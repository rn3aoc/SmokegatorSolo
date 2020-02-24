package wildfire.volunteers.smokegator.solo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import wildfire.volunteers.smokegator.solo.data.Peleng;
import wildfire.volunteers.smokegator.solo.data.PelengRepository;

public class PelengViewModel extends AndroidViewModel {

    private PelengRepository mRepository;
    private LiveData<List<Peleng>> mAllPelengs;

    public PelengViewModel (Application application) {
        super(application);
        mRepository = new PelengRepository(application);
        mAllPelengs = mRepository.getAllPelengs();
    }

    public LiveData<List<Peleng>> getAllPelengs() { return mAllPelengs; }

    public void insert(Peleng peleng) { mRepository.insert(peleng); }

}
