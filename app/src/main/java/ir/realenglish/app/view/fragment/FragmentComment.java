package ir.realenglish.app.view.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.app.MyApp;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.model.Comment;
import ir.realenglish.app.model.Score;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.view.ActivityLesson;
import ir.realenglish.app.view.activity.ActivityPost;
import ir.realenglish.app.view.adapter.CommentAdapter;

public class FragmentComment extends Fragment {
    private final String TYPE = "type";
    public static final int POST_COMMENTS = 1;
    public static final int USER_COMMENTS = 2;
    public static final int LESSON_COMMENTS = 3;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.progressBarCenter) ProgressBar progressBarCenter;
    @Bind(R.id.progressBarBottom) ProgressBar progressBarBottom;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.txtEmptyView) TextView txtEmpty;
    CommentAdapter adapter;
    private EventBus bus = EventBus.getDefault();
    private int type = 1;
    private List<Comment> comments = new ArrayList<>();

    private boolean isReceived;
    private int parentId;
    private String parentType;

    public FragmentComment() {
        // Required empty public constructor
    }

    public static FragmentComment newInstance(int type) {
        FragmentComment fragment = new FragmentComment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    /*
        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof OnCommentsInteraction) {
                listener = (OnCommentsInteraction) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnCommentsInteraction");
            }
        }
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (type) {
            case USER_COMMENTS: {
                adapter = new CommentAdapter(getContext(), comments, Score.getAll("comment"), UserService.getId(0));
                getUserComments();
                break;
            }

            case POST_COMMENTS: {
                fab.show();
                progressBarCenter.setVisibility(View.VISIBLE);
                ActivityPost parent = (ActivityPost) getActivity();
                parentId = parent.post.remoteId;
                parentType = "post";
                adapter = new CommentAdapter(getContext(), comments, Score.getAll("comment"), UserService.getId(0));
                getPostComments();
                break;
            }

            case LESSON_COMMENTS: {
                fab.show();
                progressBarCenter.setVisibility(View.VISIBLE);
                ActivityLesson parent = (ActivityLesson) getActivity();
                parentId = parent.lesson.remoteId;
                parentType = "lesson";
                if (adapter == null)
                    adapter = new CommentAdapter(getContext(), comments, Score.getAll("comment"), UserService.getId(0));
                if (!isReceived)
                    getLessonComments();
                break;
            }
        }

        recyclerView.setAdapter(adapter);
        if (isReceived) {
            notifyReceived();
        }
        if (!bus.isRegistered(this)) {
            bus.register(this);
        }
    }

    private void getPostComments() {
        Ion.with(getActivity()).load("GET", EndPoints.POST_COMMENTS.replace("_R_", String.valueOf(parentId)))
                .as(new TypeToken<List<Comment>>() {
                }).setCallback(new FutureCallback<List<Comment>>() {
            @Override
            public void onCompleted(Exception e, List<Comment> result) {
                try {
                    isReceived = true;
                    comments.addAll(result);
                    notifyReceived();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void getLessonComments() {
        Ion.with(getActivity()).load("GET", EndPoints.LESSON_COMMENTS.replace("_R1_", String.valueOf(parentId)))
                .as(new TypeToken<List<Comment>>() {
                })
                .setCallback(new FutureCallback<List<Comment>>() {
                    @Override
                    public void onCompleted(Exception e, List<Comment> result) {
                        try {
                            isReceived = true;
                            comments.addAll(result);
                            notifyReceived();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    private void removeProgressBars() {
        progressBarBottom.setVisibility(View.GONE);
        progressBarCenter.setVisibility(View.GONE);
    }

    private void getUserComments() {
        if (comments.size() > 0) {
            progressBarBottom.setVisibility(View.VISIBLE);
        } else {
            progressBarCenter.setVisibility(View.VISIBLE);
        }
        Ion.with(getContext()).load("GET", EndPoints.USER_COMMENTS.replace("_R1_", String.valueOf(UserService.getId(0))))
                .as(new TypeToken<List<Comment>>() {
                }).setCallback(new FutureCallback<List<Comment>>() {
            @Override
            public void onCompleted(Exception e, List<Comment> result) {
                progressBarBottom.setVisibility(View.GONE);
                progressBarCenter.setVisibility(View.GONE);
                try {
                    comments.addAll(result);
                    adapter.notifyDataSetChanged();
                    checkItemSize();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Subscribe
    public void onCommentsReceive(MyEvent.PostCommentsReceive eventPostCommentsReceive) {

    }

    private void notifyReceived() {
        removeProgressBars();
        adapter.notifyDataSetChanged();
        checkItemSize();
    }

    private void checkItemSize() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            txtEmpty.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.fab)
    public void onFabClicked() {
        new MaterialDialog.Builder(getContext()).title("Comment").positiveText("Send").negativeText("cancel")
                .icon(new IconicsDrawable(getContext())
                        .icon(GoogleMaterial.Icon.gmd_chat).color(Color.DKGRAY).sizeDp(24))
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(5, 255)
                .input("", null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input == null || input.toString().trim().isEmpty()) {
                            return;
                        }
                        Comment.send(MyApp.getInstance(), fab, parentType, parentId, input.toString().trim());

                    }
                }).build().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (bus.isRegistered(this)) bus.unregister(this);
    }



    /*
    public interface OnCommentsInteraction {
        void getComments(int page);
        void sendComment(String body);
    }



    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    */
}
