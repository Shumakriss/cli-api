package org.shumakriss.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;


public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, String> {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Create a POJO for the body
    class Payload {
        List<String> parameters;

        public String toString() {
            return "Payload: " + this.parameters.toString();
        }
    }

    @Override
    public String handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        String response = new String("200 OK");

        // log execution details
        logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.log("CONTEXT: " + gson.toJson(context));

        // process event
        logger.log("EVENT: " + gson.toJson(event));
        Payload payload = gson.fromJson(event.getBody(), Payload.class);
        logger.log(payload.toString());

        logger.log("EVENT TYPE: " + event.getClass().toString());

        return payload.toString();
    }
}
