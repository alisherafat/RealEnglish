package ir.realenglish.app.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.edmodo.cropper.CropImageView;
import com.edmodo.cropper.cropwindow.CropOverlayView;
import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.realenglish.app.R;
import ir.realenglish.app.app.Config;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.model.Favorite;
import ir.realenglish.app.network.MyNetworkService;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.view.activity.ActivityAddLesson;
import ir.realenglish.app.view.activity.ContainerActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ActivityProfile extends BaseActivity
        implements AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.circleImageView) CircleImageView circleImageView;
    @Bind(R.id.main_toolbar) Toolbar mToolbar;
    @Bind(R.id.txtUsernameSmall) TextView txtUsernameSmall;
    @Bind(R.id.main_linearlayout_title) LinearLayout mTitleContainer;
    @Bind(R.id.main_appbar) AppBarLayout mAppBarLayout;
    @Bind(R.id.txtUsernameBig) TextView txtUsernameBig;
    @Bind(R.id.txtTotalScore) TextView txtTotalScore;
    @Bind(R.id.txtPostsScore) TextView txtPostsScore;
    @Bind(R.id.txtCommentsScore) TextView txtCommentsScore;
    @Bind(R.id.txtFavoriteCount) TextView txtFavoriteCount;
    @Bind(R.id.txtFavoriteLessonsCount) TextView txtFavoriteLessonCount;
    @Bind(R.id.txtFavoritePostsCount) TextView txtFAvoritePostCount;
    @Bind(R.id.txtPostsCount) TextView txtPostsCount;
    @Bind(R.id.txtCommentsCount) TextView txtCommentsCount;
    @Bind(R.id.txtEmail) TextView txtEmail;
    @Bind(R.id.cardPosts)
    CardView cardPosts;
    private Context context;

    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private int userScore, commentsScore, commentsCount, favoriteLessonCount;

    private CropImageView cropper;
    private Bitmap bitmap, cropedBitmap;

    private int PICK_IMAGE_REQUEST = 1023;
    private File imageProfile = new File(Config.PROFILE_IMAGE_PATH);

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ActivityProfilePermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        context = this;


        mAppBarLayout.addOnOffsetChangedListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                refresh();
            }
        });
        mToolbar.inflateMenu(R.menu.menu_main);
        startAlphaAnimation(txtUsernameSmall, 0, View.INVISIBLE);

        setupViews();
        Snackbar.make(circleImageView, "Shake to refresh your profile", Snackbar.LENGTH_SHORT).show();
        new File(Config.DIR_IMAGE).mkdirs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    private void refreshRemote() {
        swipeRefreshLayout.setRefreshing(true);
        String url = EndPoints.USER_PROFILE_BASIC_INFO.replace("_R_", String.valueOf(UserService.getId(0)))
                + UserService.getApiToken();

        Ion.with(this).load("GET", url)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    commentsScore = object.getInt("comments_score");
                    userScore = object.getInt("score");
                    // postsScore = object.getInt("posts_score");
                    commentsCount = object.getInt("comments_count");
                    //   postsCount = object.getInt("posts_count");
                    //  UserService.putCommentsScore(commentsScore);
                    //  UserService.putPostsScore(postsScore);
                    UserService.putScore(userScore);
                    UserService.putCommentsCount(commentsCount);
                    //   UserService.putPostsCount(postsCount);
                    setupViews();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void refresh() {
        if (!UserService.grantWithSnackBar(this, circleImageView)) {
            swipeRefreshLayout.setRefreshing(false);
            setupViews();
            return;
        }
        refreshRemote();
    }

    private void setupViews() {
        if (UserService.grantSuper()) {
            cardPosts.setVisibility(View.VISIBLE);
        }
        //   int postScore = UserService.getPostsScore(0);
        //   int commentScore = UserService.getCommentsScore(0);

        favoriteLessonCount = Favorite.getAllCount("lesson");
        commentsCount = UserService.getCommentsCount(0);
        //   favoritePostCount = Favorite.getAllCount("post");

        // txtFavoriteCount.setText(String.valueOf(favoriteLessonCount + favoritePostCount));
        txtFavoriteLessonCount.setText(String.valueOf(favoriteLessonCount));
        //   txtFAvoritePostCount.setText(String.valueOf(favoritePostCount));

        txtTotalScore.setText(String.valueOf(UserService.getScore(0)));
        //   txtCommentsScore.setText(String.valueOf(commentScore));
        //   txtPostsScore.setText(String.valueOf(postScore));
        txtCommentsCount.setText(String.valueOf(commentsCount));
        txtPostsCount.setText(String.valueOf(UserService.getPostsCount(0)));

        txtUsernameBig.setText(UserService.getUsername("Username"));
        txtUsernameSmall.setText(UserService.getUsername("username"));
        txtEmail.setText(UserService.getEmail(""));

        if (imageProfile.exists()) {
            circleImageView.setImageBitmap(BitmapFactory.decodeFile(imageProfile.getAbsolutePath()));
        }
    }

    private boolean userAccess() {
        if (UserService.grant()) {
            return true;
        }
        Snackbar.make(circleImageView, "You should create an account", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, AccountActivity.class));
                    }
                })
                .show();
        return false;
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void uploadImage() {
        try {
            File file = new File(Config.DIR_IMAGE);
            file.mkdirs();
            if (imageProfile.exists()) imageProfile.delete();

            circleImageView.setImageBitmap(cropedBitmap);
            FileOutputStream fileOutputStream = new FileOutputStream(imageProfile);
            cropedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            EventBus.getDefault().post(new MyEvent.RefreshNavigation());

            Intent intent = new Intent(context, MyNetworkService.class);
            intent.putExtra(MyNetworkService.ACTION, MyNetworkService.UPLOAD_IMAGE);
            startService(intent);
            Snackbar.make(circleImageView, "Uploading image...", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnClick({R.id.btnNewPost, R.id.btnShowPosts, R.id.circleImageView, R.id.btnShowComments,
                     R.id.btnShowFavoritePosts, R.id.btnShowFavoriteLessons, R.id.btnNewLesson})
    public void onClick(View view) {
        if (!userAccess()) return;
        switch (view.getId()) {
            case R.id.btnNewPost: {
                Intent intent = new Intent(context, ContainerActivity.class);
                intent.putExtra(ContainerActivity.TYPE, ContainerActivity.IDIOM_ADD);
                startActivity(intent);
                slideToRightTransition();
                break;
            }
            /*
            case R.id.btnShowPosts: {
                if (postsCount == 0) {
                    Utils.toast("not found");
                    return;
                }
                Intent intent = new Intent(this, ContainerActivity.class);
                intent.putExtra(ContainerActivity.TYPE, ContainerActivity.POST_LIST);
                startActivity(intent);
                slideToRightTransition();
                break;
            }
            */
            case R.id.circleImageView: {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;
            }
            case R.id.btnShowComments: {
                if (commentsCount == 0) {
                    Utils.toast("not found");
                    return;
                }
                Intent intent = new Intent(this, ContainerActivity.class);
                intent.putExtra(ContainerActivity.TYPE, ContainerActivity.USER_COMMENTS);
                startActivity(intent);
                slideToRightTransition();
                break;
            }
            /*
            case R.id.btnShowFavoritePosts: {

                if (favoritePostCount == 0) {
                    Utils.toast("not found");
                    return;
                }
                Intent intent = new Intent(this, ContainerActivity.class);
                intent.putExtra(ContainerActivity.TYPE, ContainerActivity.FAVORITE_POSTS);
                startActivity(intent);
                slideToRightTransition();
                break;
            }
            */
            case R.id.btnShowFavoriteLessons: {
                if (favoriteLessonCount == 0) {
                    Utils.toast("not found");
                    return;
                }
                Intent intent = new Intent(this, ContainerActivity.class);
                intent.putExtra(ContainerActivity.TYPE, ContainerActivity.FAVORITE_LESSONS);
                startActivity(intent);
                slideToRightTransition();
                break;
            }
            case R.id.btnNewLesson: {
                Intent intent = new Intent(context, ActivityAddLesson.class);
                startActivity(intent);
                break;
            }
        }

    }

    private void showCropDialg() {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Profile image")
                .customView(R.layout.dialog_crop_image, true)
                .positiveText("set")
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(
                            @NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cropedBitmap = cropper.getCroppedImage();
                        bitmap.recycle();
                        ActivityProfilePermissionsDispatcher.uploadImageWithCheck(ActivityProfile.this);
                    }
                }).build();
        cropper = (CropImageView) dialog.getCustomView().findViewById(R.id.CropImageView);
        final CropOverlayView cropOverlayView = (CropOverlayView) cropper.findViewById(R.id.CropOverlayView);
        cropOverlayView.setInitialAttributeValues(2, true, 10, 10);
        cropper.setImageBitmap(bitmap);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cropper.setImageBitmap(bitmap);
                    }
                });
            }
        }, 100);

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != RESULT_OK) return;
        if (requestCode == PICK_IMAGE_REQUEST) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                showCropDialg();
            } catch (Exception e) {
                Utils.toast("error occurred!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        float percentage = (float) Math.abs(offset) / (float) appBarLayout.getTotalScrollRange();

        swipeRefreshLayout.setEnabled(offset == 0);

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(txtUsernameSmall, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(txtUsernameSmall, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @OnClick(R.id.circleImageView)
    public void onClick() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        refresh();
    }


    @Override
    protected void onStop() {
        super.onStop();
        ShakeDetector.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }
}