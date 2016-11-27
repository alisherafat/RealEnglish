package ir.realenglish.app.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.model.Favorite;
import ir.realenglish.app.model.Lesson;
import ir.realenglish.app.model.Score;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.view.adapter.LessonAdapter;
import ir.realenglish.app.view.component.RecyclerViewEmptySupport;

public class FragmentLessonList extends Fragment {
    public static final int NEW = 1;
    public static final int SEARCH = 2;
    public static final int FAVORITES = 3;

    @Bind(R.id.progressBarBottom)
    ProgressBar progressBarBottom;
    @Bind(R.id.recyclerView) RecyclerViewEmptySupport recyclerView;
    @Bind(R.id.progressBarCenter) ProgressBar progressBarCenter;

    private boolean loading = false;
    private int page = 1, type;
    int lastVisibleItem, visibleItemCount, totalItemCount;
    private boolean hasContinue = true;
    private String url;

    private List<Lesson> lessons = new ArrayList<>();
    private LessonAdapter adapter;
    private LinearLayoutManager layoutManager;

    private EventBus bus = EventBus.getDefault();

    public FragmentLessonList() {
        // Required empty public constructor
    }

    public static FragmentLessonList newInstance(int type) {
        FragmentLessonList fragment = new FragmentLessonList();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

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
        if (!bus.isRegistered(this)) bus.register(this);

        layoutManager = new LinearLayoutManager(getContext());
        adapter = new LessonAdapter(getActivity(), lessons, Score.getAll("lesson"), Favorite.getAll("lesson"));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (hasContinue && !loading) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        lastVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        if ((visibleItemCount + lastVisibleItem) >= totalItemCount - 3) {
                            getLessons();
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (type) {
            case NEW: {
                handleNewLessons();
                break;
            }
            case SEARCH: {
                break;
            }
            case FAVORITES:{
                handleFavoriteLessons();
                break;
            }
        }
    }

    private void handleFavoriteLessons() {
        List<Favorite> favorites = Favorite.getAll("lesson");
        String queryString = "";
        int i = 0;
        for (Favorite favorite : favorites) {
            if (i == 0) {
                queryString += favorite.favoriteableId;
            } else {
                queryString += "," + favorite.favoriteableId;
            }
            i++;
        }
        url = EndPoints.USER_FAVORITE_LESSONS
                .replace("_R1_", String.valueOf(UserService.getId(0)));
        url = url.replace("_R2_", String.valueOf(page));
        url += queryString;
        Utils.log(url);

        getLessons();
    }

    private void handleNewLessons() {
        url = EndPoints.LESSONS_GET.replace("_R_", String.valueOf(page));
        getLessons();
    }

    private void getLessons() {
        if (!hasContinue || loading) return;

        if (lessons.size() > 0) {
            progressBarBottom.setVisibility(View.VISIBLE);
        } else {
            progressBarCenter.setVisibility(View.VISIBLE);
        }
        loading = true;

        Ion.with(getActivity()).load("GET", url)
                .as(new TypeToken<List<Lesson>>() {
                })
                .setCallback(new FutureCallback<List<Lesson>>() {
                    @Override
                    public void onCompleted(Exception e, List<Lesson> result) {
                        try {
                            progressBarBottom.setVisibility(View.GONE);
                            progressBarCenter.setVisibility(View.GONE);
                            loading = false;
                            if (result.size() == 0) {
                                Utils.toast("item not found");
                                hasContinue = false;
                                return;
                            }
                            lessons.addAll(result);
                            adapter.notifyDataSetChanged();
                            page++;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            Utils.toast("Failed");
                        }
                    }
                });
    }

    @Subscribe
    public void onQuerySubmit(MyEvent.SearchSubmit searchSubmit) {
        if (type != SEARCH) return;

        if (searchSubmit.getQuery().length() < 3) {
            Utils.toast("Enter 3 characters");
            return;
        }
        url = EndPoints.LESSONS_SEARCH.replace("_R_", searchSubmit.getQuery());
        hasContinue = true;
        lessons.clear();
        adapter.notifyDataSetChanged();
        getLessons();
    }

    @Override
    public void onDestroyView() {
        if (bus.isRegistered(this)) bus.unregister(this);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
