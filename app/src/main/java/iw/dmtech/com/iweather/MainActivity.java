package iw.dmtech.com.iweather;


import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iw.dmtech.com.iweather.entity.HeWeather6;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String[] LOCATION_IDS={
            "CN101010800", //延庆（北京）
            "CN101131012",  //伊犁（新疆）
            "CN101310304",  //南沙（海南）
            "US3290097",    //洛杉矶（美国）
            "AU2147714"     //悉尼（澳大利亚）
    };
    private static final String API_KEY="11ca4545aa564de99d5b6f9132a0bb19";
    //抽屉布局对象
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<WeatherFragment> mFragments=new ArrayList<>();
    private ViewPager mPager;
    private WeatherAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer_layout);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawer=findViewById(R.id.drawer);
        mDrawerToggle=new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mDrawerToggle);
//        fillTestInstances();
        //设置ViewPager
        mPager=findViewById(R.id.viewpager);
        mAdapter=new WeatherAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        //异步获取各城市天气数据
        RequestWeatherTask task=new RequestWeatherTask();
        task.execute();
    }

//    private void fillTestInstances() {
//        for (int i=0; i < 5; i++) {
//            mFragments.add(WeatherFragment.newInstance(i));
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item_add) {
            //点击处理事件代码
            Toast.makeText(this, "添加新位置", Toast.LENGTH_SHORT).show();
        } else if (mDrawerToggle.onOptionsItemSelected(item)) {
            //处理抽屉按钮菜单点击
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class WeatherAdapter extends FragmentStatePagerAdapter {

        public WeatherAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    private class RequestWeatherTask extends AsyncTask<Void, Void, List<HeWeather6>> {
        @Override
        protected List<HeWeather6> doInBackground(Void... voids) {
            //定义空的天气列表
            List<HeWeather6> weathers=new ArrayList<>();
            //访问网络并逐个ID获取天气数据
            String format="https://free-api.heweather.net/s6/weather?location=%s&key=" + API_KEY;
            for (String id : LOCATION_IDS) {
                //组装URL
                final String url=String.format(format, id);
                //创建OkHttp客户端对象，并创建针对URL的访问请求对象
                OkHttpClient client=new OkHttpClient();
                Request request=(new Request.Builder()).url(url).build();

                Response response=null;
                try {
                    //执行访问请求并取得网路响应
                    response = client.newCall(request).execute();
                    //使用GOSN解析获取到的天气数据，产生天气实体对象并加入到列表
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray a=jsonObject.optJSONArray("HeWeather6");
                    Gson gson=new Gson();
                    HeWeather6 weather = gson.fromJson(a.getJSONObject(0).toString(), HeWeather6.class);
                    weathers.add(weather);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //将获取到的天气列表返回
            return weathers;
        }
        @Override
        protected void onPostExecute(List<HeWeather6> heWeather6s) {
            mFragments.clear();
            super.onPostExecute(heWeather6s);
            for (HeWeather6 weather : heWeather6s) {
                Log.d("iWeather",
                        weather.getBasic().getLocation() + ": " +
                                weather.getNow().getTmp() + "度");
                mFragments.add(WeatherFragment.newInstance(weather));
            }
            mAdapter.notifyDataSetChanged();
        }

    }
}
