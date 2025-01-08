package reiff.finalproject.part3;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecommendedRouteServiceFactory {

    public RecommendedRouteService getService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://k3egy5h4rmuyt4dufw6md5d5s40qpucq.lambda-url.us-east-2.on.aws/")
                // Configure Retrofit to use Gson to turn the Json into Objects
                .addConverterFactory(GsonConverterFactory.create())
                // Configure Retrofit to use Rx
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        return retrofit.create(RecommendedRouteService.class);

    }
}

