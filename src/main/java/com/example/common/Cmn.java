package com.example.common;

import java.text.DateFormat;

public class Cmn {
    @SuppressWarnings("unchecked")
    public static <T> T autoCast(Object prmObj) {
        return (T) prmObj;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 日付として適当か判定する
     * 
     * @param prmDate 年月日の文字列配列(yy, mm, dd)。年は西暦年下二桁
     * @return 日付として適当な場合、真。不適当な場合、偽
     */
    //----------------------------------------------------------------------------------------------------
    public static boolean isCorrectDate(String... prmDate) {
        DateFormat format = DateFormat.getDateInstance();
        format.setLenient(false);

        try {
            format.parse(String.join(Cnst.EMPTY, prmDate[0], Cnst.SL, prmDate[1], Cnst.SL, prmDate[2]));
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    
    
    
    
    
    
    
}
