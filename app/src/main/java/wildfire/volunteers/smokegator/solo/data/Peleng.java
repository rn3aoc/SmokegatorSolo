package wildfire.volunteers.smokegator.solo.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

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
    @ColumnInfo(name = "comment")
    private String mComment;
    @NonNull
    @ColumnInfo(name = "visibility")
    private boolean mVisibility;

    public Peleng(int localid,
                  @NonNull double lat,
                  @NonNull double lng,
                  @NonNull float bearing,
                  @NonNull String callsign,
                  @NonNull Date timestamp,
                  String comment,
                  boolean visibility) {
        this.localid = localid;
        this.mLat = lat;
        this.mLng = lng;
        this.mBearing = bearing;
        this.mCallsign = callsign;
        this.mTimestamp = timestamp;
        this.mComment = comment;
        this.mVisibility = visibility;
    }

    // Getters for Room database
    public int getLocalid(){return this.localid;}
    public double getLat(){return this.mLat;}
    public double getLng(){return this.mLng;}
    public float getBearing(){return this.mBearing;}
    public String getCallsign(){return this.mCallsign;}
    public Date getTimestamp(){return this.mTimestamp;}
    public String getComment(){return this.mComment;}
    public boolean getVisibility(){return this.mVisibility;}

    public LatLng getLatLng(){return new LatLng(this.mLat, this.mLng);}
}
