package pl.siiletscode.droppr.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import pl.siiletscode.droppr.R;
import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.RESTConnection.LoggedInUser;
import pl.siiletscode.droppr.SignInActivity;
import pl.siiletscode.droppr.model.Event;
import pl.siiletscode.droppr.model.User;
import pl.siiletscode.droppr.util.SignInCallbacks;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EFragment(R.layout.fragment_login)
@OptionsMenu(R.menu.menu_login)
public class LoginFragment extends Fragment implements SignInActivity.ActionReceiver, Validator.ValidationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private SignInCallbacks callbacks;

    @ViewById
    @NotEmpty(messageResId = R.string.notEmpty)
    @Email(messageResId = R.string.invalidMail)
    EditText email;

    private GoogleApiClient googleApiClient;

    @ViewById
    @NotEmpty(messageResId = R.string.notEmpty)
    @Password(min = 6, messageResId = R.string.invalidPass)
    EditText password;
    private Validator validator;
    @Bean
    DropprConnector connector;
    @Bean
    LoggedInUser loggedInUser;
    private ProgressDialog progressDialog;

    @AfterViews
    void init() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.serverApiKey))
                .build();
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Auth.GoogleSignInApi.silentSignIn(googleApiClient).setResultCallback(this::handleSignInResult);
        validator = new Validator(this);
        validator.setValidationListener(this);
        callbacks = (SignInCallbacks) getActivity();
        final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setTitle(R.string.login);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            //Strzał do bazy po usera, zapisać go jako zalogowanego
            //TODO tududu tududu
            Snackbar.make(email, "Udało się zalogować googlem!", Snackbar.LENGTH_LONG).show();
        }else{
            //Fail.
            Snackbar.make(email, R.string.googleLoginFailed,Snackbar.LENGTH_LONG).show();
        }
    }

    @OptionsItem(R.id.actionRegister)
    void showLogin() {
        callbacks.showRegister();
    }

    @OptionsItem(android.R.id.home)
    void onHome() {
        getActivity().finish();
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(R.string.loggingIn);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideProgress() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    @EditorAction(R.id.password)
    public void onFabClicked() {
        showProgress();
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        final User user = new User();
        user.setEmail(email.getText().toString().trim());
        user.setPasswordHash(md5(password.getText().toString().trim()));
        loggedInUser.setUser(user);
        connector.getEventList().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::loginSucceeded, this::loginFailed);
    }

    public String md5(String s) {
        try {
            final MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            final StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest)
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void loginFailed(Throwable throwable) {
        hideProgress();
        Snackbar.make(email, R.string.failedToLogin, Snackbar.LENGTH_LONG).show();
    }

    private void loginSucceeded(List<Event> events) {
        hideProgress();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        hideProgress();
        for (ValidationError error : errors) {
            ((EditText) error.getView()).setError(error.getCollatedErrorMessage(getActivity()));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Snackbar.make(email, R.string.connection_not_successful, Snackbar.LENGTH_LONG).show();
    }

    @Click(R.id.google_sign_in_button)
    public void onGoogleSignInButtonClicked(){
        signIn();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
}
