package com.example.index;

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
    /** 科目のマップ */
    private Map<String, String> accountMap;
    /** 消費税率のマップ */
    private Map<String, Integer> taxRateMap;

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
     * 科目のマップを返却する
     *
     * @return 科目のマップ
     */
    //--------------------------------------------------------------------------------
    public Map<String, String> getAccountMap() {
        if (accountMap != null) {
            return accountMap;
        }

        accountMap = new LinkedHashMap<>();
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
        if (taxRateMap != null) {
            return taxRateMap;
        }

        taxRateMap = new LinkedHashMap<>();
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
