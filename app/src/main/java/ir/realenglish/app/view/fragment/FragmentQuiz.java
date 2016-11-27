package ir.realenglish.app.view.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.model.Score;
import ir.realenglish.app.model.Test;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.DialogHelper;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.view.adapter.TestAdapter;

public class FragmentQuiz extends Fragment {
    public static final String URL = "url_to_get_test";

    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.progressBarCenter) ProgressBar progressBarCenter;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    private TestAdapter adapter;
    private List<Test> items = new ArrayList<>();
    private String url;
    private int id;

    public FragmentQuiz() {
        // Required empty public constructor
    }

    public static FragmentQuiz newInstance(String url,int id) {
        FragmentQuiz fragment = new FragmentQuiz();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString("url");
            id = getArguments().getInt("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        ButterKnife.bind(this, view);
        fab.show();
        fab.setImageDrawable(new IconicsDrawable(getActivity(), GoogleMaterial.Icon.gmd_check).color(Color.WHITE).sizeDp(24));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBarCenter.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TestAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);
        getTest();
    }

    private void getTest() {
        Ion.with(getActivity()).load("GET", url)
                .as(new TypeToken<List<Test>>() {
                })
                .setCallback(new FutureCallback<List<Test>>() {
                    @Override
                    public void onCompleted(Exception e, List<Test> result) {
                        try {
                            progressBarCenter.setVisibility(View.GONE);
                            items.addAll(result);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });

    }

    @OnClick(R.id.fab)
    void onClick() {
        if (!UserService.grantWithSnackBar(getContext(), fab)) {
            return;
        }
        if (Score.exists("lesson_test", id)) {
            adapter.showResult();
            return;
        }

        new MaterialDialog.Builder(getContext())
                .content("Do you want to see test result?")
                .positiveText("yes")
                .negativeText("no")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(
                            @NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sendTestResult();
                    }
                }).show();
    }

    private void sendTestResult() {
        final MaterialDialog dialog = DialogHelper.showIndeterminateProgressDialog(getContext(), "Sending data", false);
        int corrects = 0, wrongs = 0;
        for (Test item : items) {
            if (item.userAnswer != -1) {
                if (item.userAnswer == item.answer) {
                    corrects++;
                } else {
                    wrongs++;
                }
            }
        }
        final int score = corrects * 5 - 2 * wrongs;
        dialog.show();

        Ion.with(getActivity()).load("POST", EndPoints.USER_SCORE.replace("_R_", String.valueOf(UserService.getId(0))))
                .setBodyParameter("score", String.valueOf(score))
                .setBodyParameter("api_token", UserService.getApiToken())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            dialog.dismiss();
                            adapter.showResult();
                            Snackbar.make(fab, "Score:  +" + score, Snackbar.LENGTH_LONG).show();
                            Score.create("lesson_test", id);
                            Utils.log(result);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }
                });

    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

}
