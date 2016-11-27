package ir.realenglish.app.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.app.Config;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.app.PrefKey;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.DialogHelper;
import ir.realenglish.app.utils.IAP.IabHelper;
import ir.realenglish.app.utils.IAP.IabResult;
import ir.realenglish.app.utils.IAP.Inventory;
import ir.realenglish.app.utils.IAP.Purchase;
import ir.realenglish.app.utils.NotificationHelper;
import ir.realenglish.app.utils.TinyDB;
import ir.realenglish.app.utils.Utils;

public class AccountActivity extends BaseActivity {
    @Bind(R.id.username) EditText edtUserName;
    @Bind(R.id.password) EditText edtPass;
    @Bind(R.id.email) EditText edtEmail;
    @Bind(R.id.tilUsername) TextInputLayout inputLayoutUsername;
    @Bind(R.id.tilPass) TextInputLayout inputLayoutPass;
    @Bind(R.id.tilemail) TextInputLayout inputLayoutEmail;
    @Bind(R.id.txtError) TextView txtError;
    @Bind(R.id.radioCourseA) RadioButton radioCourseA;
    @Bind(R.id.radioCourseB) RadioButton radioCourseB;
    @Bind(R.id.btnRegister) Button btnRegister;
    @Bind(R.id.btnLogin) Button btnLogin;
    private Context context;


    //String RSA = Config.RSA_KEY;
    private String course = "a";
    private String usernameLogin, passwordLogin;
    private EditText edtUsernameLogin;
    private EditText edtPasswordLogin;
    private TextView txtResponseLogin;
    private LinearLayout lytProgress;
    private Button btnLoginInDialog;
    private MaterialDialog dialog;


    private static final String SKU_PREMIUM = Config.SKU_COURSE_A;
    private boolean isPremium = false;


    private IabHelper mHelper;

