package com.github.EnterpriseJavaSolutions.models;

import com.github.EnterpriseJavaSolutions.EnterpriseAuthSDK;
import com.github.EnterpriseJavaSolutions.exceptions.AuthException;
import com.github.EnterpriseJavaSolutions.exceptions.SetHWIDException;
import lombok.Getter;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class User {
    public EnterpriseAuthSDK sdk;
    @Getter
    public int id;
    @Getter
    public String username;
    @Getter
    public boolean hwidSet;
    @Getter
    public String hwid;
    private String token;

    private static final OkHttpClient client = new OkHttpClient();

    /*
     * THIS CONSTRUCTOR IS FOR INTERNAL USE ONLY
     */
    public User(String token, EnterpriseAuthSDK sdk) {
        this.token = token;
        this.sdk = sdk;
    }

    public void setHWID(String hwid) throws SetHWIDException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("hwid", hwid);

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(sdk.baseURL + "/api/hwid")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("authorization", token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBodyStr = response.body().string();
            JSONObject responseBody = new JSONObject(responseBodyStr);

            if (!response.isSuccessful()) {
                throw new SetHWIDException(responseBody.optString("message", "Failed to set HWID"));
            }

            this.refresh();
        } catch (IOException e) {
            throw new SetHWIDException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    // Get the user from the token
    public void refresh() throws AuthException {
        Request request = new Request.Builder()
                .url(sdk.baseURL + "/api/whoami")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String bodyString = response.body().string();
            JSONObject responseBody = new JSONObject(bodyString);

            if (!response.isSuccessful()) {
                throw new AuthException(responseBody.getString("message"));
            }

            JSONObject extra = responseBody.getJSONObject("extra");
            this.id = extra.getInt("id");
            this.username = extra.getString("username");
            this.hwidSet = extra.getBoolean("hwidSet");
            Object obj = extra.get("hwid"); // returns null if not present
            this.hwid = obj != null ? obj.toString() : null;
        } catch (IOException e) {
            throw new AuthException("HTTP request failed: " + e.getMessage(), e);
        }
    }
}
