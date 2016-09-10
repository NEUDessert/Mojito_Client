package dessert.chenxi.li.dessert_ui;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 李天烨 on 2016/8/16.
 */

public class OkHttpUtil {
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    /**
     * Post键值对
     */
    public static final String TAG = "MainActivity";
    public static void postParams(String url, String account, String password) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("a", account)
                .add("b", password)
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String r = response.body().string();
                if (response.isSuccessful()) {
                    Log.i(TAG, "httpGet1 OK: " + r);
                } else {
                    Log.i(TAG, "httpGet1 error: " + r);
                }
            }
        });
    }

}
