package yxf.teachme.network.api;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import yxf.teachme.model.ZhuangbiImage;
import rx.Observable;
/**
 * Created by Administrator on 2017/7/13.
 */

public interface VersionApi {
    @GET("search")
    Observable<List<ZhuangbiImage>> search(@Query("q") String query);
}
