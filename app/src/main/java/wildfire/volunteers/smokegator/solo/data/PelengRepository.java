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

    public void update(Peleng peleng) {new updatePelengAsyncTask(mPelengDao).execute(peleng);}
    private static class updatePelengAsyncTask extends AsyncTask<Peleng, Void, Void> {
        private PelengDao mAsyncTaskDao;
        updatePelengAsyncTask(PelengDao dao) {mAsyncTaskDao = dao;}
        @Override
        protected Void doInBackground(final Peleng... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    public void deletePeleng(Peleng peleng) { new deletePelengAsyncTask(mPelengDao).execute(peleng); }
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

    public void setVisible(Peleng peleng){ new setVisibleAsyncTask(mPelengDao).execute(peleng); }
    private static class setVisibleAsyncTask extends AsyncTask<Peleng, Void, Void> {
        private PelengDao mAsyncTaskDao;
        setVisibleAsyncTask(PelengDao dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Peleng... params) {
            int localid = params[0].getLocalid();
            mAsyncTaskDao.setVisible(localid);
            return null;
        }
    }

    public void setInvisible(Peleng peleng) { new setInvisibleAsyncTask(mPelengDao).execute(peleng); }
    private static class setInvisibleAsyncTask extends AsyncTask<Peleng, Void, Void> {
        private PelengDao mAsyncTaskDao;
        setInvisibleAsyncTask(PelengDao dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Peleng... params) {
            int localid = params[0].getLocalid();
            mAsyncTaskDao.setInvisible(localid);
            return null;
        }
    }
}
