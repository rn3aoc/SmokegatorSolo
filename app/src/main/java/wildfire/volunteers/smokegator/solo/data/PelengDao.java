package wildfire.volunteers.smokegator.solo.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PelengDao {

    @Insert
    void insert(Peleng peleng);

    @Update
    void update(Peleng peleng);

    @Query("DELETE FROM peleng_table")
    void deleteAll();

    @Query("UPDATE peleng_table SET visibility = 1 WHERE localid = :localid")
    void setVisible(int localid);

    @Query("UPDATE peleng_table SET visibility = 0 WHERE localid = :localid")
    void setInvisible(int localid);

    @Query("SELECT * from peleng_table ORDER BY timestamp DESC") // the newest pelengs first
    LiveData<List<Peleng>> getAllPelengs();

    @Query("SELECT * from peleng_table WHERE visibility IS 'true' ORDER BY timestamp DESC")
    LiveData<List<Peleng>> getAllVisiblePelengs();

    @Delete
    void deletePeleng(Peleng peleng);
}
