package org.robovm.store.util;

import java.io.IOException;

import org.robovm.store.api.RoboVMWebService.ActionWrapper;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class Gravatar {
    private static final String URL = "http://www.gravatar.com/avatar.php?gravatar_id=";

    private static final Gravatar instance = new Gravatar();

    private Gravatar() {}

    public static Gravatar getInstance() {
        return instance;
    }

    private final OkHttpClient client = new OkHttpClient();

    public String getUrl(String email, int size, Rating rating) {
        if (size < 1 || size > 600) {
            throw new IllegalArgumentException("The image size should be between 1 and 600");
        }
        String hash = MD5Util.md5Hex(email.toLowerCase());

        return String.format("%s%s&s=%d&r=%s", URL, hash, size, rating.name().toLowerCase());
    }

    public void getImageBytes(String email, int size, Rating rating, Action<byte[]> completion) {
        Objects.requireNonNull(completion, "completion");
        Request request = new Request.Builder().url(getUrl(email, size, rating)).build();

        try {
            Response response = client.newCall(request).execute();
            int code = response.code();
            if (code >= 200 && code < 300) { // Success
                byte[] bytes = response.body().bytes();
                ActionWrapper.WRAPPER.invoke(completion, bytes);
            } else {
                ActionWrapper.WRAPPER.invoke(completion, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ActionWrapper.WRAPPER.invoke(completion, null);
        }
    }

    public enum Rating {
        G, PG, R, X
    }
}
