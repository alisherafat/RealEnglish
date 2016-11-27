package ir.realenglish.app.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.model.Post;
import ir.realenglish.app.view.BaseActivity;
import ir.realenglish.app.view.adapter.ViewPagerAdapter;
import ir.realenglish.app.view.fragment.FragmentComment;
import ir.realenglish.app.view.fragment.FragmentPostItems;

public class ActivityPost extends BaseActivity {
    @Bind(R.id.tablayout) TabLayout tablayout;
    @Bind(R.id.viewPager) ViewPager viewPager;
    public Post post;
    public List<Post.Item> itemList = new ArrayList<>();
    public List<String> downloadingFiles = new ArrayList<>();
    public List<String> readyFiles = new ArrayList<>();
    private Gson gson = new Gson();
    private Type itemType = new TypeToken<List<Post.Item>>() {
    }.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idiom);
        ButterKnife.bind(this);
        setToolBar();
        getSupportActionBar().setTitle("Post");

        post = (Post) getIntent().getSerializableExtra("idiom");
        post.setItems(itemList);

        setupViewPager();
        tablayout.setupWithViewPager(viewPager);
        getItems();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    public void getItems() {
        Ion.with(this).load("GET", EndPoints.POST_SHOW.replace("_R_", "" + post.remoteId)).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                post = Post.fromJson(result);
                itemList.addAll(gson.<Collection<? extends Post.Item>>fromJson(result.getAsJsonArray("items"), itemType));
                EventBus.getDefault().post(new MyEvent.PostDataReceived(post));
            }
        });
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment idiomFragment = FragmentPostItems.newInstance(post);
        Fragment commentFragment = FragmentComment.newInstance(1);
        adapter.addFragment(idiomFragment, "Content");
        adapter.addFragment(commentFragment, "Comments");
        viewPager.setAdapter(adapter);
    }
}
