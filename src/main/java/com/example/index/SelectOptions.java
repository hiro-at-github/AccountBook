package com.example.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class SelectOptions {
    /**  */

    @Autowired
    private MessageSource messageSource;






    /**  */
    private List<String> optionKeyList;

    /**  */
    private List<String> keyList1;

    /**  */
    private List<String> keyList2;




    private void initOptionKeyList() {
        if (optionKeyList != null) {
            return;
        }

        List<String> beforeDot = getBeforeDot();







    }

    private List<String> getBeforeDot() {
        String msg = messageSource.getMessage("select_options.keys", null, Locale.JAPAN);
        String[] msgArr = msg.split(",");
        List<String> beforeDot = new ArrayList<>();

        for (String elem : msgArr) {
            String[] split = elem.split(Pattern.quote("."));

            if (!beforeDot.contains(split[0])) {
                beforeDot.add(split[0]);
            }
        }

        for (String elem : beforeDot) {

        }

        return beforeDot;
    }

    public void tmpMtd() {
        String msg = messageSource.getMessage("select_options.keys", null, Locale.JAPAN);
        String[] msgArr = msg.split(",");
        String lastElemArr0 = null;
        List<List<String>> tmpList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();

        for (String elem : msgArr) {
            String[] elemArr = elem.split(Pattern.quote("."));

//            if (lastBeforeDot == null || !lastBeforeDot.equals(elemArr[0])) {
//                lastBeforeDot = elemArr[0];
//                tempList = new ArrayList<>();
//            }

            if (lastElemArr0 != null || lastElemArr0 != null && !lastElemArr0.equals(elemArr[0])) {
                tmpList.add(tempList);
                tempList = new ArrayList<>();
            }


            tempList.add(elem);
            lastElemArr0 = elemArr[0];
        }

        System.out.println();
    }


    public void tmpMtd2() {
        String msg = messageSource.getMessage("select_options.keys", null, Locale.JAPAN);
        String[] msgArr = msg.split(",");
        List<String>
        
        for (String elem : msgArr) {
            
        }
        
        
        
        
        
        
        
    }
    
    
    
    
}
