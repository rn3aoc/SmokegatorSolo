package wildfire.volunteers.smokegator.solo.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wildfire.volunteers.smokegator.solo.R;
import wildfire.volunteers.smokegator.solo.data.Peleng;
import wildfire.volunteers.smokegator.solo.ui.PelengListAdapter;
import wildfire.volunteers.smokegator.solo.viewmodel.PelengViewModel;

public class ListFragment extends Fragment {
    /*
    private ListViewModel listViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                ViewModelProviders.of(this).get(ListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        listViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;

    }

     */

    private PelengViewModel pelengViewModel;
    PelengListAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        //PelengListAdapter adapter = new PelengListAdapter(getActivity());
        adapter = new PelengListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pelengViewModel = ViewModelProviders.of(getActivity()).get(PelengViewModel.class);
        pelengViewModel.getAllPelengs().observe(getActivity(), new Observer<List<Peleng>>(){
            @Override
            public void onChanged(@Nullable final List<Peleng> pelengs){
                adapter.setPelengs(pelengs);
            }
        } );



        return rootView;
    }
}