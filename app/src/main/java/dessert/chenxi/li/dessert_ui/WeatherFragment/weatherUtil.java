package dessert.chenxi.li.dessert_ui.WeatherFragment;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import dessert.chenxi.li.dessert_ui.OkHttpUtil;

/**
 * Created by 李天烨 on 2016/9/15.
 */
public class weatherUtil {
    public static String weatherNowInfo(){
        try {
            JSONTokener jsonWeather = new JSONTokener(OkHttpUtil.weatherGet());
            JSONObject info = (JSONObject) jsonWeather.nextValue();
            JSONObject data = info.getJSONObject("data");
            String tq = data.getString("tq");
            Log.i("tq", tq);
            return tq;
        } catch (IOException e) {
            return "";
        } catch (JSONException e) {
            return "";
        }
    }

    public static int weatherNumInfo(){
        String state = weatherNowInfo();
        if(state.equals("晴")){
            return 0;
        }else if(state.equals("多云")){
            return 1;
        }else if(state.equals("阵雨")){
            return 2;
        }else if(state.equals("雷阵雨")){
            return 3;
        }else if(state.equals("小雨") || state.equals("中雨")){
            return 4;
        }else if(state.equals("大雨") || state.equals("暴雨")){
            return 5;
        }else if(state.equals("小雪") || state.equals("中雪")){
            return 6;
        } else if(state.equals("大雪") || state.equals("暴雪")){
            return 7;
        }else if(state.equals("阵雪")){
            return 8;
        }else if(state.equals("刮风")){
            return 9;
        }else if(state.equals("沙尘暴")){
            return 10;
        }else if(state.equals("阴")){
            return 11;
        }else{
            return 0;
        }
    }
}
