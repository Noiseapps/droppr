package pl.siiletscode.droppr;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import pl.siiletscode.droppr.fragments.LoginFragment;
import pl.siiletscode.droppr.fragments.LoginFragment_;
import pl.siiletscode.droppr.fragments.RegisterFragment;
import pl.siiletscode.droppr.fragments.RegisterFragment_;
import pl.siiletscode.droppr.util.SignInCallbacks;

@EActivity(R.layout.activity_sign_in)
public class SignInActivity extends AppCompatActivity implements SignInCallbacks {

    @ViewById
    Toolbar toolbar;
    @ViewById
    FloatingActionButton fab;
    private ActionReceiver receiver;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        showLogin();
    }

    @Override
    public void showReservation() {
        final RegisterFragment build = RegisterFragment_.builder().build();
        receiver = (ActionReceiver) build;
        fab.setBackgroundResource(R.drawable.ic_add_white_24px);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, build).
                commit();
    }

    @Override
    public void showLogin() {
        final LoginFragment build = LoginFragment_.builder().build();
        receiver = (ActionReceiver) build;
        fab.setBackgroundResource(R.drawable.ic_lock_open);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, build).
                commit();
    }

    public interface ActionReceiver {
        void onFabClicked();
    }

    @Click(R.id.fab)
    void onFabClick() {
        receiver.onFabClicked();
    }
}
