package wildfire.volunteers.smokegator.solo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import wildfire.volunteers.smokegator.solo.R;
import wildfire.volunteers.smokegator.solo.data.Peleng;

public class PelengListAdapter extends RecyclerView.Adapter<PelengListAdapter.PelengViewHolder> {

    private final LayoutInflater mInflater;
    private List<Peleng> mPelengs; // Cached copy of words

    public PelengListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public PelengViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_peleng, parent, false);
        return new PelengViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PelengViewHolder holder, int position) {
        if (mPelengs != null) {
            Peleng current = mPelengs.get(position);
            holder.compassView.updateAzimuth(current.getBearing());
            holder.tvLat.setText(String.format(Locale.US,"%d", current.getLat()));
            holder.tvLng.setText(String.format(Locale.US,"%d", current.getLng()));
            holder.tvCallsign.setText(current.getCallsign());
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            holder.tvTimestamp.setText(dateFormat.format(current.getTimestamp()));


        } else {
            // Covers the case of data not being ready yet.
            holder.compassView.updateAzimuth(0f);
            holder.tvLat.setText("");
            holder.tvLng.setText("");
            holder.tvCallsign.setText("");
            holder.tvTimestamp.setText("");
        }
    }

    void setPelengs(List<Peleng> pelengs){
        mPelengs = pelengs;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPelengs != null)
            return mPelengs.size();
        else return 0;
    }

    class PelengViewHolder extends RecyclerView.ViewHolder {

        private final CompassView compassView;
        private final TextView tvLat;
        private final TextView tvLng;
        private final TextView tvCallsign;
        private final TextView tvTimestamp;

        private PelengViewHolder(View itemView) {
            super(itemView);
            this.compassView = itemView.findViewById(R.id.compass_view);
            this.tvLat = itemView.findViewById(R.id.tvLat);
            this.tvLng = itemView.findViewById(R.id.tvLng);
            this.tvCallsign = itemView.findViewById(R.id.tvCallsign);
            this.tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

        }
    }

}
