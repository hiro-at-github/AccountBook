package com.example.index;

import java.util.AbstractMap;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.example.common.Cnst;

//----------------------------------------------------------------------------------------------------
/**
 * 日付、科目、消費税率の選択肢のクラス
 */
//----------------------------------------------------------------------------------------------------
@Component
public class SelOpts {
    /** 年の選択肢のキー */
    public static final String YEAR_ARR = "yearArr";
    /** 月の選択肢のキー */
    public static final String MONTH_ARR = "monthArr";
    /** 日の選択肢のキー */
    public static final String DAY_ARR = "dayArr";

    /** メッセージソース */
    @Autowired
    private MessageSource messageSource;

    /** 科目のキー */
    private String[] accountKeyArr;
    
    private Stream<String> accountKeyStr;

    //----------------------------------------------------------------------------------------------------
    /**
     *  コンストラクタ
     */
    //----------------------------------------------------------------------------------------------------
    public SelOpts() {
        accountKeyArr = new String[] {"shokuhi", "shomohinhi", "suidokonetsuhi"};
        
        accountKeyStr = Stream.of("shokuhi", "shomohinhi", "suidokonetsuhi");
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 年の選択肢を返す
     *
     * @param prmYear 選択肢の末尾の年
     * @return 年の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getYearArr(Integer prmYear) {
        Integer thisYear = null;

        if (prmYear == null || (prmYear < 2000 || 2100 < prmYear)) {
            thisYear = GregorianCalendar.getInstance().get(Calendar.YEAR);
        } else {
            thisYear = prmYear;
        }

        String[] yearArr = new String[Cnst.LENGTH_OF_YEAR];

        for (int i = 0; i < yearArr.length; i++) {
            yearArr[i] = String.valueOf(thisYear - yearArr.length + 1 + i).substring(2);
        }

        return yearArr;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 月の選択肢を返す
     *
     * @return 月の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getMonthArr() {
        return getOptArr(12);
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 日の選択肢を返す
     *
     * @param prmYear
     * @return 日の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getDayArr() {
        return getOptArr(31);
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 科目の選択肢を返す
     *
     * @return 科目の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public Map<String, String> getAccountMap() {
        //                Map<String, String> accountMap = new LinkedHashMap<>();
        //                accountMap.put(Cnst.EMPTY, Cnst.EMPTY);
        //        
        //                for (String elem : accountKeyArr) {
        //                    accountMap.put(messageSource.getMessage("account" + Cnst.PROD + elem, null, Locale.JAPAN), elem);
        //                }
        //        
        //                return accountMap;

//        Map<String, String> accountMap = Arrays.stream(accountKeyArr)
//                .map(e -> new AbstractMap.SimpleEntry<String, String>(
//                        messageSource.getMessage("account" + Cnst.PROD + e, null, Locale.JAPAN), e))
//                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
//        accountMap.put(Cnst.EMPTY, Cnst.EMPTY);

//        SimpleEntry<String, String>[] tmpArr = (SimpleEntry<String, String>[]) accountKeyStr
//                .map(e -> new SimpleEntry<String, String>(
//                        messageSource.getMessage("account" + Cnst.PROD + e, null, Locale.JAPAN), e))
//                .toArray();
        
        
        Map<String, String> accountMap = accountKeyStr
                .map(e -> new AbstractMap.SimpleEntry<String, String>(
                        messageSource.getMessage("account" + Cnst.PROD + e, null, Locale.JAPAN), e))
                .sorted(Entry.comparingByValue())               .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
//        Map<String, String> tmpMap = new TreeMap<>(accountMap);
        
        
        
        accountMap.put(Cnst.EMPTY, Cnst.EMPTY);

        return accountMap;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 消費税率の選択肢を返す
     *
     * @return 消費税率の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public Map<String, Integer> getTaxRateMap() {
        Map<String, Integer> taxRateMap = new LinkedHashMap<>();
        taxRateMap.put(Cnst.EMPTY, null);

        // 消費税率のキーは"no1"、"no2"だけのため直接記述
        for (int i = 0; i < 2; i++) {
            String msg = messageSource.getMessage("tax_rate" + Cnst.PROD + "no" + String.valueOf(i + 1), null, Locale.JAPAN);
            taxRateMap.put(msg, Integer.valueOf(msg));
        }

        return taxRateMap;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 先頭が"01"、末尾が引数の長さの選択肢を返す
     *
     * @param prmLength 選択肢の長さ
     * @return 選択肢
     */
    //----------------------------------------------------------------------------------------------------
    private String[] getOptArr(int prmLength) {
        //        String[] optArr = new String[prmLength];
        //
        //        for (int i = 0; i < optArr.length; i++) {
        //            optArr[i] = String.format("%02d", i + 1);
        //        }
        //
        //        return optArr;

        return IntStream.rangeClosed(1, prmLength).boxed().map(e -> String.format("%02d", e)).toArray(String[]::new);
    }
}
