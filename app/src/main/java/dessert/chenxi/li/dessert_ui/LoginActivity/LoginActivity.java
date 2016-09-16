package dessert.chenxi.li.dessert_ui.LoginActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkcool.circletextimageview.CircleTextImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dessert.chenxi.li.dessert_ui.DataBase.DataBase;
import dessert.chenxi.li.dessert_ui.DataBase.DataBaseUtil;
import dessert.chenxi.li.dessert_ui.LocationDevID.locationDevIDActivity;
import dessert.chenxi.li.dessert_ui.OkHttpUtil;
import dessert.chenxi.li.dessert_ui.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "0000:0000", "0001:1235"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mAccountView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button btnLogin;
    private TextView tvLoginFail, tvRegister;
    private CircleTextImageView portraitPic;
    private String url = "http://dessert.reveur.me:8080/DataServer/login";
    private String historyInfo ;
    private String pieces[];
    private DataBase dataBase;

//    private String fileName = Environment.getExternalStorageDirectory().getPath()
//                            +File.separator+".DataStorage"+File.separator +"HistoryInfo.txt";
    private String fileName = Environment.getExternalStorageDirectory().getPath()
                                +File.separator +"HistoryInfo.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mAccountView = (AutoCompleteTextView) findViewById(R.id.account);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        dataBase = new DataBase(LoginActivity.this, "User");

//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        btnLogin = (Button) findViewById(R.id.btn_loginIn);
        portraitPic = (CircleTextImageView) findViewById(R.id.profile_image);
        tvLoginFail = (TextView) findViewById(R.id.tv_loginFail);
        tvRegister = (TextView) findViewById(R.id.tv_register);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(!DataBaseUtil.isEmpty(LoginActivity.this)){
//            Log.i("path", fileName);
            historyInfo = DataBaseUtil.readFirstInSql(LoginActivity.this);
            for (int i=0; i<2; i++) {
                pieces = historyInfo.split(":");
            }
            signIn(pieces[0], pieces[1]);

            Toast.makeText(getApplicationContext(), "历史账号登陆成功！", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent();
            //键值对
            intent.putExtra("account", pieces[0]);
            //从此activity传到另一Activity
            intent.setClass(LoginActivity.this, locationDevIDActivity.class);
            //启动另一个Activity
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        }

    }

    public void viewOnClick(View v){
        switch (v.getId()){
            case R.id.tv_loginFail:
                Toast.makeText(getApplicationContext(), "那就别用了！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_register:
                Toast.makeText(getApplicationContext(), "现在不让注册！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_loginIn:
                attemptLogin();
//                if(!(mAccountView.getText().equals("")) && !(mPasswordView.getText().equals(""))){
//                    account = mAccountView.getText().toString();
//                    password = mPasswordView.getText().toString();
//                    if(signIn(uri, account, password)) {
//                        Toast.makeText(getApplicationContext(), "登陆成功！", Toast.LENGTH_SHORT).show();
////                          Intent intent = new Intent();
////                          intent.setClass(LoginActivity.this, MainActivity.class);
////                          Bundle bundle = new Bundle();
////                          bundle.putString("account", account);
////                          intent.putExtra("bundle", bundle);
////                          setResult(1, intent);
////                          LoginActivity.this.finish();
//                        saveInfo(account, password);
//
//                        Intent intent=new Intent();
//                        //键值对
//                        intent.putExtra("account", account);
//                        //从此activity传到另一Activity
//                        intent.setClass(LoginActivity.this, MainActivity.class);
//                        //启动另一个Activity
//                        LoginActivity.this.startActivity(intent);
//                        LoginActivity.this.finish();
//                    }else {
//                        Toast.makeText(getApplicationContext(), "登陆失败!", Toast.LENGTH_SHORT).show();
//                        break;
//                    }
//                }
                break;
            default:
                break;
        }
    }

    private boolean signIn(String account, String password){
        if (OkHttpUtil.LoginPostParams(url, account, password)){

            return true;
        }else {
            return false;
        }
    }


//    保存到文档
    private void saveInfo(String name, String password) {
        String content = name +":"+ password;
        try{
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
            Toast.makeText(LoginActivity.this, "保存成功", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

//        FileOutputStream fout = new FileOutputStream(fileName);
//        byte[] bytes = writeStr.getBytes();
//
//        fout.write(bytes);
//        fout.close();
//

    }

//    读取文档
    private String readInfo(){
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] bytes = new byte[64];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            String content = new String(arrayOutputStream.toByteArray());
            return content;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Toast.makeText(getApplicationContext(), "读取联系人权限", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(account, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAccount;
        private final String mPassword;

        UserLoginTask(String account, String password) {
            mAccount = account;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return signIn(mAccount, mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(getApplicationContext(), "登陆成功！", Toast.LENGTH_SHORT).show();
                DataBaseUtil.insertInSql(LoginActivity.this, mAccount, mPassword);
                Intent intent=new Intent();
                //键值对
                intent.putExtra("account", mAccount);
                //从此activity传到另一Activity
                intent.setClass(LoginActivity.this, locationDevIDActivity.class);
                //启动另一个Activity
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            } else {
                Toast.makeText(getApplicationContext(), "登陆失败！", Toast.LENGTH_SHORT).show();
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

