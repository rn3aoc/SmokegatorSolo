package wildfire.volunteers.smokegator.solo.ui.pelengator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PelengatorViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PelengatorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}