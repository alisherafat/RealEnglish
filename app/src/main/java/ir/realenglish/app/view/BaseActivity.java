package ir.realenglish.app.view;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.color.CircleView;

import ir.realenglish.app.R;
import ir.realenglish.app.app.PrefKey;


public class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;

    protected void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        int color = PrefKey.getPrimaryColor();
        if (color != -1) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(CircleView.shiftColorDown(color));
                getWindow().setNavigationBarColor(color);
            }
        }
    }

    protected FragmentManager fragmentManager = getSupportFragmentManager();

    protected void startFragment(final Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment, fragment.getClass().getSimpleName()).commit();
    }

    protected void slideToLeftTransition() {
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    protected void slideToRightTransition() {
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = new Intent("PERMISSION_RECEIVER");
        intent.putExtra("requestCode",requestCode);
        intent.putExtra("permissions",permissions);
        intent.putExtra("grantResults",grantResults);
        sendBroadcast(intent);
    }
}

/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
 */
