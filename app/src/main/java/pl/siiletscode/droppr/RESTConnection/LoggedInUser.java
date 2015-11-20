package pl.siiletscode.droppr.RESTConnection;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import pl.siiletscode.droppr.model.Preferences_;
import pl.siiletscode.droppr.model.User;

/**
 * Created by Walen on 2015-11-20.
 */
@EBean(scope = EBean.Scope.Singleton)
public class LoggedInUser {
    @Pref
    Preferences_ prefs;
    private User user;

    private final Gson gson = new Gson();

    @AfterInject
    void init(){
        user = gson.fromJson(prefs.savedUser().get(), User.class);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
