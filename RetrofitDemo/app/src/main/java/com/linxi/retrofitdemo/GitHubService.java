package com.linxi.retrofitdemo;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface GitHubService {
//    @GET("users/{user}/repos")
//    Call<ResponseBody> listRepos(@Path("user") String user);
    @POST("query")
//    Call<PostQueryInfo> search(@Query("type") String type,@Query("postid") String postid);
    Observable<PostQueryInfo> searchRx(@Query("type") String type,@Query("postid") String postid);
}
