package com.yan.myapplication.utils;

import android.content.res.AssetManager;
import android.util.Log;

import com.yan.myapplication.bean.Phone;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by user on 2017/5/11.
 * 读取表格
 */

public class ReadExcel {
    public static String TAG = "excel";

    /**
     * 获取 excel 表格中的数据,不能在主线程中调用
     *
     * @param xlsName excel 表格的名称
     * @param index   第几张表格中的数据
     */
    public static ArrayList<Phone> getXlsData(String path, int index) {
        ArrayList<Phone> countryList = new ArrayList<Phone>();
        try {
            InputStream instream = new FileInputStream(path);
            Workbook workbook = Workbook.getWorkbook(instream);
            Sheet sheet = workbook.getSheet(index);

            int sheetNum = workbook.getNumberOfSheets();
            int sheetRows = sheet.getRows();
            int sheetColumns = sheet.getColumns();

            Log.d(TAG, "the num of sheets is " + sheetNum);
            Log.d(TAG, "the name of sheet is  " + sheet.getName());
            Log.d(TAG, "total rows is 行=" + sheetRows);
            Log.d(TAG, "total cols is 列=" + sheetColumns);

            for (int i = 0; i < sheetRows; i++) {
                Phone Phone = new Phone();
                //设置手机号
                Phone.setCompany(sheet.getCell(0, i).getContents());
                //设置姓名
                Phone.setName(sheet.getCell(1, i).getContents());
                //设置手机号
                Phone.setPhone(sheet.getCell(2, i).getContents());
                String regExp = "^[0-9]{11}$";
                Pattern p = Pattern.compile(regExp);
                Matcher m = p.matcher(sheet.getCell(2, i).getContents());
                if(m.find()) {
                    countryList.add(Phone);
                }
            }

            workbook.close();

        } catch (Exception e) {
            Log.e(TAG, "read error=" + e, e);
        }

        return countryList;
    }

}
