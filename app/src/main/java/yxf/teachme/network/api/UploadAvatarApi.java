package yxf.teachme.network.api;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Administrator on 2017/7/16.
 */

public interface UploadAvatarApi {
    @Multipart
    @POST("avatar/{username}")
    Observable<String> uploadAvatar(@Path("username") String username, @Part MultipartBody.Part file);
}
