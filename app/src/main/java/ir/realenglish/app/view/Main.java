package ir.realenglish.app.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.CircleView;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.app.Config;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.app.PrefKey;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.presenter.AppService;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.view.activity.ActivitySearch;
import ir.realenglish.app.view.activity.ContainerActivity;

public class Main extends BaseActivity implements ColorChooserDialog.ColorCallback {


    @Bind(R.id.navigation_view) NavigationView navigation;
    @Bind(R.id.DrawerLayout) DrawerLayout drawerLayout;
    private FloatingActionButton floatingActionButton;
    private Context context;
    private EditText inputName, inputEmail, inputText;
    private TextInputLayout inputLayoutName, inputLayoutEmail;
    private Button btnEnter;
    private MaterialDialog askDialog;
    private EventBus eventBus = EventBus.getDefault();
    private String fragmentType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        if (!PrefKey.hasSeenTour()) {
            startActivity(new Intent(this, TourActivity.class));
        }
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
*/
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        context = this;

        initialize();
        /*
        if (!isStoragePermissionGranted()) {
            Utils.message("Allow sd card access to download some data");
        }
        primaryPreSelect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        final File courseDirectory = new File(Config.DIR_APP + File.separator + course);
        if (!courseDirectory.exists()) {
            courseDirectory.mkdirs();
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Looper.prepare();
                MainController.checkWhatsNew(context);
                Looper.loop();
            }
        }, 800);
*/

        Fragment fragment = FragmentLessonList.newInstance(FragmentLessonList.NEW);
        startFragment(fragment);
        fragmentType = "lessons";
        getSupportActionBar().setTitle("New Lessons");
    }



    public FloatingActionButton getFAB() {
        return floatingActionButton;
    }


    private int primaryPreSelect;

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        primaryPreSelect = selectedColor;
        PrefKey.setPrimaryColor(selectedColor);
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(selectedColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(selectedColor));
            getWindow().setNavigationBarColor(selectedColor);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Permission is granted
                return true;
            } else {
//Permission is revoked
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }


    }


    private void showColorChooserPrimary() {
        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.color_sub)
                .preselect(primaryPreSelect)
                .show();
    }

    @Subscribe
    public void refreshNavigation(MyEvent.RefreshNavigation event) {
        View header = navigation.getHeaderView(0);
        TextView txtUsername = (TextView) header.findViewById(R.id.name);
        TextView txtEmail = (TextView) header.findViewById(R.id.email);
        ImageView impProfile = (ImageView) header.findViewById(R.id.circleView);

        try {
            txtUsername.setText(UserService.getUsername("username"));
            txtEmail.setText(UserService.getEmail("email@example.com"));
            if (new File(Config.PROFILE_IMAGE_PATH).exists())
                impProfile.setImageBitmap(BitmapFactory.decodeFile(Config.PROFILE_IMAGE_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                Intent intent = new Intent(this, ActivitySearch.class);
                if (fragmentType.equals("lessons")) {
                    intent.putExtra(ActivitySearch.TYPE, ActivitySearch.LESSONS);
                } else {
                    intent.putExtra(ActivitySearch.TYPE, ActivitySearch.POSTS);
                }
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void initialize() {
        setToolBar();
        if (!eventBus.isRegistered(context)) eventBus.register(context);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) return false;
                selectDrawerItem(menuItem);
                return true;
            }

            public void selectDrawerItem(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.itemLessons: {
                        Fragment fragment = FragmentLessonList.newInstance(FragmentLessonList.NEW);
                        startFragment(fragment);
                        fragmentType = "lessons";
                        getSupportActionBar().setTitle("New Lessons");
                        break;
                    }
                    /*
                    case R.id.posts: {
                        Fragment fragment = FragmentPostList.newInstance(1);
                        startFragment(fragment);
                        fragmentType = "posts";
                        getSupportActionBar().setTitle("New Posts");
                        break;
                    }
                    */
                    case R.id.itemProfile: {
                        Intent intent = new Intent(context, ActivityProfile.class);
                        startActivity(intent);
                        break;
                    }

                    case R.id.itemNotice: {
                        Intent intent = new Intent(context, ContainerActivity.class);
                        intent.putExtra(ContainerActivity.TYPE, ContainerActivity.NOTIFICATION_LIST);
                        startActivity(intent);
                        break;
                    }
                    /*
                    case R.id.itemTopUser:
                        startActivity(new Intent(context, TopUserActivity.class));
                        break;
                        */
                    case R.id.itemAccount:
                        startActivity(new Intent(context, AccountActivity.class));
                        break;
                    case R.id.itemShare:
                        AppService.shareApplication(context);
                        break;
                    /*
                    case R.id.itemColor:
                        showColorChooserPrimary();
                        break;
                    case R.id.itemTour:
                        startActivity(new Intent(context, TourActivity.class));
                        break;
                        */
                    case R.id.itemAsk:
                        showAskDialog();
                        break;
                    case R.id.itemRate:
                        if (Utils.isBazaarInstalled()) {
                            String uri = "bazaar://details?id=" + getPackageName();
                            Intent rateIntent = new Intent(Intent.ACTION_EDIT);
                            rateIntent.setData(Uri.parse(uri));
                            rateIntent.setPackage("com.farsitel.bazaar");
                            startActivity(rateIntent);
                            break;
                        }
                        Utils.toast("please install bazaar on your device");
                        break;


                }
                drawerLayout.closeDrawers();
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        actionBarDrawerToggle.syncState();

        if (UserService.grant())
            refreshNavigation(new MyEvent.RefreshNavigation());

    }

    public void showAskDialog() {
        askDialog = new MaterialDialog.Builder(context)
                .title("Ask a question")
                .customView(R.layout.dialog_ask_question, true)
                .build();
        inputLayoutName = (TextInputLayout) askDialog.getCustomView().findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) askDialog.getCustomView().findViewById(R.id.input_layout_email);
        inputName = (EditText) askDialog.getCustomView().findViewById(R.id.input_name);
        inputText = (EditText) askDialog.getCustomView().findViewById(R.id.edtAskText);
        inputEmail = (EditText) askDialog.getCustomView().findViewById(R.id.input_email);
        btnEnter = (Button) askDialog.getCustomView().findViewById(R.id.btn_enter);

        if (UserService.grant()) {
            inputEmail.setText(UserService.getEmail(""));
            inputName.setText(UserService.getUsername(""));
            inputEmail.setEnabled(false);
            inputName.setEnabled(false);
            inputText.requestFocus();
        }

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAskToServer();
            }
        });
        askDialog.show();
    }

    private void sendAskToServer() {
        if (!validateName() || !validateEmail()) {
            return;
        }
        final String name = inputName.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        String text = inputText.getText().toString().trim();
        if (text.isEmpty()) {
            inputText.setError("enter your message");
            return;
        }
        askDialog.dismiss();

        Ion.with(context).load("POST", EndPoints.BASE_API + "/contact")
                .setBodyParameter("name", name)
                .setBodyParameter("email", email)
                .setBodyParameter("body", text)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
            }
        });
        Snackbar.make(navigation, "Sending message...", Snackbar.LENGTH_SHORT).show();
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
            }
        }

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating name
    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("enter your name please");
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty() || !Utils.isValidEmail(email)) {
            inputLayoutEmail.setError("enter a valid email address");
            requestFocus(inputEmail);
            return false;
        }
        inputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventBus.isRegistered(this)) eventBus.unregister(this);
    }
}