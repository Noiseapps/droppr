package pl.siiletscode.droppr.fragments;

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
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import pl.siiletscode.droppr.R;
import pl.siiletscode.droppr.RESTConnection.DropprConnector;
import pl.siiletscode.droppr.RESTConnection.LoggedInUser;
import pl.siiletscode.droppr.SignInActivity;
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

    @AfterViews
    void init() {
        validator = new Validator(this);
        validator.setValidationListener(this);
        callbacks = (SignInCallbacks) getActivity();
        final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setTitle(R.string.login);
        }
    }

    @OptionsItem(R.id.actionLogin)
    void showLogin() {
        callbacks.showRegister();
    }

    @Override
    public void onFabClicked() {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        connector.getEventList().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            ((EditText) error.getView()).setError(error.getCollatedErrorMessage(getActivity()));
        }
    }
}
