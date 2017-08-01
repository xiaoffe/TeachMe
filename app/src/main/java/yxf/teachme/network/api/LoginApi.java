package yxf.teachme.network.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import yxf.teachme.model.LoginUserInfo;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface LoginApi {
    @GET("member/login")
    Observable<LoginUserInfo> getLoginInfo(@Query("mobile") String mobile, @Query("password") String password);
}
