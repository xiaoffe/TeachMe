package yxf.teachme.factory;

import java.util.HashMap;

import yxf.teachme.fragment.FragmentMe;
import yxf.teachme.fragment.FragmentOne;
import yxf.teachme.fragment.FragmentTwo;
import yxf.teachme.fragment.MyProfileFragment;
import yxf.teachme.ui.base.BaseFragment;
import yxf.teachme.Constants;

/**
 * Created by Administrator on 2017/7/17.
 */

public class FragmentFactory {
    private static final String TAG = "FragmentFactory";
    public static HashMap<Integer, BaseFragment> tempFragment = new HashMap<>();

    public static BaseFragment getFragment(int position){
        BaseFragment mFragment = tempFragment.get(position);
        if (mFragment != null) {
            return mFragment;
        }
        switch(position){
            case Constants.FragmentFactorConstant.FRAGMENT_ONE:
                mFragment = new FragmentOne();
                break;
            case Constants.FragmentFactorConstant.FRAGMENT_TWO:
                mFragment = new FragmentMe();
                break;
            case Constants.FragmentFactorConstant.FRAGMENT_THR:
                mFragment = new MyProfileFragment();
                break;
            default:
                mFragment = new FragmentOne();
                break;
        }
        tempFragment.put(position, mFragment);

        //返回需要的界面
        return mFragment;
    }
}
