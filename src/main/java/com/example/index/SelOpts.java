package com.example.index;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
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

    //TODO:ソースコード整理
//    private static final String ACCOUNT = "account";
    
    /** メッセージソース */
    @Autowired
    private MessageSource messageSource;

    //----------------------------------------------------------------------------------------------------
    /**
     *  コンストラクタ
     */
    //----------------------------------------------------------------------------------------------------
    public SelOpts() {
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

//        String[] yearArr = new String[Cnst.LENGTH_OF_YEAR];
//
//        for (int i = 0; i < yearArr.length; i++) {
//            yearArr[i] = String.valueOf(thisYear - yearArr.length + 1 + i).substring(2);
//        }

        return IntStream.rangeClosed(thisYear - Cnst.LENGTH_OF_YEAR + 1, thisYear).boxed()
                .map(e -> String.valueOf(e).substring(2)).toArray(String[]::new);
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
        Map<String, String> accountMap = new LinkedHashMap<>();
        accountMap.put(Cnst.EMPTY, Cnst.EMPTY);
        
        String[] keyArr = messageSource.getMessage("select_options.keys", null, Locale.JAPAN).split(Cnst.COMMA);
//        Stream.of(keyArr).filter(e -> e.contains("account"))
//                .forEach(e -> accountMap.put(messageSource.getMessage(e, null, Locale.JAPAN), e.split(Cnst.PROD)[1]));
        // 上記コメントアウトe.split(Cnst.PROD)[1]で例外発生する為
      Stream.of(keyArr).filter(e -> e.contains("account"))
      .forEach(e -> accountMap.put(messageSource.getMessage(e, null, Locale.JAPAN), e.substring("account".length() + 1)));

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
        Stream.of("1", "2").forEach(e -> {
            String msg = messageSource.getMessage(String.join(Cnst.EMPTY, "tax_rate" + Cnst.PROD, "no", e), null,
                    Locale.JAPAN);
            taxRateMap.put(msg, Integer.valueOf(msg));
        });

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
