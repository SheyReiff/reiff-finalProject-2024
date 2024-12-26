package reiff.finalproject;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface CitiBikeService {

    @GET("/gbfs/en/station_information.json")

    Single<Stations> StationsResponse();

    @GET("/gbfs/en/station_status.json")

    Single<Stations> StatusResponse();

}
