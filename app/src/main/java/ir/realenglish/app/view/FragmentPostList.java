package ir.realenglish.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.model.Favorite;
import ir.realenglish.app.model.Post;
import ir.realenglish.app.model.Score;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.view.activity.ContainerActivity;
import ir.realenglish.app.view.adapter.PostListAdapter;

public class FragmentPostList extends Fragment {
    public static final int NEW = 1;
    public static final int USER_POST_LIST = 2;
    public static final int SEARCH = 3;
    public static final int FAVORITE_POST = 4;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBarBottom)
    ProgressBar progressBarBottom;
    @Bind(R.id.progressBarCenter) ProgressBar progressBarCenter;
    @Bind(R.id.fab) FloatingActionButton fab;
    //@Bind(R.id.search_view)
    List<Post> postList;
    PostListAdapter adapter;
    LinearLayoutManager layoutManager;
    private boolean loading = false;
    private int page = 1, type;
    int lastVisibleItem, visibleItemCount, totalItemCount;
    private boolean hasContinue = true, isFirstLoad = true;
    String url;
    private MaterialSearchView searchView;
    private EventBus bus = EventBus.getDefault();

    public static FragmentPostList newInstance(int type) {
        FragmentPostList fragment = new FragmentPostList();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_idiom, container, false);
        ButterKnife.bind(this, view);
        if (!bus.isRegistered(this)) bus.register(this);
        setHasOptionsMenu(true);
        return view;
    }

    @OnClick(R.id.fab)
    public void fabClick() {
        Intent intent = new Intent(getContext(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.TYPE, ContainerActivity.IDIOM_ADD);
        startActivity(intent);
        ((BaseActivity) getActivity()).slideToRightTransition();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab.hide();

        postList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());

        adapter = new PostListAdapter(getContext(), postList, Score.getAll("post"), Favorite.getAll("post"));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        switch (type) {
            case NEW:
                handleNewPosts();
                break;
            case SEARCH:
                handleSearchPosts();
                break;
            case USER_POST_LIST:
                handleUserPostList();
                break;
            case FAVORITE_POST:
                handleFavoritePosts();
                break;
        }
    }

    private void handleFavoritePosts() {
        List<Favorite> favorites = Favorite.getAll("post");
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
        url = EndPoints.USER_POST_FAVORITES
                .replace("_R1_", String.valueOf(UserService.getId(0)));
        url = url.replace("_R2_", String.valueOf(page));
        url = url.replace("_R3_", queryString);
        getPosts();
    }

    private void handleUserPostList() {
        url = EndPoints.USER_POSTS.replace("_R2_", String.valueOf(page))
                .replace("_R1_", String.valueOf(UserService.getId(0)));
        getPosts();
    }


    private void handleSearchPosts() {
    }

    private void handleNewPosts() {
        url = EndPoints.POST_BASE.replace("_R_", String.valueOf(page));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (hasContinue && !loading) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        lastVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        if ((visibleItemCount + lastVisibleItem) >= totalItemCount - 3) {
                            getPosts();
                        }
                    }
                }
            }
        });
        getPosts();
    }


    public void getPosts() {
        if(!hasContinue || loading) return;

        if (postList.size() > 0) {
            progressBarBottom.setVisibility(View.VISIBLE);
        } else {
            progressBarCenter.setVisibility(View.VISIBLE);
        }
        loading = true;

        Ion.with(getContext()).load("GET", url)
                .as(new TypeToken<List<Post>>() {
                }).setCallback(new FutureCallback<List<Post>>() {
            @Override
            public void onCompleted(Exception e, List<Post> result) {
                try {
                    progressBarBottom.setVisibility(View.GONE);
                    progressBarCenter.setVisibility(View.GONE);
                    loading = false;
                    if (result == null) {
                        Utils.toast("connection failed");
                        return;
                    }
                    if (result.size() == 0) {
                        Utils.toast("item not found");
                        hasContinue = false;
                        return;
                    }
                    postList.addAll(result);
                    adapter.notifyDataSetChanged();
                    page++;
                } catch (Exception e1) {
                    e1.printStackTrace();
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
        url = EndPoints.POST_SEARCH.replace("_R_", searchSubmit.getQuery());
        hasContinue = true;
        postList.clear();
        adapter.notifyDataSetChanged();
        getPosts();

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bus.isRegistered(this)) bus.unregister(this);
        ButterKnife.unbind(this);
    }

}

