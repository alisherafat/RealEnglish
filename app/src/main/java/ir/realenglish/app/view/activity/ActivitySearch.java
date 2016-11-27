package ir.realenglish.app.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.view.BaseActivity;
import ir.realenglish.app.view.FragmentLessonList;
import ir.realenglish.app.view.FragmentPostList;

public class ActivitySearch extends BaseActivity {

    public static final String TYPE = "my_filter_type";
    public static final int POSTS = 1;
    public static final int LESSONS = 2;
    private Context context;
    @Bind(R.id.search_view) MaterialSearchView searchView;
    @Bind(R.id.fab) FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        ButterKnife.bind(this);
        setToolBar();


        initialize();
        handleSearchMode();
    }


    private void handleSearchMode() {
        Fragment fragment = null;
        switch (getIntent().getIntExtra(TYPE, -1)) {
            case POSTS: {
                fragment = FragmentPostList.newInstance(FragmentPostList.SEARCH);
                break;
            }
            case LESSONS: {
                fragment = FragmentLessonList.newInstance(FragmentLessonList.SEARCH);
                break;
            }
            default:
                throw new RuntimeException("type is not provided");
        }
        if (fragment != null)
            startFragment(fragment);
    }

    private void initialize() {
        fab.hide();
        searchView.post(new Runnable() {
            @Override
            public void run() {
                searchView.showSearch();
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 0)
                    EventBus.getDefault().post(new MyEvent.SearchSubmit(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    public interface OnSubmitSearch {
        void onSubmit(String query);
    }


}
