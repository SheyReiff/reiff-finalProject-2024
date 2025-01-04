
package reiff.finalproject.aws;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RequestHandlerTest {

    @Test
    public void handleRequest() {

        //given
        String json = """
        {
          "from": {
            "lat": 40.8211,
            "lon": -73.9359
          },
          "to": {
            "lat": 40.7190,
            "lon": -73.9585
          }
        }
        """;

        APIGatewayProxyRequestEvent apiEvent = new APIGatewayProxyRequestEvent();
        apiEvent.setBody(json);
        ClosestStationRequestHandler requestHandler = new ClosestStationRequestHandler();

        //when

        Response response = requestHandler.handleRequest(apiEvent, null);
        //then

        assertNotNull(response.getStartStation().station_id);
        System.out.println(response.toJsonString());

    }
}
