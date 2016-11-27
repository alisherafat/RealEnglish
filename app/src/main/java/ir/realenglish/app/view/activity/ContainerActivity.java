package ir.realenglish.app.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.mikepenz.iconics.context.IconicsContextWrapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.model.Post;
import ir.realenglish.app.view.BaseActivity;
import ir.realenglish.app.view.FragmentAddPost;
import ir.realenglish.app.view.FragmentLessonList;
import ir.realenglish.app.view.FragmentPostList;
import ir.realenglish.app.view.fragment.FragmentComment;
import ir.realenglish.app.view.fragment.FragmentPostItems;
import ir.realenglish.app.view.fragment.FragmentPushList;
import ir.realenglish.app.view.fragment.FragmentQuiz;

public class ContainerActivity extends BaseActivity {

    public static final String TYPE = "action";
    public static final int IDIOM_SHOW = 1;
    public static final int IDIOM_ADD = 2;
    public static final int IDIOM_EDIT = 3;
    public static final int POST_LIST = 4;
    public static final int USER_COMMENTS = 5;
    public static final int FAVORITE_POSTS = 6;
    public static final int NOTIFICATION_LIST = 7;
    public static final int FAVORITE_LESSONS = 8;
    public static final int LESSON_QUIZ = 9;
    @Bind(R.id.fab) FloatingActionButton floatingActionButton;
    Context context;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);
        setToolBar();
        context = this;
        floatingActionButton.hide();
        switch (getIntent().getIntExtra(TYPE, 0)) {
            case IDIOM_SHOW:
                Fragment idiomFragment = FragmentPostItems.newInstance((Post) getIntent().getSerializableExtra("idiom"));
                getSupportActionBar().setTitle("Post");
                startFragment(idiomFragment);
                break;
            case IDIOM_ADD:
                Fragment addFragment = FragmentAddPost.newInstance(null, false);
                getSupportActionBar().setTitle("New Post");
                startFragment(addFragment);
                break;
            case IDIOM_EDIT:
                Fragment editFragment = FragmentAddPost.newInstance((Post) getIntent().getSerializableExtra("post"), true);
                getSupportActionBar().setTitle("Edit Post");
                startFragment(editFragment);
                break;
            case POST_LIST: {
                Fragment fragment = FragmentPostList.newInstance(FragmentPostList.USER_POST_LIST);
                getSupportActionBar().setTitle("My Posts");
                startFragment(fragment);
                break;
            }
            case USER_COMMENTS:
                Fragment mFrament = FragmentComment.newInstance(2);
                getSupportActionBar().setTitle("My Comments");
                startFragment(mFrament);
                break;
            case FAVORITE_POSTS: {
                Fragment fragment = FragmentPostList.newInstance(FragmentPostList.FAVORITE_POST);
                getSupportActionBar().setTitle("Favorite Posts");
                startFragment(fragment);
                break;
            }
            case FAVORITE_LESSONS: {
                Fragment fragment = FragmentLessonList.newInstance(FragmentLessonList.FAVORITES);
                getSupportActionBar().setTitle("Favorite Lessons");
                startFragment(fragment);
                break;
            }
            case NOTIFICATION_LIST: {
                Fragment fragment = FragmentPushList.newInstance();
                getSupportActionBar().setTitle("Notifications");
                startFragment(fragment);
                break;
            }
            case LESSON_QUIZ: {
                Fragment fragment = FragmentQuiz.newInstance(getIntent().getStringExtra("url"), getIntent().getIntExtra("id", 0));
                getSupportActionBar().setTitle("Quiz");
                startFragment(fragment);
                break;
            }
        }

    }


    public FloatingActionButton getFAB() {
        return floatingActionButton;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleBackNavigation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleBackNavigation();
        super.onBackPressed();
    }

    private void handleBackNavigation() {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameLayout);
        if (currentFragment == null) {
            super.onBackPressed();
            return;
        }
        if (currentFragment instanceof FragmentAddPost || currentFragment instanceof FragmentPostList
                || currentFragment instanceof FragmentComment) {
            finish();
            slideToLeftTransition();
        }
    }
}
