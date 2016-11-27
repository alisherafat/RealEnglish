package ir.realenglish.app.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.model.PushNotification;
import ir.realenglish.app.view.adapter.PushNotificationAdapter;
import ir.realenglish.app.view.component.RecyclerViewEmptySupport;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPushList extends Fragment {

    @Bind(R.id.recyclerView) RecyclerViewEmptySupport recyclerView;
    @Bind(R.id.progressBarCenter) ProgressBar progressBarCenter;
    @Bind(R.id.txtEmptyView) TextView txtEmptyView;

    private List<PushNotification> items;
    private PushNotificationAdapter adapter;

    public FragmentPushList() {
        // Required empty public constructor
    }

    public static FragmentPushList newInstance() {
        FragmentPushList fragment = new FragmentPushList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setEmptyView(txtEmptyView);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getData();
        adapter = new PushNotificationAdapter(getContext(), items);
        RecyclerViewSwipeManager swipeMgr = new RecyclerViewSwipeManager();
        recyclerView.setAdapter(swipeMgr.createWrappedAdapter(adapter));
        swipeMgr.attachRecyclerView(recyclerView);
    }

    private void getData() {
        this.items = new Select().from(PushNotification.class).execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
