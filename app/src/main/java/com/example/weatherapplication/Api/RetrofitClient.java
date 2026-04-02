package com.example.weatherapplication.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RetrofitClient {

    public static final String BaseURL = "http://api.weatherapi.com/v1/";
    private static final String Key = "651acd42ccf8469e95250549263103";
     private static Retrofit retrofit;

     public static Retrofit getInstance(){

         if (retrofit == null){

             OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                 Request original = chain.request();

                 Request request = original.newBuilder()
                         .header("key", Key)
                         .build();
                 return chain.proceed(request);

             }).build();

             retrofit = new Retrofit.Builder()
                     .baseUrl(BaseURL)
                     .client(client)
                     .addConverterFactory(GsonConverterFactory.create())
                     .build();
         }
         return retrofit;
     }

     public static RetrofitAPI getApi(){
         return getInstance().create(RetrofitAPI.class);
     }

}
