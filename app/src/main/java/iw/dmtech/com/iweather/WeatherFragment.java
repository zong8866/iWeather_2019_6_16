package iw.dmtech.com.iweather;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import iw.dmtech.com.iweather.entity.Daily_forecast;
import iw.dmtech.com.iweather.entity.HeWeather6;


public class WeatherFragment extends Fragment {
    //    private int mPosition;
    private HeWeather6 mWeather;
    private TextView mTempCur;
    private ImageView mWeatherImage;
    private TextView mTempMin, mTempMax;
    private ImageView mConditionIcon;
    private TextView mConditionDesc;
    private TextView mUpdateTime;

    //Activity是否已经创建完成
    private boolean isActivityCreated=false;

    public static WeatherFragment newInstance(HeWeather6 weather) {
        WeatherFragment fragment=new WeatherFragment();
        fragment.updateWeather(weather);
        return fragment;
    }

    private void updateWeather(HeWeather6 weather) {
    mWeather = weather;
    if (isActivityCreated){
        fillData();
    }
    }

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isActivityCreated=true;
        fillData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_weather, container, false);
        mTempCur=view.findViewById(R.id.tv_temp_cur);
        Typeface typeface=Typeface.createFromAsset(
                getActivity().getAssets(), "setofont.ttf");
        mTempCur.setTypeface(typeface);

        mWeatherImage=view.findViewById(R.id.iv_weather_bg);
        mTempMax=view.findViewById(R.id.tv_overview_temp_max);
        mTempMin=view.findViewById(R.id.tv_overview_temp_min);
        mConditionIcon=view.findViewById(R.id.iv_condition);
        mConditionDesc=view.findViewById(R.id.tv_condition);
        mUpdateTime=view.findViewById(R.id.tv_update_time);


        View dataContainer=view.findViewById(R.id.weather_data_container);
        dataContainer.setPadding(0, 0, 0, getVirtualBarHeight(getActivity()));

        return view;
    }

    private int getVirtualBarHeight(Context context) {
        // 取得全屏高度值
        WindowManager windowManager=
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display=windowManager.getDefaultDisplay();
        DisplayMetrics dm=new DisplayMetrics();
        display.getMetrics(dm);
        int height=dm.heightPixels;
        // 如果系统版本大于5.0，则获取除虚拟栏之外的高度
        // 系统版本如果在5.0之前，则不存在这个问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        }
        int realHeight=dm.heightPixels;
        // 二者相减得到虚拟栏高度
        int virtualbarHeight=realHeight - height;
        if (virtualbarHeight < 0) {
            virtualbarHeight=0;
        }
        return virtualbarHeight;
    }

    private void fillData() {
        //在此处向UI填充数据

        //获取上下文，为空则退出
        Context content=getActivity();
        if (content == null || mWeather == null) {
            return;
        }
        //设置当前气温、当日最高和最低气温
        mTempCur.setText(mWeather.getNow().getTmp());
        Daily_forecast today = mWeather.getDaily_forecast().get(0);
        mTempMin.setText(today.getTmp_min()+"℃");
        mTempMax.setText(today.getTmp_max()+"℃");

        //设置当前天气状况图标和文字描述
        //根据当前天气状况获取图标资源
        mConditionIcon.setImageResource(content.getResources().getIdentifier(
                "ic_w"+mWeather.getNow().getCond_code(),
                "raw",
                getContext().getPackageName()));

        mConditionDesc.setText(mWeather.getNow().getCond_txt());
        mUpdateTime.setText(mWeather.getUpdate().getLoc());


    }
}
