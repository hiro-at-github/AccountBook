package com.example.common;

import java.text.DateFormat;
import java.util.stream.Stream;

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

    //----------------------------------------------------------------------------------------------------
    /**
     * 文字列配列を連結してキャメルケースの文字列を作成し返す
     * 
     * @param prmStrArr 処理対象の文字列配列
     * @return 作成したキャメルケースの文字列
     */
    //----------------------------------------------------------------------------------------------------
    public static String arrToCamel(String... prmStrArr) {
        Stream<String> strStr = Stream.of(prmStrArr).skip(1)
                .map(e -> String.join(Cnst.EMPTY, String.valueOf(Character.toUpperCase(e.charAt(0))), e.substring(1)));

        return String.join(Cnst.EMPTY, prmStrArr[0], String.join(Cnst.EMPTY, strStr.toArray(String[]::new)));
    }
}
