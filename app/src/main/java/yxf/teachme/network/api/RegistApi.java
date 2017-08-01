package yxf.teachme.network.api;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/7/16.
 */

public interface RegistApi {
    @POST("member/reg_sms")
    Observable<String> regist(@Query("mobile") String mobile, @Query("password") String password);
}
