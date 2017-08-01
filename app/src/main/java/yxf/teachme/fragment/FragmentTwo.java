package yxf.teachme.fragment;

import android.view.View;
import android.widget.TextView;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import yxf.teachme.R;
import yxf.teachme.ui.base.BaseFragment;
import yxf.teachme.ui.base.LoadingPager;

/**
 * Created by Administrator on 2017/7/17.
 */

public class FragmentTwo extends BaseFragment {
    @Bind(R.id.textView)
    TextView text;
    private View rootView;
    @Override
    protected LoadingPager.LoadedResult initData() {
        LoadingPager.LoadedResult result = LoadingPager.LoadedResult.ERROR;
        int random = -1;
        try{
            Thread.sleep(2*1000);
            random = new Random().nextInt(5);
        }catch (Exception e){
            e.printStackTrace();
            return LoadingPager.LoadedResult.ERROR;
        }
        switch(random){
            case 0:
                result = LoadingPager.LoadedResult.EMPTY;
                break;
            default:
                result = LoadingPager.LoadedResult.SUCCESS;
                break;
        }
        return result;
    }

    @Override
    protected View initSuccessView() {
        rootView = View.inflate(getContext(), R.layout.frgament_two_test, null);
        ButterKnife.bind(rootView);
        return rootView;
    }
}
