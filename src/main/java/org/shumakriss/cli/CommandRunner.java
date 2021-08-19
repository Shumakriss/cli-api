package org.shumakriss.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CommandRunner {

    static String API_PARAMETER = "/apis/cli/urls/root";

    public static void main(String[] args) throws IOException {

        Gson gson = new GsonBuilder().create();

        // Get the dynamically named endpoint from a statically named SSM parameter
        SsmClient ssmClient = SsmClient.builder().region(Region.US_EAST_1).build();
        GetParameterRequest getParameterRequest = GetParameterRequest.builder().name(API_PARAMETER).build();
        GetParameterResponse getParameterResponse = ssmClient.getParameter(getParameterRequest);
        String apiUrl = getParameterResponse.parameter().value();
        System.out.println("API URL: " + apiUrl);

        // POST the command line parameters to the endpoint
        URL url = new URL(apiUrl);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("parameters", Arrays.asList(args));
        gson.toJson(payload).getBytes(StandardCharsets.UTF_8);

        byte[] out = gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        // Print the response
        InputStream inputStream = http.getInputStream();
        byte[] responseBytes = inputStream.readAllBytes();
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        System.out.println("Response: " + responseString);

        // Return successful exit code
        System.exit(0);
    }
}
