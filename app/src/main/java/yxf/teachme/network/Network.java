package yxf.teachme.network;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import yxf.teachme.network.api.LoginApi;
import yxf.teachme.network.api.RegistApi;
import yxf.teachme.network.api.UploadAvatarApi;
import yxf.teachme.network.api.VersionApi;

public class Network {
    private static LoginApi loginApi;
    private static RegistApi registApi;
    private static VersionApi zhuangbiApi;
    private static UploadAvatarApi uploadAvatarApi;
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();
    //  还要看Retrofit。。
    //效果（例子）：http://www.zhuangbi.info/search?q=装逼
    public static VersionApi getVersionApi() {
        if (zhuangbiApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://www.zhuangbi.info/")
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            zhuangbiApi = retrofit.create(VersionApi.class);
        }
        return zhuangbiApi;
    }
    public static LoginApi getLoginApi() {
        if (loginApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://www.wendabang.com/Api/")
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            loginApi = retrofit.create(LoginApi.class);
        }
        return loginApi;
    }
    public static RegistApi getRegistApi() {
        if (registApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://www.wendabang.com/Api/")
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            registApi = retrofit.create(RegistApi.class);
        }
        return registApi;
    }
    public static UploadAvatarApi getUploadAvatarApi(){
        if (uploadAvatarApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://www.wendabang.com/Api/")
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            uploadAvatarApi = retrofit.create(UploadAvatarApi.class);
        }
        return uploadAvatarApi;
    }
}
