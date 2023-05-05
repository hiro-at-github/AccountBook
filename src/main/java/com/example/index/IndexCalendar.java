package com.example.index;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class IndexCalendar {

    /**  */
    private String[] yearArr;

    /**  */
    private String[] monthArr;

    /**  */
    private String[] dayArr;

    /**  */
    private String currentYear;

    /**  */
    private String currentMonth;

    /**  */
    private String currentDay;

    /**  */
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