    private String uniquePaymentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);
        ButterKnife.bind(this);
        context = this;
        setToolBar();
        uniquePaymentID = Utils.generateRandomString(8);
        /*
        if (Utils.isBazaarInstalled()) {
             mHelper = new IabHelper(this, RSA);
              initializeIAB();
        } else {
            DialogHelper.showSimpleDialog(context, getString(R.string.install_bazaar));
        }
        */
        initilizeLayout();
    }

    private void initilizeLayout() {
        edtUserName.addTextChangedListener(new MyTextWatcher(edtUserName));
        edtPass.addTextChangedListener(new MyTextWatcher(edtPass));
        edtEmail.addTextChangedListener(new MyTextWatcher(edtEmail));
        if (UserService.grant()) {
            btnRegister.setEnabled(false);
            txtError.setText("You have created an account before!");
        }
    }


    private void sendRegistrationToServer(final String username, String pass, final String email, String gcmToken) {
        final MaterialDialog dialog = DialogHelper.showIndeterminateProgressDialog(context, "Creating Account", false);
        dialog.show();
        Ion.with(context).load("POST", EndPoints.BASE_USERS)
                .setBodyParameter("name", username).setBodyParameter("password", pass)
                .setBodyParameter("email", email).setBodyParameter("gcm_token", gcmToken)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                dialog.dismiss();
                if (e != null) {
                    Utils.toast("Failed, try again please");
                    return;
                }
                try {
                    JSONObject object = new JSONObject(result.trim());
                    txtError.setVisibility(View.GONE);
                    txtError.setText("");
                    switch (object.getInt("code")) {
                        case 1:
                            object.put("name", username);
                            object.put("email", email);
                            if (UserService.login(object)) {
                                new NotificationHelper(context).setTitle("Congratulations!")
                                        .setMessage(username + ", your account created successfully.")
                                        .setIconID(R.mipmap.ic_action_tick).showNotification();
                                EventBus.getDefault().post(new MyEvent.RefreshNavigation());
                                finish();
                            } else {
                                Utils.toast("Failed to save your information!");
                            }
                            break;
                        case 2:
                            txtError.setVisibility(View.VISIBLE);
                            JSONArray array = object.getJSONArray("messages");
                            for (int i = 0; i < array.length(); i++) {
                                txtError.append(" - " + array.getString(i) + "\n");
                            }
                            break;
                        case 3:
                            inputLayoutUsername.setError("username is already taken");
                            inputLayoutEmail.setError("email exists");
                            //  txtError.setText(" - An internal error occurred in server,please try again...");
                            break;
                        case 4:
                            inputLayoutUsername.setError("username is already taken");
                            break;
                        case 5:
                            inputLayoutEmail.setError("email exists");
                            break;
                        case 6:
                            txtError.setVisibility(View.VISIBLE);
                            txtError.setText("something went wrong, try again please");
                            break;
                    }
                } catch (Exception e1) {
                    Utils.toast("Failed");
                    e1.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.btnRegister)
    void register() {
        /*
        if (!Utils.isBazaarInstalled()) {
            Utils.message("please install bazaar on your device");
            return;
        }
        */
        if (!validateUsername() || !validatePassword() || !validateEmail())
            return;

        String gcmToken = TinyDB.getString(PrefKey.GCM_TOKEN, "");
        if (gcmToken.isEmpty()) {
            Utils.toast("Token is empty");
            Utils.toast("Contact support team");
            return;
        }

        String username = edtUserName.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        sendRegistrationToServer(username, pass, email, gcmToken);
        // payThroughBazaar();
    }

    @OnClick(R.id.btnLogin)
    void login() {
        if (Utils.isOnlineAdv(context))
            showLoginDialog();
    }

    private void showLoginDialog() {
        dialog = new MaterialDialog.Builder(context)
                .title("Login ")
                .customView(R.layout.dialog_login, true)
                .build();
        edtUsernameLogin = (EditText) dialog.getCustomView().findViewById(R.id.edtUsername);
        edtPasswordLogin = (EditText) dialog.getCustomView().findViewById(R.id.edtPassword);
        txtResponseLogin = (TextView) dialog.getCustomView().findViewById(R.id.txtResponse);
        lytProgress = (LinearLayout) dialog.getCustomView().findViewById(R.id.lytProgress);
        btnLoginInDialog = (Button) dialog.getCustomView().findViewById(R.id.btnLogin);
        btnLoginInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameLogin = edtUsernameLogin.getText().toString().trim();
                passwordLogin = edtPasswordLogin.getText().toString().trim();
                if (usernameLogin.length() < 3) {
                    edtUsernameLogin.setError("username is not valid");
                } else if (passwordLogin.length() < 4) {
                    edtPasswordLogin.setError("password must be at least 4 characters!");
                } else {
                    loginByServer(usernameLogin, passwordLogin);
                }

            }
        });
        dialog.show();
    }

    private void loginByServer(final String username, final String pass) {
        lytProgress.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Utils.log("onActivityResult(" + requestCode + "," + resultCode + "," + data);
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Utils.log("onActivityResult handled by IABUtil.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.log("error in on activity result");
        }
    }


    private void payThroughBazaar() {

        final IabHelper.OnIabPurchaseFinishedListener onPurchaseFinished = new IabHelper.OnIabPurchaseFinishedListener() {

            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                Utils.log("purchase finished");
                if (!result.isFailure()) {
                    if (purchase.getOrderId().equals(purchase.getToken())) {
                        if (purchase.getSku().equals(SKU_PREMIUM)
                                && purchase.getDeveloperPayload().equals(uniquePaymentID)) {
                            // purchase was successful
                            isPremium = true;
                            PrefKey.setCoursePayment(course);
                            register();
                        }
                    } else {
                        //Purchase is not valid!
                        Utils.toast("purchase id is not valid!!");
                    }
                } else {
                    // purchase cancelled by user
                    Utils.log("canceled");
                }
                if (mHelper != null)
                    mHelper.flagEndAsync();
            }
        };

        try {
            mHelper.launchPurchaseFlow(
                    AccountActivity.this,
                    SKU_PREMIUM,
                    0,
                    onPurchaseFinished,
                    uniquePaymentID
            );
        } catch (Exception e) {
            e.printStackTrace();
            Utils.toast("an error occurred while connecting to market");
        }
    }

    private void initializeIAB() {
        final IabHelper.QueryInventoryFinishedListener onConnectToMarketFinished = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    Utils.log("Failed to query inventory: " + result);
                    return;
                } else {
                    Utils.log("Query inventory was successful.");
                    isPremium = inventory.hasPurchase(SKU_PREMIUM);
                    if (isPremium) {
                        // you are pro by this sku
                        Utils.log("user is pro");
                    } else {
                        // you are not pro by this sku
                        Utils.log("user is not pro");
                    }

                }
            }
        };
        Utils.log("Starting setup.");
        try {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Utils.log("Problem setting up In-app Billing: " + result);
                    }
                    // Hooray, IAB is fully set up!
                    mHelper.queryInventoryAsync(onConnectToMarketFinished);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
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
                case R.id.username:
                    validateUsername();
                    break;
                case R.id.password:
                    validatePassword();
                    break;
                case R.id.email:
                    validateEmail();
                    break;
            }
        }
    }


    private boolean validateEmail() {
        if (!Utils.isValidEmail(edtEmail.getText().toString().trim())) {
            inputLayoutEmail.setError("Enter a valid email address");
            return false;
        }
        inputLayoutEmail.setError(null);
        return true;
    }

    private boolean validatePassword() {
        if (edtPass.getText().toString().trim().length() < 4) {
            inputLayoutPass.setError("Minimum length is 4 characters");
            return false;
        }
        inputLayoutPass.setError(null);
        return true;
    }

    private boolean validateUsername() {
        if (edtUserName.getText().toString().trim().length() < 3) {
            inputLayoutUsername.setError("Minimum length is 3 characters");
            return false;
        }
        inputLayoutUsername.setError(null);
        return true;
    }


}
