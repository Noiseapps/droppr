package pl.siiletscode.droppr.RESTConnection;

import android.content.Context;
import android.content.res.Resources;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import pl.siiletscode.droppr.R;
import retrofit.RestAdapter;

/**
 * Created by Walen on 2015-11-20.
 */


@EBean(scope = EBean.Scope.Singleton)
public class DropprConnector {
    private DropprAPI apiService;
    @RootContext
    public Context ctx;

    @AfterInject
    void init(){
        initApiService();
    }

    private void initApiService() {

        final RestAdapter adapter = new RestAdapter.Builder().
                setLogLevel(RestAdapter.LogLevel.FULL).
                setEndpoint(ctx.getString(R.string.endpoint)).build();
        apiService = adapter.create(DropprAPI.class);
    }
}
