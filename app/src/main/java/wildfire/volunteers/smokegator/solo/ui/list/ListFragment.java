package wildfire.volunteers.smokegator.solo.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wildfire.volunteers.smokegator.solo.R;
import wildfire.volunteers.smokegator.solo.data.Peleng;
import wildfire.volunteers.smokegator.solo.ui.PelengListAdapter;
import wildfire.volunteers.smokegator.solo.viewmodel.PelengViewModel;

public class ListFragment extends Fragment {

    private PelengViewModel pelengViewModel;
    PelengListAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        adapter = new PelengListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pelengViewModel = new ViewModelProvider(getActivity()).get(PelengViewModel.class);
        pelengViewModel.getAllPelengs().observe(getActivity(), new Observer<List<Peleng>>(){
            @Override
            public void onChanged(@Nullable final List<Peleng> pelengs){
                adapter.setPelengs(pelengs);
            }
        } );


        // User gestures recognition
        ItemTouchHelper helperDelete = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Peleng mPeleng = adapter.getPelengAtPosition(position);
                        pelengViewModel.deletePeleng(mPeleng);

                        Toast.makeText(getActivity(), "Deleting " +
                                mPeleng.getLocalid(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        ItemTouchHelper helperVisibility = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Peleng mPeleng = adapter.getPelengAtPosition(position);
                        pelengViewModel.togglePelengVisibility(mPeleng);


                        Toast.makeText(getActivity(), "Visibility changed ", Toast.LENGTH_LONG).show();
                    }
                }
        );

        helperDelete.attachToRecyclerView(recyclerView);
        helperVisibility.attachToRecyclerView(recyclerView);

        return rootView;
    }
}