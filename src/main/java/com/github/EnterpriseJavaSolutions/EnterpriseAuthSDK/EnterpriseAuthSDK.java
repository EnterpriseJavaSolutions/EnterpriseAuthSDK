package com.github.EnterpriseJavaSolutions.EnterpriseAuthSDK;

import com.github.EnterpriseJavaSolutions.EnterpriseAuthSDK.exceptions.AuthException;
import com.github.EnterpriseJavaSolutions.EnterpriseAuthSDK.models.User;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class EnterpriseAuthSDK {
    public String baseURL;
    private static final OkHttpClient client = new OkHttpClient();

    public EnterpriseAuthSDK(String baseURL) {
        this.baseURL = baseURL;
    }

    public User login(String username, String password) throws AuthException {
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("username", username);
        bodyJson.put("password", password);

        RequestBody body = RequestBody.create(
                bodyJson.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseURL + "/api/signin")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String bodyString = response.body().string();
            JSONObject responseBody = new JSONObject(bodyString);

            if (!response.isSuccessful()) {
                throw new AuthException(responseBody.getString("message"));
            }

            String token = responseBody.getJSONObject("extra").getString("token");
            User user = new User(token, this);
            user.refresh();
            return user;

        } catch (IOException e) {
            throw new AuthException("HTTP request failed: " + e.getMessage(), e);
        }
    }
}
