package dessert.chenxi.li.dessert_ui.LocationDevID;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import dessert.chenxi.li.dessert_ui.MainActivity;
import dessert.chenxi.li.dessert_ui.OkHttpUtil;
import dessert.chenxi.li.dessert_ui.R;

public class locationDevIDActivity extends AppCompatActivity {

    private String account, location, devID;
    private String[] locationArr, deviceIDArr;
    private Spinner locationSpinner;
    private String testUrl = "http://dessert.reveur.me:8080/DataServer/getDeviceInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_dev_id);

        Intent intent=getIntent();
        account =intent.getStringExtra("account");

        //从上个Activity传过来的值
        Toast.makeText(this, account+"  请选择设备位置", Toast.LENGTH_SHORT).show();
        initViews();
    }

    private void initViews(){
        try {
            JSONTokener jsonLocation = new JSONTokener(OkHttpUtil.locationGet(testUrl, account));
            JSONArray jsonArray = new JSONArray(jsonLocation);
            locationArr = new String[jsonArray.length()+1];
            deviceIDArr = new String[jsonArray.length()+1];
            locationArr[0] = "未知";
            deviceIDArr[0] = "0";
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                locationArr[i+1] = jsonObject.getString("location");
                deviceIDArr[i+1] = jsonObject.getString("devID");
                Log.i("第"+i+"个","location:"+locationArr[i+1]+
                        "devID:"+deviceIDArr[i+1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        locationSpinner = (Spinner) findViewById(R.id.locationChoiceSpinner);

        ArrayAdapter<String> classNameAdapter = new ArrayAdapter<>(this, R.layout.my_spinner_textview, locationArr);
        classNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(classNameAdapter);
        locationSpinner.setGravity(0x10);
        locationSpinner.setSelection(0,true);

        locationSpinner.setOnItemSelectedListener(new MyOnBaudSelectedListener());
    }



    public class MyOnBaudSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            if (position ==  0){
                Toast.makeText(locationDevIDActivity.this, account+"请选择设备位置", Toast.LENGTH_SHORT).show();
            }else {
                location = locationArr[position];
                devID = deviceIDArr[position];
                Log.i("Info", location+":"+devID);
                Intent intent=new Intent();
                //键值对
                intent.putExtra("account", account);
                intent.putExtra("location", location);
                intent.putExtra("devID", devID);
                //从此activity传到另一Activity
                intent.setClass(locationDevIDActivity.this, MainActivity.class);
                //启动另一个Activity
                locationDevIDActivity.this.startActivity(intent);
                locationDevIDActivity.this.finish();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent){

        }

    }
}
