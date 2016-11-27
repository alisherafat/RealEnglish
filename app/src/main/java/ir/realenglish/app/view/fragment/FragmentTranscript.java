package ir.realenglish.app.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.view.ActivityLesson;

public class FragmentTranscript extends Fragment {

    @Bind(R.id.txtTranscript) TextView txtTranscript;
    @Bind(R.id.progressBarCenter) ProgressBar progressBarCenter;
    @Bind(R.id.cardView)
    CardView cardView;
    private ActivityLesson parent;

    public FragmentTranscript() {
        // Required empty public constructor
    }

    public static FragmentTranscript newInstance() {
        FragmentTranscript fragment = new FragmentTranscript();
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
        View view = inflater.inflate(R.layout.fragment_transcript, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        progressBarCenter.setVisibility(View.VISIBLE);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = (ActivityLesson) getActivity();
        if (parent.lesson != null && parent.lesson.transcript != null) {
            updateView(new MyEvent.LessonReceived(parent.lesson));
        }
    }

    @Subscribe
    public void updateView(MyEvent.LessonReceived event) {
        progressBarCenter.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
        txtTranscript.setText(event.lesson.transcript);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

}
