package com.example.index;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class IndexService {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IndexCalendar indexCalendar;

    private String[] accountKeyArr;

    private String[] taxKeyArr;

    private String[] errorKeyArr;

    private Map<String, String> accountMap;

    private Map<String, Integer> taxRateMap;

    private Map<String, String> errMsgMap;

    @Autowired
    private SelectOptions selectOptions;

    public IndexService() {
        accountKeyArr = new String[] {"shokuhi", "shomohinhi", "suidokonetsuhi"};
        taxKeyArr = new String[] {"no1", "no2"};
        errorKeyArr = new String[] {"amount", "tax_amount", "amount_message"};

        selectOptions = new SelectOptions();

    }




    /** 科目のマップ */
    public Map<String, String> getAccountMap() {
        if (accountMap != null) {
            return accountMap;
        }

        accountMap = new HashMap<>();
        accountMap.put("", "");

        for (String elem : accountKeyArr) {
            accountMap.put(messageSource.getMessage("account." + elem, null, Locale.JAPAN), elem);
        }

        return accountMap;
    }

    /** 消費税率のマップ */
    public Map<String, Integer> getTaxRateMap() {
        if (taxRateMap != null) {
            return taxRateMap;
        }

        taxRateMap = new HashMap<>();
        taxRateMap.put("", null);

        for (String elem : taxKeyArr) {
            String msg = messageSource.getMessage("tax_rate." + elem, null, Locale.JAPAN);
            taxRateMap.put(msg, Integer.valueOf(msg));
        }
        selectOptions.getSelectOptionsKeysMap();

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
        initErrMsgMap();
//        StringBuilder errMsgBuilder = new StringBuilder();
        String errMsg = new String();
//        List<String> errMsgLst = new ArrayList<>();
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

//            if (errMsgLst.contains(msg)) {
//                continue;
//            }

            if (errMsg.contains(msg)) {
                continue;
            }

//            errMsgLst.add(msg);

            if (errMsg.length() > 0) {
                errMsg += "、";
            }

            errMsg += msg;
        }

//        throw new Exception(errMsg + "は半角数字を入力する");
        return errMsg + errMsgMap.get("amount_error_message");
    }


    private void tempMtd() {
//        messageSource.
    }

    private void initErrMsgMap() {
        if (errMsgMap != null) {
            return;
        }

        errMsgMap = new HashMap<>();
        errMsgMap.put("amount", messageSource.getMessage("amount", null, Locale.JAPAN));
        errMsgMap.put("taxAmount", messageSource.getMessage("taxAmount", null, Locale.JAPAN));
        errMsgMap.put("amount_error_message", messageSource.getMessage("amount_error_message", null, Locale.JAPAN));
    }









}
