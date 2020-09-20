package com.aadumkhor.appyhighsubmission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewFragment extends Fragment {

    // List of Native ads and MenuItems that populate the RecyclerView.
    private List<Object> mRecyclerViewItems;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        mRecyclerViewItems = activity.getRecyclerViewItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        // Specify a linear layout manager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        // Specify an adapter.
        NewsAdapter adapter = new NewsAdapter(mRecyclerViewItems, getContext());
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }
}
