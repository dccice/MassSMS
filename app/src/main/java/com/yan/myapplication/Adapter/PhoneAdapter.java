package com.yan.myapplication.Adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yan.myapplication.R;
import com.yan.myapplication.bean.Phone;

import java.util.List;

/**
 * Created by user on 2017/5/11.
 */

public class PhoneAdapter extends BaseQuickAdapter<Phone, BaseViewHolder> {

    public PhoneAdapter(@Nullable List<Phone> data) {
        super(R.layout.item_phone, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, Phone item) {
        helper.setText(R.id.tv_company, item.getCompany());
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_phone, item.getPhone());
    }
}
