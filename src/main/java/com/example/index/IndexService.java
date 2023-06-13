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
    /**  */
    private static final String ERR_MSG_KEY = "amount_message";

    /** メッセージソース */
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IndexCalendar indexCalendar;
    /** 科目のマップ */
    private Map<String, String> accountMap;
    /** 消費税率のマップ */
    private Map<String, Integer> taxRateMap;
    /**  */
    private Map<String, String> errMsgMap;

    @Autowired
    private SelectOptions selectOptions;

    //--------------------------------------------------------------------------------
    /**
     * コンストラクタ
     */
    //--------------------------------------------------------------------------------
    public IndexService() {
        selectOptions = new SelectOptions();
    }

    //--------------------------------------------------------------------------------
    /**
     * 現在の月を返却する
     */
    //--------------------------------------------------------------------------------
    public int getCurrentMonth() {

        Calendar cal = GregorianCalendar.getInstance();

        return cal.get(Calendar.MONTH);

    }

    //--------------------------------------------------------------------------------
    /**
     *
     */
    //--------------------------------------------------------------------------------
    public IndexCalendar getIndexCalendar() {
        return indexCalendar;
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

        return selectOptions.getAccountMap();
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

        return selectOptions.getTaxRateMap();
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
        return errMsg + errMsgMap.get(snakeToCamel(ERR_MSG_KEY));
    }


    private void initErrMsgMap() {
        if (errMsgMap != null) {
            return;
        }

        //TODO:イテレータの元は先、イテレータで作るものは後
        //TODO:イテレータの元のコンストラクタでの初期化を検討
        String[] keyArr = {"amount", "tax_amount", ERR_MSG_KEY};
        errMsgMap = new HashMap<>();

        for (String elem : keyArr) {
            errMsgMap.put(snakeToCamel(elem), messageSource.getMessage("error." + elem, null, Locale.JAPAN));
        }
    }

    //--------------------------------------------------------------------------------
    /**
     * スネークケースからキャメルケースに変換して返却する
     *
     * @param 処理対象文字列
     */
    //--------------------------------------------------------------------------------
    private String snakeToCamel(String prmTarget) {
        String[] split = prmTarget.split("_");
        StringBuilder builder = new StringBuilder(split[0]);

        for (int i = 1; i < split.length; i++) {
            builder.append(split[i].substring(0, 1).toUpperCase()).append(split[i].substring(1));
        }

        return builder.toString();
    }







}
