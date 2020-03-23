package wildfire.volunteers.smokegator.solo.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PelengRepository {

    private PelengDao mPelengDao;
    private LiveData<List<Peleng>> mAllPelengs;
    private LiveData<List<Peleng>> mAllVisiblePelengs;

    public PelengRepository(Application application) {
        PelengRoomDatabase db = PelengRoomDatabase.getDatabase(application);
        mPelengDao = db.pelengDao();
        mAllPelengs = mPelengDao.getAllPelengs();
        mAllVisiblePelengs = mPelengDao.getAllVisiblePelengs();

    }

    public LiveData<List<Peleng>> getAllPelengs() {
        return mAllPelengs;
    }

    public LiveData<List<Peleng>> getAllVisiblePelengs() { return mAllVisiblePelengs; }

    public void insert (Peleng peleng) {
        new insertAsyncTask(mPelengDao).execute(peleng);
    }

    private static class insertAsyncTask extends AsyncTask<Peleng, Void, Void> {

        private PelengDao mAsyncTaskDao;

        insertAsyncTask(PelengDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Peleng... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deletePelengAsyncTask extends AsyncTask<Peleng, Void, Void> {
        private PelengDao mAsyncTaskDao;

        deletePelengAsyncTask(PelengDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Peleng... params) {
            mAsyncTaskDao.deletePeleng(params[0]);
            return null;
        }
    }

    public void deletePeleng(Peleng peleng)  {
        new deletePelengAsyncTask(mPelengDao).execute(peleng);
    }
}
