package wildfire.volunteers.smokegator.solo.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wildfire.volunteers.smokegator.solo.R;
import wildfire.volunteers.smokegator.solo.data.Peleng;
import wildfire.volunteers.smokegator.solo.viewmodel.PelengViewModel;

public class PelengListAdapter extends RecyclerView.Adapter<PelengListAdapter.PelengViewHolder> {

    private final LayoutInflater mInflater;
    private List<Peleng> mPelengs; // Cached copy of words
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private Context mContext;

    public PelengListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public Peleng getPelengAtPosition(int position) {
        return mPelengs.get(position);
    }

    @Override
    public PelengViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_peleng, parent, false);
        return new PelengViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PelengViewHolder holder, int position) {
        if (mPelengs != null) {
            final Peleng current = mPelengs.get(position);
            holder.compassView.updateAzimuth(current.getBearing());
            holder.tvLat.setText(String.format(Locale.US,"%f", current.getLat()));
            holder.tvLng.setText(String.format(Locale.US,"%f", current.getLng()));
            holder.tvCallsign.setText(current.getCallsign());
            //DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            holder.tvTimestamp.setText(dateFormat.format(current.getTimestamp()));

            holder.sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String clippedCallsign; // check whether the callsign is too long
                    if (current.getCallsign().length() > 10) {
                        clippedCallsign = current.getCallsign().substring(0, 9) + "...";
                    }
                    else {
                        clippedCallsign = current.getCallsign();
                    }

                    String envelope = new String(
                            (String.format(Locale.US,"%f, ", current.getLat())) +
                            (String.format(Locale.US,"%f, ", current.getLng())) +
                            (String.format(Locale.US,"%.1f, ", current.getBearing())) +
                            clippedCallsign);

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, envelope);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    holder.sendButton.getContext().startActivity(shareIntent);

                }
            });

        } else {
            // Covers the case of data not being ready yet.
            holder.compassView.updateAzimuth(0f);
            holder.tvLat.setText("");
            holder.tvLng.setText("");
            holder.tvCallsign.setText("");
            holder.tvTimestamp.setText("");
        }


    }

   public void setPelengs(List<Peleng> pelengs){
        mPelengs = pelengs;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mPelengs has not been updated (means initially, it's null, and we can't return null).
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
        private final ImageButton sendButton;
        //private final Button deleteButton;
        //private final Button editButton;

        private PelengViewHolder(View itemView) {
            super(itemView);
            this.compassView = itemView.findViewById(R.id.compass_view);
            this.tvLat = itemView.findViewById(R.id.tvLat);
            this.tvLng = itemView.findViewById(R.id.tvLng);
            this.tvCallsign = itemView.findViewById(R.id.tvCallsign);
            this.tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            this.sendButton = itemView.findViewById(R.id.sendButton);
            //this.deleteButton = itemView.findViewById(R.id.deleteButton);
            //this.editButton = itemView.findViewById(R.id.editButton);

        }



    }



}
