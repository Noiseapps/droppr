package pl.siiletscode.droppr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    @ViewById
    Toolbar toolbar;
    @ViewById
    FloatingActionButton fab;
    private ActionReceiver receiver;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        showLogin();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.insufficientPermissions);
                builder.setMessage(getString(R.string.requiresPermissions));
                builder.setPositiveButton(R.string.quit, (dialog, which) -> {
                    finish();
                });
                builder.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void showRegister() {
        final RegisterFragment build = RegisterFragment_.builder().build();
        receiver = build;
        fab.setImageResource(R.drawable.ic_add_white_24px);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, build).
                commit();
    }

    @Override
    public void showLogin() {
        final LoginFragment build = LoginFragment_.builder().build();
        receiver = build;
        fab.setImageResource(R.drawable.ic_lock_open);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, build).
                commit();
    }

    @Click(R.id.fab)
    void onFabClick() {
        receiver.onFabClicked();
    }

    public interface ActionReceiver {
        void onFabClicked();
    }
}
