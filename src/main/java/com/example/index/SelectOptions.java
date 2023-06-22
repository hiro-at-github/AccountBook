package com.example.index;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

//--------------------------------------------------------------------------------
/**
 * 科目と消費税率の選択肢のクラス
 */
//--------------------------------------------------------------------------------
@Component
public class SelectOptions {
    //TODO:定数の定数クラスへの移動検討
    /** 年の選択肢の個数 */
    private static final int LENGTH_OF_YEAR =2;
    /** 空文字列 */
    private static final String EMPTY = "";
    /** ドット */
    private static final String DOT = ".";

    /** メッセージソース */
    @Autowired
    private MessageSource messageSource;
    /** 科目のキー */
    private String[] accountKeyArr;
    /** 消費税率のキー */
    private String[] taxRateKeyArr;

    //--------------------------------------------------------------------------------
    /**
     *  コンストラクタ
     */
    //--------------------------------------------------------------------------------
    public SelectOptions() {
        accountKeyArr = new String[] {"shokuhi", "shomohinhi", "suidokonetsuhi"};
        taxRateKeyArr = new String[] {"no1", "no2"};
    }

    //--------------------------------------------------------------------------------
    /**
     * 年月日のマップを返却する
     *
     * @return 年月日のマップ
     */
    //--------------------------------------------------------------------------------
    public Map<String, String[]> getDateArrMap(Calendar prmCalendar) {
        // 年の設定
        String[] yearArr = new String[LENGTH_OF_YEAR];
        int thisYear = prmCalendar.get(Calendar.YEAR);

        for (int i = 0; i < LENGTH_OF_YEAR; i++) {
            yearArr[i] = String.valueOf(thisYear - LENGTH_OF_YEAR + 1 + i).substring(2);
        }

        // 月の設定
        int lengthOfMonth = 12;
        String[] monthArr = new String[lengthOfMonth];

        for (int i = 0; i < lengthOfMonth; i++) {
            monthArr[i] = String.format("%02d", i + 1);
        }

        // 日の設定
        int lengthOfDay = 31;
        String[] dayArr = new String[lengthOfDay];

        for (int i = 0; i < lengthOfDay; i++) {
            dayArr[i] = String.format("%02d", i + 1);
        }

        return new HashMap<String, String[]>() {
                {put("yearArr", yearArr);
                put("monthArr", monthArr);
                put("dayArr", dayArr);}
            };
    }

    //--------------------------------------------------------------------------------
    /**
     * 科目のマップを返却する
     *
     * @return 科目のマップ
     */
    //--------------------------------------------------------------------------------
    public Map<String, String> getAccountMap() {
        Map<String, String> accountMap = new LinkedHashMap<>();
        accountMap.put(EMPTY, EMPTY);

        for (String elem : accountKeyArr) {
            accountMap.put(messageSource.getMessage("account" + DOT + elem, null, Locale.JAPAN), elem);
        }

        return accountMap;
    }

    //--------------------------------------------------------------------------------
    /**
     * 消費税率のマップを返却する
     *
     * @return 消費税率のマップ
     */
    //--------------------------------------------------------------------------------
    public Map<String, Integer> getTaxRateMap() {
        Map<String, Integer> taxRateMap = new LinkedHashMap<>();
        taxRateMap.put(EMPTY, null);

        for (String elem : taxRateKeyArr) {
            String msg = messageSource.getMessage("tax_rate" + DOT + elem, null, Locale.JAPAN);
            taxRateMap.put(msg, Integer.valueOf(msg));
        }

        return taxRateMap;
    }

//    private Map<String, List<String>> getSelectOptionsKeysMap() {
//        String msg = messageSource.getMessage("select_options.keys", null, Locale.JAPAN);
//        String[] msgArr = msg.split(",");
//        Map<String, List<String>> selectOptionsKeysMap = new LinkedHashMap<>();
//
//        for (String elem : msgArr) {
//            String[] elemArr = elem.split(Pattern.quote("."));
//            List<String> valueList = selectOptionsKeysMap.get(elemArr[0]);
//
//            if (valueList == null) {
//                List<String> newValueList = new ArrayList<>();
//                newValueList.add(elem);
//                selectOptionsKeysMap.put(elemArr[0], newValueList);
//            } else {
//                valueList.add(elem);
//            }
//        }
//
//        return selectOptionsKeysMap;
//    }
}
