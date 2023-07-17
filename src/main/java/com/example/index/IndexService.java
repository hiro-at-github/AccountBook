package com.example.index;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private static final String ERR = "error";

    /**  */
    private static final String FLD_ERR_P = "field.error.";
    /**  */
    private static final String RLT_ERR_P = "relate.error.";
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

    /**  */
    private String rltErrMsg;

    /**  */
    private List<FieldError> rltFldErrLst;

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
            fldErrMsgMap = getErrMsgMap(FLD_ERR_P, ACCOUNT, AMOUNT, TAX + Cnst.UD_S + AMOUNT,
                    AMOUNT + Cnst.UD_S + MESSAGE);
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

        return errMsgBuilder.append(fldErrMsgMap.get(buildKey(AMOUNT, MESSAGE))).toString();
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    public String confirmAllItemsEntered(AccountTaxrateAmount[] prmATAArr) {
        if (rltErrMsgMap == null) {
            rltErrMsgMap = getErrMsgMap(RLT_ERR_P, ACCOUNT, TAX + Cnst.UD_S + RATE, AMOUNT,
                    INPUT + Cnst.UD_S + MESSAGE);
        }

        StringBuilder errMsgBuilder = new StringBuilder();
        int apndCnt = 0;

        for (int i = 0; i < prmATAArr.length; i++) {
            AccountTaxrateAmount elem = prmATAArr[i];
            List<String> errItemLst = new ArrayList<>();

            if (Cnst.EMPTY.equals(elem.getAccount())) {
                errItemLst.add(rltErrMsgMap.get(ACCOUNT));
            }

            if (Cnst.EMPTY.equals(elem.getTaxRate())) {
                errItemLst.add(rltErrMsgMap.get(buildKey(TAX, RATE)));
            }

            if (elem.getAmount() == null) {
                errItemLst.add(rltErrMsgMap.get(AMOUNT));
            }

            int size = errItemLst.size();

            if (size == 0 || size == 3) {
                continue;
            }

            errMsgBuilder.append(String.format("%02d", i + 1)).append("の").append(errItemLst.get(0));

            apndCnt++;
            if (apndCnt % 8 == 0) {
                errMsgBuilder.append(Cnst.SPRT);
            }

            if (errItemLst.size() == 2) {
                errMsgBuilder.append("と").append(errItemLst.get(1));

                apndCnt++;
                if (apndCnt % 8 == 0) {
                    errMsgBuilder.append(Cnst.SPRT);
                }
            }

            errMsgBuilder.append(Cnst.F_COMMA);
        }

        if (errMsgBuilder.length() > 0) {
            errMsgBuilder.insert(0, Cnst.SPRT).insert(0, rltErrMsgMap.get(buildKey(INPUT, MESSAGE)));

            return errMsgBuilder.deleteCharAt(errMsgBuilder.length() - 1).toString();
        }

        return null;
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    public List<String> checkItemMtd(ReceiptForm prmReceiptForm) {
        List<String> tmpLst = new ArrayList<>();

        List<FieldError> errLst = new ArrayList<>();

        AccountTaxrateAmount[] aTAArr = prmReceiptForm.getATAArr();
        for (int i = 0; i < aTAArr.length; i++) {
            AccountTaxrateAmount elem = aTAArr[i];
            List<String> tempLst = new ArrayList<>();
            String fmt = String.format("aTAArr[%d].", i);

            AccountTaxrateAmount aTA = new AccountTaxrateAmount();

            if (Cnst.EMPTY.equals(elem.getAccount())) {
                tempLst.add(apnd(fmt, ACCOUNT));
                aTA.setAccount(ERR);
                errLst.add(new FieldError("receiptForm", apnd(fmt, ACCOUNT), null));
            }

            if (Cnst.EMPTY.equals(elem.getTaxRate())) {
                tempLst.add(apnd(fmt, buildKey(TAX, RATE)));
                aTA.setTaxRate(ERR);
            }

            if (elem.getAmount() == null) {
                tempLst.add(apnd(fmt, AMOUNT));
                aTA.setAmntForCnfrm(ERR);
            }

            if (tempLst.size() == 0 || tempLst.size() == 3) {
                continue;
            }

            tmpLst.addAll(tempLst);
        }

        if (prmReceiptForm.getTaxAmountFor08() == null) {
            tmpLst.add(buildKey(TAX, AMOUNT));
        }

        return tmpLst;
    }

    public boolean isRelatedItemsEntered(ReceiptForm prmReceiptForm) {
        Map<Integer, List<String>> errItemMap = picupXxxItems(prmReceiptForm.getATAArr());
        //TODO:消費税額のコレクション化検討
        Integer taxAmountFor08 = prmReceiptForm.getTaxAmountFor08();
        Integer taxAmountFor10 = prmReceiptForm.getTaxAmountFor10();

        if (errItemMap.size() == 0 && taxAmountFor08 != null && taxAmountFor10 != null) {
            return true;
        }

        //
        if (rltErrMsgMap == null) {
            rltErrMsgMap = getErrMsgMap(RLT_ERR_P, ACCOUNT, TAX + Cnst.UD_S + RATE, AMOUNT,
                    INPUT + Cnst.UD_S + MESSAGE);
        }

        // エラーメッセージとエラー項目の組立
        StringBuilder msgBuilder = new StringBuilder();
        rltFldErrLst = new ArrayList<>();

        for (int elem : errItemMap.keySet()) {
            List<String> valLst = errItemMap.get(elem);

            msgBuilder.append(buildRltErrMsg(elem, valLst));

            String fmt = String.format("aTAArr[%d].", elem);

            //            rltFldErrLst.add(new FieldError("receiptForm", apnd(fmt, null), null));
            //TODO:メソッド作ってその戻り値をaddAll
            rltFldErrLst.addAll(buildRltFldErrLst(elem, valLst));
        }

        if (taxAmountFor08 == null) {
            msgBuilder.append("");
        }

        rltErrMsg = msgBuilder.toString();

        return false;
    }

    public String getRltErrMsg() {
        return rltErrMsg;
    }

    public List<FieldError> getRltFldErrLst() {
        return rltFldErrLst;
    }

    // privateメソッド ---------------------------------------------------------------

    //--------------------------------------------------------------------------------
    /**
     * エラーメッセージを返却する
     */
    //--------------------------------------------------------------------------------
    //TODO:メソッド名変更
    private Map<String, String> getErrMsgMap(String prmPrefix, String... prmKeyArr) {
        Map<String, String> errMsgMap = new HashMap<>();

        for (String elem : prmKeyArr) {
            errMsgMap.put(snakeToCamel(elem), messageSource.getMessage(prmPrefix + elem, null, Locale.JAPAN));
        }

        return errMsgMap;
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    //TODO:未入力の項目をピックアップする旨の名称に変更
    private Map<Integer, List<String>> picupXxxItems(AccountTaxrateAmount[] prmATAArr) {
        Map<Integer, List<String>> errItemMap = new LinkedHashMap<>();

        for (int i = 0; i < prmATAArr.length; i++) {
            AccountTaxrateAmount elem = prmATAArr[i];
            List<String> errItemLst = new ArrayList<>();

            if (Cnst.EMPTY.equals(elem.getAccount())) {
                errItemLst.add(ACCOUNT);
            }

            if (Cnst.EMPTY.equals(elem.getTaxRate())) {
                errItemLst.add(buildKey(TAX, RATE));
            }

            if (elem.getAmount() == null) {
                errItemLst.add(AMOUNT);
            }

            int size = errItemLst.size();
            if (size == 0 || size == 3) {
                continue;
            }

            errItemMap.put(i, errItemLst);
        }

        return errItemMap;
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    private List<String> tmpMtd(Map<Integer, List<String>> prmErrItemMap) {
        for (int elem : prmErrItemMap.keySet()) {
            List<String> valLst = prmErrItemMap.get(elem);

        }



        return null;
    }







    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    private String buildRltErrMsg(int prmKey, List<String> prmValLst) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02d", prmKey + 1)).append("の").append(rltErrMsgMap.get(prmValLst.get(0)));

        List<String> tempLst = new ArrayList<>();
        //        tempLst.add(apnd(String.format("%02d", prmKey + 1)), "の", rltErrMsgMap.get(prmValLst.get(0)));

        if (prmValLst.size() == 2) {
            builder.append("と").append(rltErrMsgMap.get(prmValLst.get(1)));
        }

        return builder.append(Cnst.F_COMMA).toString();
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    //TODO:メソッド名変更検討
    private List<FieldError> buildRltFldErrLst(int prmKey, List<String> prmValLst) {
        List<FieldError> errLst = new ArrayList<>();

        for (String elem : prmValLst) {
            FieldError err = new FieldError("receiptForm", apnd(String.format("aTAArr[%01d].", prmKey), elem), null);
            errLst.add(err);
        }

        return errLst;
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

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    private String buildKey(String... prmKeyArr) {
        StringBuilder builder = new StringBuilder();

        for (String elem : prmKeyArr) {
            builder.append(elem).append(Cnst.UD_S);
        }

        return snakeToCamel(builder.deleteCharAt(builder.length() - 1).toString());
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    private String apnd(String... prmStrArr) {
        StringBuilder builder = new StringBuilder();

        for (String elem : prmStrArr) {
            builder.append(elem);
        }

        return builder.toString();
    }

}
