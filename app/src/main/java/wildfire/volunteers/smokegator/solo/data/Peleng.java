package wildfire.volunteers.smokegator.solo.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "peleng_table")
public class Peleng {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "localid")
    private int localid;
    @NonNull
    @ColumnInfo(name = "lat")
    private double mLat;
    @NonNull
    @ColumnInfo(name = "lng")
    private double mLng;
    @NonNull
    @ColumnInfo(name = "bearing")
    private float mBearing;
    @NonNull
    @ColumnInfo(name = "callsign")
    private String mCallsign;
    @NonNull
    @ColumnInfo(name = "timestamp")
    private Date mTimestamp;

    public Peleng(int localid,
                  @NonNull double lat,
                  @NonNull double lng,
                  @NonNull float bearing,
                  @NonNull String callsign,
                  @NonNull Date timestamp) {
        this.localid = localid;
        this.mLat = lat;
        this.mLng = lng;
        this.mBearing = bearing;
        this.mCallsign = callsign;
        this.mTimestamp = timestamp;
    }

    // Getters for Room database
    public int getLocalid(){return this.localid;}
    public double getLat(){return this.mLat;}
    public double getLng(){return this.mLng;}
    public float getBearing(){return this.mBearing;}
    public String getCallsign(){return this.mCallsign;}
    public Date getTimestamp(){return this.mTimestamp;}
}
