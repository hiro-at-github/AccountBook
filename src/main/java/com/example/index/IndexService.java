package com.example.index;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexService {

    @Autowired
    private IndexCalendar indexCalendar;

    /** 科目のマップ */
    public Map<String, String> getAccountMap() {

        Map<String, String> accountMap = new HashMap<>();
        accountMap.put("", "");
        accountMap.put("食費", "shokuhi");
        accountMap.put("消耗品費", "shomohinhi");
        accountMap.put("水道光熱費", "suidokonetsuhi");

        return accountMap;

    }

    /** 消費税率のマップ */
    public Map<String, Integer> getTaxRateMap() {
        Map<String, Integer> taxRateMap = new HashMap<>();
        taxRateMap.put("", null);
        taxRateMap.put("8", 8);
        taxRateMap.put("10", 10);

        return taxRateMap;

    }

    /**  */
    public int getCurrentMonth() {

        Calendar cal = GregorianCalendar.getInstance();

        return cal.get(Calendar.MONTH);

    }

    /**  */
    public IndexCalendar getIndexCalendar() {
        return indexCalendar;
    }

}
