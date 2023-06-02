package com.example.index;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class IndexService {

    private Map<String, String> errMsgMap;

    @Autowired
    private IndexCalendar indexCalendar;

    public IndexService() {
        errMsgMap = new HashMap<>();
        errMsgMap.put("amount", "税額");
        errMsgMap.put("taxAmount", "金額");
    }




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

    /** 戻り値：エラーメッセージの文字列 */
    public String buildErrMsg(BindingResult prmBResult) {
        StringBuilder errMsgBuilder = new StringBuilder();
        String errMsg = new String();
        List<String> errMsgLst = new ArrayList<>();
        List<FieldError> fErrLst = prmBResult.getFieldErrors();

        for (FieldError elem : fErrLst) {
            String field = elem.getField();
            String[] keyArr = field.split(Pattern.quote("."));

//            switch(keyArr.length) {
//            case 1:
//                errMsgLst.add(errMsgMap.get(keyArr[0]));
//
//                break;
//
//            case 2:
//                errMsgLst.add(errMsgMap.get(keyArr[1]));
//
//                break;
//
//            default:
//
//                break;
//            }

            String msg = errMsgMap.get(keyArr[keyArr.length - 1]);

            if (errMsgLst.contains(msg)) {
                continue;
            }

            if (errMsg.contains(msg)) {
                continue;
            }

            errMsgLst.add(msg);

            if (errMsg.length() > 0) {
                errMsg += "、";
            }

            errMsg += msg;
        }

//        throw new Exception(errMsg + "は半角数字を入力する");
        return errMsg + "は半角数字を入力する";
    }







}
