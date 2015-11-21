package pl.siiletscode.droppr.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import pl.siiletscode.droppr.R;
import pl.siiletscode.droppr.SignInActivity;
import pl.siiletscode.droppr.util.SignInCallbacks;

@EFragment(R.layout.fragment_login)
@OptionsMenu(R.menu.menu_login)
public class LoginFragment extends Fragment implements SignInActivity.ActionReceiver{

    private SignInCallbacks callbacks;

    @AfterViews
    void init() {
        callbacks = (SignInCallbacks) getActivity();
        final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(supportActionBar != null) {
            supportActionBar.setTitle(R.string.login);
        }
    }

    @OptionsItem(R.id.actionLogin)
    void showLogin() {
        callbacks.showLogin();
    }

    @Override
    public void onFabClicked() {

    }
}
