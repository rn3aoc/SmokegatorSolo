package wildfire.volunteers.smokegator.solo.data;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class PelengClipboard {

    private Context mContext;
    private ClipData mClip;
    private ClipboardManager mClipboard;
    private Peleng mPeleng;
    private String mString;
    private List<String> parseResult;

    // Constructor
    public PelengClipboard(Context context) {
        mContext = context;
        mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    }


    public Peleng getPeleng() {
        mString = getString();
        parseResult = Arrays.asList(mString.split("\\s*,\\s*"));

        try {
            Double lat = Double.parseDouble(parseResult.get(0));
        }
        catch (Exception e) {
            return null;
        }


        mPeleng = new Peleng(0,
                Double.parseDouble(parseResult.get(0)), //latitude
                Double.parseDouble(parseResult.get(1)), //longitude
                Float.parseFloat(parseResult.get(2)), //bearing
                parseResult.get(3), // Collsign
                new Date(),
                parseResult.get(4), // Comment
                true
                );

        return mPeleng;
    }

    public String getString(){
        if (!isReady()) return "Not ready";

        ClipData.Item item = mClipboard.getPrimaryClip().getItemAt(0);
        String string = item.getText().toString();
        return string;
    }


    public boolean isReady() {

        if (!(mClipboard.hasPrimaryClip())) {
            return false;
        }
        else if ((mClipboard.getPrimaryClipDescription().hasMimeType("text/*"))) {
            return true;
        }
        return false;
    }

}


