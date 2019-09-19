package com.example.mytrip.apiinterface;

import com.example.mytrip.constant.UrlHelper;
import com.example.mytrip.interceptor.AuthenticationInterceptor;
import com.example.mytrip.interceptor.MockInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(UrlHelper.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    private static AuthenticationInterceptor authInterceptor;

    private static MockInterceptor mockInterceptor = new MockInterceptor(); // only for mocking purpose

    public static <S> S createService(final Class<S> serviceClass, String token) {

        if (!httpClient.interceptors().contains(logging)) {
            logging.level(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }

        if (!httpClient.interceptors().contains(authInterceptor)) {
            authInterceptor = new AuthenticationInterceptor(token);
        }

        if (!httpClient.interceptors().contains(mockInterceptor)) {

            httpClient.addInterceptor(mockInterceptor);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }

    public static <S> S createService(final Class<S> serviceClass) {

        if (httpClient.interceptors().contains(authInterceptor)) {
            httpClient.interceptors().remove(authInterceptor);
        }

        if (httpClient.interceptors().contains(mockInterceptor)) {

            httpClient.interceptors().remove(mockInterceptor);
            logging.level(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
