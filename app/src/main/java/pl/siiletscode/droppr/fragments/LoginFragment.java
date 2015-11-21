package pl.siiletscode.droppr.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
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
public class LoginFragment extends Fragment implements SignInActivity.ActionReceiver, Validator.ValidationListener {

    private SignInCallbacks callbacks;

    @ViewById
    @NotEmpty(messageResId = R.string.notEmpty)
    @Email(messageResId = R.string.invalidMail)
    EditText email;

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
}
