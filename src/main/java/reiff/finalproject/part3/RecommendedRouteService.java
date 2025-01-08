package reiff.finalproject.part3;

import io.reactivex.rxjava3.core.Single;
import reiff.finalproject.aws.Request;
import reiff.finalproject.aws.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RecommendedRouteService {
    @POST("/CitiBikeRequestHandler")
    Single<Response> getStation(@Body Request request);
}

