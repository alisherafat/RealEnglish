package ir.realenglish.app.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.view.adapter.PostItemAdapter;
import ir.realenglish.app.model.Post;
import ir.realenglish.app.view.activity.ContainerActivity;
import ir.realenglish.app.view.activity.ActivityPost;

public class FragmentPostItems extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARAM1 = "param1";
    private ActivityPost parent;
    @Bind(R.id.txtTitle) TextView txtTitle;
    @Bind(R.id.txtDesc) TextView txtDesc;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    private List<Post.Item> itemList = new ArrayList<>();
    private EventBus bus = EventBus.getDefault();
    private PostItemAdapter adapter;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = (ActivityPost) getActivity();
        txtTitle.setText(parent.post.title);
        txtDesc.setText(parent.post.description);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new PostItemAdapter(getContext(), parent.post, false);
        recyclerView.setAdapter(adapter);
        Collections.sort(parent.itemList, new Comparator<Post.Item>() {
            @Override
            public int compare(Post.Item first, Post.Item second) {
                return first.sort > second.sort ? 1 : -1;
            }
        });
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    @Subscribe
    public void idiomReceived(MyEvent.PostDataReceived eventPostDataReceived) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // idiom = (Idiom) getArguments().getSerializable(PARAM1);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.idiom_show, menu);
        //menu.findItem(R.id.item_edit).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit:
                Intent intent = new Intent(getContext(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.TYPE, ContainerActivity.IDIOM_EDIT);
                intent.putExtra("post", parent.post);
                getActivity().startActivity(intent);
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_idiom, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        bus.register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bus.unregister(this);
        ButterKnife.unbind(this);
    }

    public FragmentPostItems() {
        // Required empty public constructor
    }

    public static FragmentPostItems newInstance(Post param1) {
        FragmentPostItems fragment = new FragmentPostItems();
        Bundle args = new Bundle();
        args.putSerializable(PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        adapter.releaseMediaPlayer();
        super.onDestroy();
    }
}
