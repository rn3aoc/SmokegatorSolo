package wildfire.volunteers.smokegator.solo.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PelengDao {

    @Insert
    void insert(Peleng peleng);

    @Query("DELETE FROM peleng_table")
    void deleteAll();

    @Query("SELECT * from peleng_table ORDER BY timestamp DESC") // the newest pelengs first
    LiveData<List<Peleng>> getAllPelengs();

    @Query("SELECT * from peleng_table WHERE visibility IS 'true' ORDER BY timestamp DESC")
    LiveData<List<Peleng>> getAllVisiblePelengs();

    @Delete
    void deletePeleng(Peleng peleng);
}
