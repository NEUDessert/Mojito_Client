package dessert.chenxi.li.dessert_ui.HomeFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashMap;

import dessert.chenxi.li.dessert_ui.OkHttpUtil;
import dessert.chenxi.li.dessert_ui.R;
import dessert.chenxi.li.dessert_ui.SimpleLineChart.SimpleLineChart;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String url = "http://192.168.50.198:8080/DataServer/uploadData";
    private String lastUrl = "http://115.159.205.225:8080/li/";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String account;

    private SimpleLineChart mSimpleLineChart;
    private TextView tvAccount, tvSettings, tvMoreHistory, tvBuyMore, tvContactUs;
    private Button btnWeather, btnInfo;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mSimpleLineChart = (SimpleLineChart) view.findViewById(R.id.simpleLineChart);
        tvAccount = (TextView) view.findViewById(R.id.tv_home_account);
        tvBuyMore = (TextView) view.findViewById(R.id.tv_buyMore);
        tvContactUs = (TextView) view.findViewById(R.id.tv_contactUs);
        tvMoreHistory = (TextView) view.findViewById(R.id.tv_moreHistory);
        tvSettings = (TextView) view.findViewById(R.id.tv_settings);

        String[] xItem = {"1","2","3","4","5","6","7"};
        String[] yItem = {"30","20","10","0","-10"};
        if(mSimpleLineChart == null)
            Log.e("wing","null!!!!");
        mSimpleLineChart.setXItem(xItem);
        mSimpleLineChart.setYItem(yItem);
        HashMap<Integer,Integer> pointMap = new HashMap();
        for(int i = 0;i<xItem.length;i++){
            pointMap.put(i, (int) (Math.random()*5));
        }
        mSimpleLineChart.setData(pointMap);

        tvAccount.setText(account);
        tvSettings.setOnClickListener(ClickHandler);
        tvMoreHistory.setOnClickListener(ClickHandler);
        tvContactUs.setOnClickListener(ClickHandler);
        tvBuyMore.setOnClickListener(ClickHandler);

        return view;
    }

    private View.OnClickListener ClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_buyMore:
                    Toast.makeText(getActivity(), "辽宁沈阳最大电子器械厂DESSERT倒闭了," +
                            "王八蛋老板李晨曦吃喝嫖赌,欠下了3.5个亿,带着他的小姨子跑了," +
                            "原价都是1000多、2000多的板子,统统20块,李晨曦王八蛋,你不是人," +
                            "我们辛辛苦苦给你干了大半年,你不发工资,你还我血汗钱。！", Toast.LENGTH_LONG).show();
                    OkHttpUtil.postMoreParams(url,"admin","2","30","30","150");
                    break;
                case R.id.tv_contactUs:
                    Toast.makeText(getActivity(), "自己打电话去！", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_settings:
                    Toast.makeText(getActivity(), "还没连呢！", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_moreHistory:
                    Toast.makeText(getActivity(), "现在还没数据呢！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setAccount(String name){
        account = name;
    }
}
