package com.example.index;

import java.util.ArrayList;
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
    /** エラーメッセージのキー */
    private static final String ERR_MSG_KEY = "amount_message";

    //TODO:定数クラスに移動
    private static final String S_SPRTR = System.getProperty("line.separator");
    private static final String S_EMPTY = "";

    /** メッセージソース */
    @Autowired
    private MessageSource messageSource;

    /** 日付の選択肢 */
    @Autowired
    private IndexCalendar indexCalendar;

    /** 科目と消費税率の選択肢 */
    @Autowired
    private SelectOptions selectOptions;

    /** 科目のマップ */
//    private Map<String, String> accountMap;
    /** 消費税率のマップ */
//    private Map<String, Integer> taxRateMap;
    /** エラーメッセージのマップ */
    private Map<String, String> errMsgMap;

    //--------------------------------------------------------------------------------
    /**
     * コンストラクタ
     */
    //--------------------------------------------------------------------------------
    public IndexService() {
//        selectOptions = new SelectOptions();
    }

//    //--------------------------------------------------------------------------------
//    /**
//     * 現在の月を返却する
//     */
//    //--------------------------------------------------------------------------------
//    public int getCurrentMonth() {
//        Calendar cal = GregorianCalendar.getInstance();
//
//        return cal.get(Calendar.MONTH);
//    }

    //--------------------------------------------------------------------------------
    /**
     * 日付の選択肢を返却する
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
        return selectOptions.getTaxRateMap();
    }

    //--------------------------------------------------------------------------------
    /**
     * バインディングリザルトのデータを元に、エラーメッセージを組み立てて返却する
     *
     * @param prmResult バインディングリザルト
     * @return エラーメッセージ
     */
    //--------------------------------------------------------------------------------
    public String buildErrMsg(BindingResult prmResult) {
        initErrMsgMap();
        StringBuilder errMsgBuilder = new StringBuilder();

        List<FieldError> errLst = prmResult.getFieldErrors();
        for (FieldError elem : errLst) {
            String field = elem.getField();
            String[] keyArr = field.split(Pattern.quote("."));

            String msg = errMsgMap.get(keyArr[keyArr.length - 1]);

            if (errMsgBuilder.indexOf(msg) > -1) {
                errMsgBuilder.append("、");
            }

            if (errMsgBuilder.length() > 0) {
                errMsgBuilder.append("、");
            }

            errMsgBuilder.append(msg);
        }

        return errMsgBuilder.toString() + errMsgMap.get(snakeToCamel(ERR_MSG_KEY));
    }

    //TODO:一通りなめてデータを返すこともできるが、一先ずチェック結果だけを返すこととする
    public String checkTmpMtd(AccountTaxrateAmount[] prmATAArr) {

        StringBuilder errMsgBuilder = new StringBuilder();

        for (int i = 0; i < prmATAArr.length; i++) {
            AccountTaxrateAmount elem = prmATAArr[i];
            String num = String.valueOf(i + 1);

            //TODO:エラー項目リストに変更
            List<String> errMsgLst = new ArrayList<>();

            //TODO:空文字他の定数化

            if (S_EMPTY.equals(elem.getAccount())) {
                errMsgLst.add("科目" + num);
            }

            if (S_EMPTY.equals(elem.getTaxRate())) {
                errMsgLst.add("税率" + num);
            }

            if (elem.getAmount() == null) {
                errMsgLst.add("金額" + num);
            }

            int size = errMsgLst.size();

            if (size == 0 || size == 3) {
                continue;
            }

            errMsgBuilder.append(errMsgLst.get(0));

            if (errMsgLst.size() == 2) {
                errMsgBuilder.append("、").append(errMsgLst.get(1));
            }

            errMsgBuilder.append("が入力されていない").append(S_SPRTR);
        }

        if (errMsgBuilder.length() > 0) {
            return errMsgBuilder.deleteCharAt(errMsgBuilder.length() - 1).toString();
        }

        return null;
    }





    // privateメソッド ---------------------------------------------------------------

    //--------------------------------------------------------------------------------
    /**
     * エラーメッセージを初期化する
     */
    //--------------------------------------------------------------------------------
    private void initErrMsgMap() {
        if (errMsgMap != null) {
            return;
        }

        errMsgMap = new HashMap<>();

        //TODO:イテレータの元になる配列のコンストラクタでの初期化を検討
        String[] keyArr = {"amount", "tax_amount", ERR_MSG_KEY};
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

    //TODO:空文字はEmptyか否か
    private boolean isNullOrEmpty(String prmTarget) {
        return true;
    }



}
