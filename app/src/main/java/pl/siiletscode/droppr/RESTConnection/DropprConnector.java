package pl.siiletscode.droppr.RESTConnection;

import android.content.Context;
import android.support.annotation.NonNull;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

import pl.siiletscode.droppr.R;
import pl.siiletscode.droppr.model.Event;
import pl.siiletscode.droppr.model.User;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Walen on 2015-11-20.
 */


@EBean(scope = EBean.Scope.Singleton)
public class DropprConnector {
    private DropprAPI apiService;
    @RootContext
    public Context ctx;

    @Bean
    LoggedInUser loggedUser;

    @AfterInject
    void init(){
        initApiService();
    }

    private void initApiService() {

        final RestAdapter adapter = new RestAdapter.Builder().
                setLogLevel(RestAdapter.LogLevel.FULL).
                setRequestInterceptor(new Interceptor()).
                setEndpoint(ctx.getString(R.string.endpoint)).build();
        apiService = adapter.create(DropprAPI.class);
    }

    public Observable<User> addUser(@NonNull User user){
        if (apiService == null){
            return null;
        }
        return apiService.addUser(user);
    }

    public Observable<List<Event>> getEventList(){
        if (apiService == null){
            return null;
        }
        return apiService.getEventList();
    }

    public Observable<List<String>> getEventTypes(){
        if (apiService == null){
            return null;
        }
        return apiService.getEventTypes();
    }

    public Observable<List<User>> getEventGuests(@NonNull int eventId){
        if (apiService == null){
            return null;
        }
        return apiService.getEventGuests(eventId);
    }

    public Observable<User> getUserById(@NonNull int userId){
        if (apiService == null){
            return null;
        }
        return apiService.getUserById(userId);
    }

    public void editUserById(@NonNull int userId, @NonNull User user){
        if(apiService == null){
            return;
        }
        apiService.editUserById(userId, user);
    }

    public Observable<Event> getEventById(int eventId){
        if(apiService == null){
            return null;
        }
        return apiService.getEventById(eventId);
    }

    public void createEvent(@NonNull Event event){
        if(apiService == null){
            return;
        }
        apiService.createEvent(event);
    }

    public void editEventById(int eventId, Event event){
        if(apiService == null){
            return;
        }
        apiService.editEventById(eventId, event);
    }

    public void deleteEventById(int eventId){
        if(apiService == null){
            return;
        }
        apiService.deleteEventById(eventId);
    }

    public void removeUserFromEvent(int eventId, int userId){
        if(apiService == null){
            return;
        }
        apiService.removeUserFromEvent(eventId, userId);
    }

    private class Interceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade request) {
            addAuthenticationHeader(request);
        }

        private void addAuthenticationHeader(RequestFacade request) {
            final String basicAuthEncoded = getAuth();
            request.addHeader("Authorization", String.format("Basic %1$s", basicAuthEncoded));
        }
    }

    private String getAuth() {
        return loggedUser.getUser().getEmail() + ":" + loggedUser.getUser().getPasswordHash();
    }
}
