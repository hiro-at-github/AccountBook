package com.example.index;

import java.util.ArrayList;
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
    public static final int YEAR = 0;
    /**  */
    public static final int MONTH = 1;
    /**  */
    public static final int DAY = 2;
    /**  */
    public static final int CURRENT_DATE = 0;
    /**  */
    public static final int YEAR_ARR = 1;
    /**  */
    public static final int MONTH_ARR = 2;
    /**  */
    public static final int DAY_ARR = 3;
    /**  */
    public static final int ACCOUNT_MAP = 4;
    /**  */
    public static final int TAX_RATE_MAP = 5;

    /**  */
    private static final String FLD_ERR_P = "field.error.";
    /**  */
    private static final String RLT_ERR_P = "serv.error.";
    /**  */
    private static final String ACCOUNT = "account";
    /**  */
    private static final String AMOUNT = "amount";
    /**  */
    private static final String TAX = "tax";
    /**  */
    private static final String RATE = "rate";
    /**  */
    private static final String INPUT = "input";
    /**  */
    private static final String MESSAGE = "message";
    
    
    /** メッセージソース */
    @Autowired
    private MessageSource messageSource;

    /** 科目と消費税率の選択肢 */
    @Autowired
    private SelOpts selOpts;

    /** 現在の年(整数) */
    private int thisYear;
    /** 現在の日付 */
    private String[] currentDateArr;

    /** バインディングリザルトのフィールドエラーズ用のエラーメッセージのマップ */
    private Map<String, String> fldErrMsgMap;

    /**  */
    //TODO:関連チェック？のエラーであることが分かる変数名とし、そのエラーメッセージだけを格納する
    private Map<String, String> rltErrMsgMap;

    //--------------------------------------------------------------------------------
    /**
     * コンストラクタ
     */
    //--------------------------------------------------------------------------------
    public IndexService() {
        Calendar calendar = GregorianCalendar.getInstance();
        thisYear = calendar.get(Calendar.YEAR);
        currentDateArr = new String[] { String.valueOf(thisYear).substring(2),
                String.format("%02d", calendar.get(Calendar.MONTH) + 1),
                String.format("%02d", calendar.get(Calendar.DATE)) };
    }

    //--------------------------------------------------------------------------------
    /**
     * 現在の日付を返却する
     *
     * @return 現在の日付
     */
    //--------------------------------------------------------------------------------
    public String[] getCurrentDate() {
        return currentDateArr;
    }

    //--------------------------------------------------------------------------------
    /**
     * 年の選択肢を返却する
     *
     * @return 年の選択肢
     */
    //--------------------------------------------------------------------------------
    public String[] getYearArr() {
        return selOpts.getYearArr(thisYear);
    }

    //--------------------------------------------------------------------------------
    /**
     * 月の選択肢を返却する
     *
     * @return 月の選択肢
     */
    //--------------------------------------------------------------------------------
    public String[] getMonthArr() {
        return selOpts.getMonthArr();
    }

    //--------------------------------------------------------------------------------
    /**
     * 日の選択肢を返却する
     *
     * @return 日の選択肢
     */
    //--------------------------------------------------------------------------------
    public String[] getDayArr() {
        return selOpts.getDayArr();
    }

    //--------------------------------------------------------------------------------
    /**
     * 科目の選択肢を返却する
     *
     * @return 科目の選択肢
     */
    //--------------------------------------------------------------------------------
    public Map<String, String> getAccountMap() {
        return selOpts.getAccountMap();
    }

    //--------------------------------------------------------------------------------
    /**
     * 消費税率の選択肢を返却する
     *
     * @return 消費税率の選択肢
     *
     */
    //--------------------------------------------------------------------------------
    public Map<String, Integer> getTaxRateMap() {
        return selOpts.getTaxRateMap();
    }

    //--------------------------------------------------------------------------------
    /**
     * バインディングリザルトのデータを元に、エラーメッセージを組み立てて返却する
     *
     * @param prmResult バインディングリザルト
     * @return エラーメッセージ
     */
    //--------------------------------------------------------------------------------
    public String buildFldErrMsg(BindingResult prmResult) {
        if (fldErrMsgMap == null) {
            fldErrMsgMap
                = getErrMsgMap(FLD_ERR_P, ACCOUNT, AMOUNT, TAX + Cnst.UD_S + AMOUNT, AMOUNT + Cnst.UD_S + MESSAGE);
        }

        StringBuilder errMsgBuilder = new StringBuilder();

        List<FieldError> errLst = prmResult.getFieldErrors();
        for (FieldError elem : errLst) {
            String field = elem.getField();
            String[] keyArr = field.split(Pattern.quote(Cnst.DOT));

            String msg = fldErrMsgMap.get(keyArr[keyArr.length - 1]);

            if (errMsgBuilder.indexOf(msg) > -1) {
                continue;
            }

            if (errMsgBuilder.length() > 0) {
                errMsgBuilder.append(Cnst.F_COMMA);
            }

            errMsgBuilder.append(msg);
        }

        return errMsgBuilder.toString() + fldErrMsgMap.get(snakeToCamel(AMOUNT + Cnst.UD_S + MESSAGE));
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    //TODO:名称変更
    public String checkTmpMtd(AccountTaxrateAmount[] prmATAArr) {
        if (rltErrMsgMap == null) {
            rltErrMsgMap
                = getErrMsgMap(RLT_ERR_P, ACCOUNT, TAX + Cnst.UD_S + RATE, AMOUNT, INPUT + Cnst.UD_S + MESSAGE);
        }

        StringBuilder errMsgBuilder = new StringBuilder();

        for (int i = 0; i < prmATAArr.length; i++) {
            AccountTaxrateAmount elem = prmATAArr[i];
            String num = String.format("%02d", i + 1);

            //TODO:エラー項目リストに変更。項目はitemでよいか
            List<String> errItemLst = new ArrayList<>();

            //TODO:空文字他の定数化

            if (Cnst.EMPTY.equals(elem.getAccount())) {
                errItemLst.add(rltErrMsgMap.get("account") + num);
            }

            if (Cnst.EMPTY.equals(elem.getTaxRate())) {
                errItemLst.add(rltErrMsgMap.get("taxRate") + num);
            }

            if (elem.getAmount() == null) {
                errItemLst.add(rltErrMsgMap.get("amount")+ num);
            }

            int size = errItemLst.size();

            if (size == 0 || size == 3) {
                continue;
            }

            errMsgBuilder.append(errItemLst.get(0));

            if (errItemLst.size() == 2) {
                errMsgBuilder.append(Cnst.F_COMMA).append(errItemLst.get(1));
            }

            errMsgBuilder.append(rltErrMsgMap.get("inputMessage")).append(Cnst.SPRT);
        }

        if (errMsgBuilder.length() > 0) {
            return errMsgBuilder.deleteCharAt(errMsgBuilder.length() - 1).toString();
        }

        return null;
    }





    // privateメソッド ---------------------------------------------------------------

    //--------------------------------------------------------------------------------
    /**
     * エラーメッセージを返却する
     */
    //--------------------------------------------------------------------------------
    //TODO:メソッド名変更
    private Map<String, String> getErrMsgMap(String prmPre, String... prmKeyArr) {
        Map<String, String> errMsgMap = new HashMap<>();

        for (String elem : prmKeyArr) {
            errMsgMap.put(snakeToCamel(elem), messageSource.getMessage(prmPre + elem, null, Locale.JAPAN));
        }

        return errMsgMap;
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
