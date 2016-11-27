package ir.realenglish.app.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.view.ActivityLesson;
import ir.realenglish.app.view.adapter.WordAdapter;

public class FragmentVocabulary extends Fragment {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.progressBarCenter) ProgressBar progressBarCenter;
    private ActivityLesson parent;
    private WordAdapter adapter;

    public FragmentVocabulary() {
        // Required empty public constructor
    }

    public static FragmentVocabulary newInstance() {
        FragmentVocabulary fragment = new FragmentVocabulary();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        ButterKnife.bind(this, view);
        progressBarCenter.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = (ActivityLesson) getActivity();
       updateView();
    }

    public void updateView() {
        if (parent.lesson != null && parent.lesson.words != null) {
            progressBarCenter.setVisibility(View.GONE);
            adapter = new WordAdapter(getContext(), parent.lesson.words);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
