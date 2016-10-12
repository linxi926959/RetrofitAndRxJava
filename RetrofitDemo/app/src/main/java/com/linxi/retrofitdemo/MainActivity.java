package com.linxi.retrofitdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.tv);

        //请求的真实地址是   https://api.github.com/users/octocat/repos

        //配置设置
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)  //设置连接超时
                .readTimeout(10000L, TimeUnit.MILLISECONDS)      //设置读取超时
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)     //设置写入超时
                .cache(new Cache(getCacheDir(), 10 * 1024 * 1024))   //设置缓存目录和10M缓存
                .addInterceptor(interceptor)      //添加日志拦截器,(该方法可以设置公共参数,头信息)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://www.kuaidi100.com/")
                //添加数据解析ConverterFactory
                .addConverterFactory(GsonConverterFactory.create())
                //添加Rxjava
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        GitHubService gitHubService = retrofit.create(GitHubService.class);
        gitHubService.searchRx("yuantong","500379523313")
                //访问网络切换异步线程
                .subscribeOn(Schedulers.io())
                //响应结果处理切换成主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PostQueryInfo>() {
                    @Override
                    public void onCompleted() {
                        //请求结束回调
                    }

                    @Override
                    public void onError(Throwable e) {
                        //错误回调
                        e.printStackTrace();
                        mTextView.setText("请求出错");
                    }

                    @Override
                    public void onNext(PostQueryInfo postQueryInfo) {
                        //成功结果回调
                        Log.e("app",postQueryInfo.getNu());
                        mTextView.setText(postQueryInfo.getNu());
                    }
                });




//        Call<PostQueryInfo> search = gitHubService.search("yuantong", "500379523313");
        //search.execute()同步请求网络,不能在主线程里调用,要开一个异步线程来使用
//        search.enqueue(new Callback<PostQueryInfo>() {//异步请求网络
//            @Override
//            public void onResponse(Call<PostQueryInfo> call, Response<PostQueryInfo> response) {
//                Log.e("app",response.body().getNu());
//            }
//
//            @Override
//            public void onFailure(Call<PostQueryInfo> call, Throwable t) {
//
//            }
//        });

//        Call<ResponseBody> repos = gitHubService.listRepos("octocat");
//        repos.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.e("app",response.body().source().toString());
//                Toast.makeText(MainActivity.this, "展示", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
    }
}
