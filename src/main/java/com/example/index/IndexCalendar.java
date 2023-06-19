package com.example.index;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Component;

import lombok.Data;

//--------------------------------------------------------------------------------
/**
 * 日付の選択肢のクラス
 */
//--------------------------------------------------------------------------------
@Component
@Data
public class IndexCalendar {
    /** 年の選択肢 */
    private String[] yearArr;

    /** 月の選択肢 */
    private String[] monthArr;

    /** 日の選択肢 */
    private String[] dayArr;

    /** 本日の年 */
    private String currentYear;

    /** 本日の月 */
    private String currentMonth;

    /** 本日の日 */
    private String currentDay;

    //--------------------------------------------------------------------------------
    /**
     * コンストラクタ
     */
    //--------------------------------------------------------------------------------
    public IndexCalendar() {
        Calendar calendar = GregorianCalendar.getInstance();

        // 年の設定
        int lengthOfYear = 2;
        yearArr = new String[lengthOfYear];
        int thisYear = calendar.get(Calendar.YEAR);

        for (int i = 0; i < lengthOfYear; i++) {
            yearArr[i] = String.valueOf(thisYear - lengthOfYear + 1 + i).substring(2);
        }

        currentYear = yearArr[lengthOfYear - 1];

        // 月の設定
        int lengthOfMonth = 12;
        monthArr = new String[lengthOfMonth];

        for (int i = 0; i < lengthOfMonth; i++) {
            monthArr[i] = String.format("%02d", i + 1);
        }

        currentMonth = String.format("%02d", calendar.get(Calendar.MONTH) + 1);

        // 日の設定
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayArr = new String[actualMaximum];

        for (int i = 0; i < actualMaximum; i++) {
            dayArr[i] = String.format("%02d", i + 1);
        }

        currentDay = String.format("%02d", calendar.get(Calendar.DATE));
    }
}
