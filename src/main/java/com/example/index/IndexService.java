package com.example.index;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.common.Cnst;

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
    private static final String PREFIX = "idx.";
    /**  */
    //    private static final String RLT_ERR_P = "relate.error.";
    
    /**  */
    private static final String F_DOT = "f_dot";
    /**  */
    private static final String DATE = "date";
    
    
    
    /**  */
    private static final String ACCOUNT = "account";
    /**  */
    private static final String AMOUNT = "amount";
    /**  */
    private static final String TAX = "tax";
    /**  */
    private static final String RATE = "rate";
    /**  */
    private static final String TAX_RATE = "tax_rate";
    /**  */
    private static final String TAX_AMOUNT = "tax_amount";
    /**  */
    private static final String AMOUNT_RANGE = "amount_range";
    /**  */
    private static final String INCORRECT = "incorrect";
    /**  */
    private static final String NOT_ENTERED = "not_entered";

    //TODO:名称変更検討
    /**  */
    private static final String A_T_A_0 = "aTAArr[0].";
    /**  */
    private static final String FOR = "For";
    /**  */
    private static final String P08 = "08";
    /**  */
    private static final String P10 = "10";
    
    
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
    private Map<String, String> errMsgPropMap;

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
        // エラーメッセージ用プロパティが取得済みか確認
        if (errMsgPropMap == null) {
         // エラーメッセージ用プロパティの取得
            errMsgPropMap = getErrMsgPrpMap();
        }

        StringBuilder errMsgBuilder = new StringBuilder();

        List<FieldError> errLst = prmResult.getFieldErrors();
        for (FieldError elem : errLst) {
            String key = null;
            if (elem.getField().contains(TAX)) {
                key = TAX_AMOUNT;
            } else {
                key = AMOUNT;
            }

            String msg = errMsgPropMap.get(snakeToCamel(key));

            if (errMsgBuilder.indexOf(msg) > -1) {
                continue;
            }

            if (errMsgBuilder.length() > 0) {
                errMsgBuilder.append(Cnst.F_COMMA);
            }

            errMsgBuilder.append(msg);
        }

        return errMsgBuilder.append(errMsgPropMap.get(snakeToCamel(AMOUNT_RANGE))).toString();
    }


    //TODO:ここから。
    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    public boolean isRelatedItemsEntered(ReceiptForm prmReceiptForm) {
        // 年月日が日付として適当か確認
        boolean isDateXxx = isDateXxx(prmReceiptForm.getYear(), prmReceiptForm.getMonth(), prmReceiptForm.getDay());

        // 科目・税率・金額の入力の有無の確認
        Map<Integer, List<String>> errItemMap = picupXxxItems(prmReceiptForm.getATAArr());

        // 税額の取得
        Integer taxAmountFor08 = prmReceiptForm.getTaxAmountFor08();
        Integer taxAmountFor10 = prmReceiptForm.getTaxAmountFor10();

        if (isDateXxx && errItemMap != null && errItemMap.size() == 0
                && (taxAmountFor08 != null || taxAmountFor10 != null)) {
            // 日付、科目・税率・金額、税額の入力に不備がなければ真を返却
            return true;
        }

        // 以下、入力に不備がある場合の処理
        // エラーメッセージ、フィールドエラーの作成、偽の返却

        // エラーメッセージ用プロパティが取得済みか確認
        if (errMsgPropMap == null) {
            // エラーメッセージ用プロパティの取得
            errMsgPropMap = getErrMsgPrpMap();
        }

        // エラーメッセージとフィールドエラーの作成
        List<String> rltErrMsgLst = new ArrayList<>();
        rltFldErrLst = new ArrayList<>();

        if (!isDateXxx) {
            // 日付の入力に不備がある場合
            rltErrMsgLst.add(apnd(errMsgPropMap.get(DATE), errMsgPropMap.get(INCORRECT), Cnst.SPRT));
            rltFldErrLst.add(createFieldError("day"));
        }

        if (errItemMap == null) {
            // 全ての科目・税率・金額が未入力の場合
            rltErrMsgLst.add(apnd(errMsgPropMap.get(ACCOUNT), errMsgPropMap.get(snakeToCamel(F_DOT)),
                    errMsgPropMap.get(snakeToCamel(TAX_RATE)), errMsgPropMap.get(snakeToCamel(F_DOT)),
                    errMsgPropMap.get(AMOUNT), errMsgPropMap.get(snakeToCamel(NOT_ENTERED)), Cnst.SPRT));
            rltFldErrLst.add(createFieldError(apnd(A_T_A_0, ACCOUNT)));
            rltFldErrLst.add(createFieldError(apnd(A_T_A_0, snakeToCamel(TAX_RATE))));
            rltFldErrLst.add(createFieldError(apnd(A_T_A_0, AMOUNT)));
        } else {
            // 科目・税率・金額の組み合わせで未入力項目がある場合
            Pair<List<String>, List<FieldError>> pair = Y230723_1(errItemMap);
            rltErrMsgLst.addAll(pair.getFirst());
            rltFldErrLst.addAll(pair.getSecond());
        }

        if (taxAmountFor08 == null && taxAmountFor10 == null) {
            // 税額のいずれもが未入力の場合
            rltErrMsgLst.add(apnd(errMsgPropMap.get(snakeToCamel(TAX_AMOUNT)), errMsgPropMap.get(snakeToCamel(NOT_ENTERED)), Cnst.SPRT));
            rltFldErrLst.add(createFieldError(apnd(snakeToCamel(TAX_AMOUNT), FOR, P08)));
            rltFldErrLst.add(createFieldError(apnd(snakeToCamel(TAX_AMOUNT), FOR, P10)));
        }

        rltErrMsg = String.join(Cnst.EMPTY, rltErrMsgLst);

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
    private Map<String, String> getErrMsgPrpMap() {
        Map<String, String> prpMap = new HashMap<>();
        
        String[] codeArr = {AMOUNT, TAX_AMOUNT, AMOUNT_RANGE,
                F_DOT, DATE, ACCOUNT, TAX_RATE, INCORRECT, NOT_ENTERED};
        for (String elem : codeArr) {
            prpMap.put(snakeToCamel(elem), messageSource.getMessage(PREFIX + elem, null, Locale.JAPAN));
//            prpMap.put(elem, messageSource.getMessage(PREFIX + elem, null, Locale.JAPAN));
        }
        
        return prpMap;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * 日付の整合性を確認する旨
     */
    //--------------------------------------------------------------------------------
    //TODO:メソッド名変更。日付が不正の場合は「日付が不正」だけをメッセージとするため、戻り値はbooleanで可
    private boolean isDateXxx(String... prmDate) {
        DateFormat format = DateFormat.getDateInstance();
        format.setLenient(false);

        try {
            format.parse(apnd(prmDate[0], "/", prmDate[1], "/", prmDate[2]));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    //TODO:メソッド名検討
    private FieldError createFieldError(String prmField) {
        return new FieldError("receiptForm", prmField, null);
    }

    //    //--------------------------------------------------------------------------------
    //    /**
    //     * ダミー
    //     */
    //    //--------------------------------------------------------------------------------
    //    //TODO:メソッド名、引数名変更
    //    private List<String> from0721_1(boolean prmIsDateXxx, Map<Integer, List<String>> prmErrItemMap,
    //            Integer... prmTaxAmountArr) {
    //
    //        if (prmIsDateXxx && prmErrItemMap != null && prmErrItemMap.size() == 0
    //                && (prmTaxAmountArr[0] != null || prmTaxAmountArr[1] != null)) {
    //            return null;
    //        }
    //
    //        //TODO:エラーメッセージ用のプロパティを取得(展開)する旨のコメント
    //        if (rltErrMsgMap == null) {
    //            rltErrMsgMap = getErrMsgMap(RLT_ERR_P, ACCOUNT, TAX + Cnst.UD_S + RATE, AMOUNT,
    //                    INPUT + Cnst.UD_S + MESSAGE);
    //        }
    //
    //        //TODO:名称変更
    //        List<String> rtnLst = new ArrayList<String>();
    //
    //        //        if (!prmIsDateXxx) {
    //        //            //TODO:プロパティから取得。改行
    //        //            rtnLst.add("日付が不正");
    //        //        }
    //
    //        if (prmErrItemMap == null) {
    //            //TODO:プロパティから取得。改行
    //            //            rtnLst.add("科目・税率・金額が未入力");
    //            rtnLst.add("科目・税率・金額");
    //        } else if (prmErrItemMap.size() > 0) {
    //            //TODO:メソッド化もしくは直書き
    //        }
    //
    //        if (prmTaxAmountArr[0] == null && prmTaxAmountArr[1] == null) {
    //            //TODO:プロパティから取得。改行
    //            //            rtnLst.add("税額が未入力");
    //            rtnLst.add("税額");
    //        }
    //
    //        if (!prmIsDateXxx) {
    //            //TODO:プロパティから取得。改行。rtnLstの先頭に挿入
    //            //      rtnLst.add("日付が不正");
    //        }
    //
    //        return rtnLst;
    //    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    //TODO:未入力の項目をピックアップする旨の名称に変更
    //TODO:問題ない場合：sizeが0のMapが返る。
    //問題ある場合：1以上、引数の長さ以下のMapが返る。
    //未入力の場合：nullを返す
    private Map<Integer, List<String>> picupXxxItems(AccountTaxrateAmount[] prmATAArr) {
        int emptyCounter = 0;
        Map<Integer, List<String>> errItemMap = new LinkedHashMap<>();

        int length = prmATAArr.length;
        for (int i = 0; i < length; i++) {
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

            switch (errItemLst.size()) {
            case 0:
                continue;

            case 1:
            case 2:
                errItemMap.put(i, errItemLst);
                continue;

            case 3:
                emptyCounter++;
                continue;

            default:
                continue;
            }
        }

        if (emptyCounter == length) {
            return null;
        }

        return errItemMap;
    }

    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    private String buildRltErrMsg(int prmKey, List<String> prmValLst) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02d", prmKey + 1)).append("の").append(errMsgPropMap.get(prmValLst.get(0)));

        if (prmValLst.size() == 2) {
            builder.append("と").append(errMsgPropMap.get(prmValLst.get(1)));
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
     * ダミー
     */
    //--------------------------------------------------------------------------------
    private Pair<List<String>, List<FieldError>> Y230723_1(Map<Integer, List<String>> prmErrItemMap) {
        List<String> lRltErrMsgLst = new ArrayList<>();
        List<FieldError> lRltFldErrLst = new ArrayList<>();

        int i = 0;
        for (int elem : prmErrItemMap.keySet()) {
            List<String> valLst = prmErrItemMap.get(elem);

            lRltErrMsgLst.add(buildRltErrMsg(elem, valLst));
            i++;
            if (i == 8) {
                i = 0;
                lRltErrMsgLst.add(Cnst.SPRT);
            }
            
            lRltFldErrLst.addAll(buildRltFldErrLst(elem, valLst));
        }

        int lastIndex = lRltErrMsgLst.size() - 1;
        String lastElem = lRltErrMsgLst.get(lastIndex);
        if (lastElem.equals(Cnst.SPRT)) {
            lRltErrMsgLst.remove(lastIndex);
        }
        
        int lstIndex = lRltErrMsgLst.size() - 1;
        String lstElem = lRltErrMsgLst.get(lstIndex);
        lRltErrMsgLst.set(lstIndex, lstElem.substring(0, lstElem.length() - 1));
        
        lRltErrMsgLst.add(apnd(errMsgPropMap.get(snakeToCamel(NOT_ENTERED)), Cnst.SPRT));
        
        return Pair.of(lRltErrMsgLst, lRltFldErrLst);
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
