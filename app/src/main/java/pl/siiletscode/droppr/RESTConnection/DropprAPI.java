package pl.siiletscode.droppr.RESTConnection;

import java.util.List;

import pl.siiletscode.droppr.model.Event;
import pl.siiletscode.droppr.model.User;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Walen on 2015-11-20.
 */
public interface DropprAPI {
    @POST("/api/users")
    Observable<User> addUser(@Body String email, @Body String passwordHash);

    @GET("/api/events")
    Observable<List<Event>> getEventList();

    @GET("api/events/types")
    Observable<List<String>> getEventTypes();

    @GET("/api/events/{id}/participants")
    Observable<List<User>> getEventGuests(@Path("id") int eventId);

    @GET("/api/users/{id}")
    Observable<User> getUserById(@Path("id") int userId);

    @PUT("/api/users/{id}")
    void editUserById(@Path("id") int userId, @Body User user);

    @GET("/api/events/{id}")
    Observable<Event> getEventById(@Path("id") int eventId);

    @POST("/api/events")
    void createEvent(@Body Event event);

    @PUT("/api/events/{id}")
    void editEventById(@Path("id") int eventId, @Body Event event);

    @DELETE("/api/events/{id}")
    void deleteEventById(@Path("id") int eventId);

    @DELETE("/api/events/{evtId}/users/{usrId}")
    void removeUserFromEvent(@Path("evtId") int eventId, @Path("usrId") int userId);

}
