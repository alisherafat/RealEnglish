package ir.realenglish.app.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.view.adapter.TopUserAdapter;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.model.User;
import ir.realenglish.app.utils.GridAutofitLayoutManager;
import ir.realenglish.app.utils.Utils;

public class TopUserActivity extends BaseActivity {
    @Bind(R.id.recycler) RecyclerView recycler;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    List<User> userList = new ArrayList<>();
    Context context;
    LinearLayoutManager layoutManager;
    TopUserAdapter adapter;
    MaterialDialog dialog;
    private boolean loading = false;
    private int page = 1;
    int lastVisibleItem, visibleItemCount, totalItemCount;
    private boolean hasContinue = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_user);
        ButterKnife.bind(this);
        context = this;
        setToolBar();
      //  dialog = DialogHelper.showIndeterminateProgressDialog(context, "Loading...", false);

        adapter = new TopUserAdapter(userList, context);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        //layoutManager = new LinearLayoutManager(context);
        GridLayoutManager gridLayoutManager = new GridAutofitLayoutManager(context, 150);
       // recycler.addItemDecoration(new ItemOffsetDecoration(5));
      //  recycler.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setAdapter(adapter);
        getData();


        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (hasContinue) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        lastVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        if (!loading) {
                            if ((visibleItemCount + lastVisibleItem) >= totalItemCount - 3) {
                                getData();
                            }
                        }
                    }
                }
            }
        });
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        loading = true;
        Ion.with(context).load("GET",EndPoints.USER_TOP)
                .setBodyParameter("page", String.valueOf(page))
                .as(new TypeToken<List<User>>() {}).setCallback(new FutureCallback<List<User>>() {
            @Override
            public void onCompleted(Exception e, List<User> result) {
                progressBar.setVisibility(View.GONE);
                loading = false;
                if (result == null) {
                    e.printStackTrace();
                    Utils.toast("failed to retrieve data");
                    return;
                }
                if(result.isEmpty()) hasContinue = false;
                userList.addAll(result);
                adapter.notifyDataSetChanged();
                page++;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
