package wildfire.volunteers.smokegator.solo.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

@Database(entities = {Peleng.class}, version = 2, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class PelengRoomDatabase extends RoomDatabase {

    public abstract PelengDao pelengDao();
    private static PelengRoomDatabase INSTANCE;

    public static PelengRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PelengRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PelengRoomDatabase.class, "peleng_database")
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}


