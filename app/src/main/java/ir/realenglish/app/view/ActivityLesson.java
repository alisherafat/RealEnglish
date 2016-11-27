package ir.realenglish.app.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.app.Config;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.custom.CustomPlayer;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.model.Favorite;
import ir.realenglish.app.model.Lesson;
import ir.realenglish.app.network.MyNetworkService;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.view.activity.ActivityAddLesson;
import ir.realenglish.app.view.activity.ContainerActivity;
import ir.realenglish.app.view.adapter.ViewPagerAdapter;
import ir.realenglish.app.view.fragment.FragmentComment;
import ir.realenglish.app.view.fragment.FragmentTranscript;
import ir.realenglish.app.view.fragment.FragmentVocabulary;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ActivityLesson extends BaseActivity {
    public static final String LESSON = "lesson";
    private Context context;
    @Bind(R.id.tablayout) TabLayout tabLayout;
    @Bind(R.id.viewPager) ViewPager viewPager;
    @Bind(R.id.player) CustomPlayer customPlayer;

    private List<String> podNames = new ArrayList<>();
    private String directoryPath;
    private File directoryFile;
    @Nullable
    public Lesson lesson;
    private FragmentTranscript fragment1;
    private FragmentVocabulary fragment2;
    private EventBus bus = EventBus.getDefault();
    private boolean isFavorite;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        ButterKnife.bind(this);
        context = this;
        bus.register(this);
        initViews();

        lesson = (Lesson) getIntent().getSerializableExtra(LESSON);
        directoryPath = Config.DIR_LESSON + File.separator + lesson.number + "-" + lesson.name + "_" + lesson.remoteId;
        directoryFile = new File(directoryPath);
        isFavorite = Favorite.exists("lesson", lesson.remoteId);

        getItems();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ActivityLessonPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    private void getPodNames() {
        podNames.clear();
        for (Lesson.File file : lesson.data.files) {
            podNames.add(file.name);
        }
    }

    public void getItems() {
        Ion.with(this).load("GET", EndPoints.LESSON_SHOW.replace("_R_", String.valueOf(lesson.remoteId)))
                .as(new TypeToken<Lesson>() {
                })
                .setCallback(new FutureCallback<Lesson>() {
                    @Override
                    public void onCompleted(Exception e, Lesson result) {
                        try {
                            lesson = result;
                            bus.post(new MyEvent.LessonReceived(lesson));
                            fragment2.updateView();
                            getPodNames();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }


    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragment1 = FragmentTranscript.newInstance();
        fragment2 = FragmentVocabulary.newInstance();
        Fragment fragment3 = FragmentComment.newInstance(FragmentComment.LESSON_COMMENTS);
        adapter.addFragment(fragment1, "Content");
        adapter.addFragment(fragment2, "Vocabulary");
        adapter.addFragment(fragment3, "Comments");
        viewPager.setAdapter(adapter);
    }


    private void initViews() {
        setToolBar();
        setTitle("Lesson");
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    protected void onPause() {
        customPlayer.onPause();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lesson, menu);
        if (!UserService.grantSuper()) {
            menu.findItem(R.id.edit).setVisible(false);
        }
        MenuItem item = menu.findItem(R.id.action_favorite);
        if (isFavorite) {
            item.setIcon(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_favorite)
                    .color(Color.WHITE).sizeDp(24));
        } else {
            item.setIcon(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_favorite_border)
                    .color(Color.WHITE).sizeDp(24));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pod: {
                if (podNames.size() == 0) {
                    Utils.toast("please wait");
                    return true;
                }
                showPodDialog();
                return true;
            }
            case R.id.action_favorite: {
                if (!UserService.grantWithSnackBar(context, viewPager)) return true;

                if (isFavorite) {
                    Favorite.changeFavoriteList(context, tabLayout, "lesson", lesson.remoteId, false);
                    item.setIcon(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_favorite_border)
                            .color(Color.WHITE).sizeDp(24));
                } else {
                    Favorite.changeFavoriteList(context, tabLayout, "lesson", lesson.remoteId, true);
                    item.setIcon(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_favorite)
                            .color(Color.WHITE).sizeDp(24));
                }
                isFavorite = !isFavorite;
                return true;
            }
            case R.id.quiz: {
                Intent intent = new Intent(context, ContainerActivity.class);
                intent.putExtra(ContainerActivity.TYPE, ContainerActivity.LESSON_QUIZ);
                intent.putExtra("url", EndPoints.LESSON_QUIZ.replace("_R_", String.valueOf(lesson.remoteId)));
                intent.putExtra("id", lesson.remoteId);
                startActivity(intent);
                return true;
            }
            case R.id.edit: {
                Intent intent = new Intent(context, ActivityAddLesson.class);
                intent.putExtra(ActivityAddLesson.TYPE, ActivityAddLesson.EDIT);
                intent.putExtra(ActivityAddLesson.ID, lesson.remoteId);
                startActivity(intent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void downloadFile(Lesson.File file) {
        directoryFile.mkdirs();
        Intent intent = new Intent(this, MyNetworkService.class);
        intent.putExtra(MyNetworkService.ACTION, MyNetworkService.DOWNLOAD_LESSON_FILES);
        intent.putExtra("url", file.path);
        intent.putExtra("file", directoryPath + "/" + file.name);
        startService(intent);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForPhoneCall(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("We need your permission to put some files in external storage")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    private void showPodDialog() {
        new MaterialDialog.Builder(this)
                .title("choose")
                .items(podNames)
                .iconRes(R.mipmap.ic_headphone_dark)
                .limitIconToDefaultSize()
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == -1) {
                            return false;
                        }
                        onPlayPodClick(which);
                        return true;
                    }
                })
                .positiveText("play")
                .negativeText("cancel")
                .show();
    }

    private void onPlayPodClick(final int index) {
        String path = directoryPath + "/" + podNames.get(index);
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            customPlayer.setPath(path, podNames.get(index));
            customPlayer.setVisibility(View.VISIBLE);
            return;
        }
        new MaterialDialog.Builder(this).title("Download Pod").content("This file does not exists in your device, would you like to download?")
                .positiveText("yes")
                .negativeText("no")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(
                            @NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ActivityLessonPermissionsDispatcher.downloadFileWithCheck(ActivityLesson.this, lesson.data.files.get(index));
                    }
                }).show();
    }

    @Subscribe
    public void onDownloadComplete(MyEvent.DownloadComplete event) {
        getPodNames();
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        customPlayer.onDestroy();
        super.onDestroy();
    }
}
