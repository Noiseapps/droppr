package pl.siiletscode.droppr.model;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Walen on 2015-11-20.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface Preferences {
    @DefaultString("{}")
    String savedUser();
}
