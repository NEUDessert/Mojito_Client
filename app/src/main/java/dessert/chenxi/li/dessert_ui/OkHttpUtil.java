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
    public static boolean result;
    public static String TAG;
    public static OkHttpClient client = new OkHttpClient();;

    /**
     * Post键值对
     */

    public static boolean postParams(String url, final String account, final String password) {
        RequestBody body = new FormBody.Builder().add("username", account)
                                                .add("password", password).build();

        Request request = new Request.Builder().url(url).post(body).build();
        Log.i("request",request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    setResult(true);
                    TAG = String.valueOf(result);
                    Log.i(TAG, "httpGet OK: " + account+","+password +","+ response.toString());
                    Log.i("body", response.body().string());
                } else {
                    setResult(false);
                    TAG = String.valueOf(result);
                    Log.i(TAG, "httpGet error: " + account+","+password +","+ response.toString());
                    Log.i("body", response.body().string());
                }
            }
        });
        if (getResult()){
            return true;
        }else {
            return false;
        }
    }

    public static boolean getResult(){
        return result;
    }

    public static void setResult(boolean num){
        result = num;
    }
}
