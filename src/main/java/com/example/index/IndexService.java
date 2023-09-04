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
import java.util.stream.Stream;

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
    //    private static final String TAX_RATE = "tax_rate";
    /**  */
    //    private static final String TAX_AMOUNT = "tax_amount";
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
    private static final String DAY_S = "day";

    /**  */
    private static final String INPUT = "input";
    /**  */
    private static final String MESSAGE = "message";

    /**  */
    private static final String RECEIPT_FORM = "receiptForm";

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

    //----------------------------------------------------------------------------------------------------
    /**
     * コンストラクタ
     */
    //----------------------------------------------------------------------------------------------------
    public IndexService() {
        Calendar calendar = GregorianCalendar.getInstance();
        thisYear = calendar.get(Calendar.YEAR);
        currentDateArr = new String[] { String.valueOf(thisYear).substring(2),
                String.format("%02d", calendar.get(Calendar.MONTH) + 1),
                String.format("%02d", calendar.get(Calendar.DATE)) };
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 現在の日付を返却する
     *
     * @return 現在の日付
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getCurrentDate() {
        return currentDateArr;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 年の選択肢を返却する
     *
     * @return 年の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getYearArr() {
        return selOpts.getYearArr(thisYear);
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 月の選択肢を返却する
     *
     * @return 月の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getMonthArr() {
        return selOpts.getMonthArr();
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 日の選択肢を返却する
     *
     * @return 日の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getDayArr() {
        return selOpts.getDayArr();
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 科目の選択肢を返却する
     *
     * @return 科目の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public Map<String, String> getAccountMap() {
        return selOpts.getAccountMap();
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 消費税率の選択肢を返却する
     *
     * @return 消費税率の選択肢
     */
    //----------------------------------------------------------------------------------------------------
    public Map<String, Integer> getTaxRateMap() {
        return selOpts.getTaxRateMap();
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * バインディングリザルトのデータを元に、エラーメッセージを組み立てて返却する
     *
     * @param prmResult バインディングリザルト
     * @return エラーメッセージ
     */
    //----------------------------------------------------------------------------------------------------TODO:142行目から呼び出し
    public String buildFldErrMsg(BindingResult prmResult) {
        // エラーメッセージ用プロパティが取得済みか確認
        if (errMsgPropMap == null) {
            // エラーメッセージ用プロパティの取得
            errMsgPropMap = getErrMsgPrpMap();
        }

        return buildErrMsg(prmResult.getFieldErrors());
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 日付の適当性、未入力項目の確認をし結果を返却する。
     * 入力に不備がある場合はエラーメッセージの組立
     * (関連チェック)
     * 
     * @param prmReceiptForm レシートフォーム
     * @return 入力に不備がない場合は真。不備がある場合は偽
     */
    //----------------------------------------------------------------------------------------------------
    public boolean isRelatedItemsEntered(ReceiptForm prmReceiptForm) {
        // 年月日が日付として適当か確認
        boolean isCrctDt = isCorrectDate(prmReceiptForm.getYear(), prmReceiptForm.getMonth(), prmReceiptForm.getDay());

        // 科目・税率・金額の入力の有無の確認
        Map<Integer, List<String>> uentrdItemMap = pickUpUnenteredItemMap(prmReceiptForm.getATAArr());

        // 税額の取得
        Integer taxAmountFor08 = prmReceiptForm.getTaxAmountFor08();
        Integer taxAmountFor10 = prmReceiptForm.getTaxAmountFor10();

        if (isCrctDt && uentrdItemMap != null && uentrdItemMap.size() == 0
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

        if (!isCrctDt) {
            // 日付の入力に不備がある場合
            rltErrMsgLst.add(apnd(errMsgPropMap.get(DATE), errMsgPropMap.get(INCORRECT), Cnst.SPRT));
            rltFldErrLst.add(createFieldError(DAY_S));
        }

        if (uentrdItemMap == null) {
            // 全ての科目・税率・金額が未入力の場合
            rltErrMsgLst.add(apnd(errMsgPropMap.get(ACCOUNT), errMsgPropMap.get(snakeToCamel(F_DOT)),
                    errMsgPropMap.get(buildCamelCase(TAX, RATE)), errMsgPropMap.get(snakeToCamel(F_DOT)),
                    errMsgPropMap.get(AMOUNT), errMsgPropMap.get(snakeToCamel(NOT_ENTERED)), Cnst.SPRT));
            rltFldErrLst.add(createFieldError(apnd(A_T_A_0, ACCOUNT)));
            rltFldErrLst.add(createFieldError(apnd(A_T_A_0, buildCamelCase(TAX, RATE))));
            rltFldErrLst.add(createFieldError(apnd(A_T_A_0, AMOUNT)));
        } else {
            // 科目・税率・金額の組み合わせで未入力項目がある場合
            Pair<List<String>, List<FieldError>> errPair = buildCombiErrPair(uentrdItemMap);
            rltErrMsgLst.addAll(errPair.getFirst());
            rltFldErrLst.addAll(errPair.getSecond());
        }

        if (taxAmountFor08 == null && taxAmountFor10 == null) {
            // 税額のいずれもが未入力の場合
            rltErrMsgLst.add(apnd(errMsgPropMap.get(buildCamelCase(TAX, AMOUNT)),
                    errMsgPropMap.get(snakeToCamel(NOT_ENTERED)), Cnst.SPRT));
            rltFldErrLst.add(createFieldError(apnd(buildCamelCase(TAX, AMOUNT), FOR, P08)));
            rltFldErrLst.add(createFieldError(apnd(buildCamelCase(TAX, AMOUNT), FOR, P10)));
        }

        rltErrMsg = String.join(Cnst.EMPTY, rltErrMsgLst);

        return false;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 関連チェックのエラーメッセージを返却する
     * 
     * @return 関連チェックのエラーメッセージ
     */
    //----------------------------------------------------------------------------------------------------
    public String getRltErrMsg() {
        return rltErrMsg;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 関連チェックのフィールドエラーのリストを返却する
     * 
     * @return 関連チェックのフィールドエラーのリスト
     */
    //----------------------------------------------------------------------------------------------------
    public List<FieldError> getRltFldErrLst() {
        return rltFldErrLst;
    }

    // 以上値確認用publicメソッド。以下値加工用publicメソッド

    
    //----------------------------------------------------------------------------------------------------
    /**
     * 小計、合計を算出し、小計、合計、外税額8％、外税額10％を返す
     *
     * @param prmReceiptForm レシートフォーム
     * @return 小計、合計、外税額8％、外税額10％の配列
     */
    //----------------------------------------------------------------------------------------------------
//    public String[] getTotalAndTaxAmountArr(ReceiptForm prmReceiptForm) {
//        int subtotal = sumAmount(prmReceiptForm.getATAArr());
//
//        Pair<String[], Integer> pairForTaxAmount = toStringAndSumForTaxAmount(prmReceiptForm.getTaxAmountFor08(),
//                prmReceiptForm.getTaxAmountFor10());
//
//        return new String[] { String.valueOf(subtotal), String.valueOf(subtotal + pairForTaxAmount.getSecond()),
//                pairForTaxAmount.getFirst()[0], pairForTaxAmount.getFirst()[1] };
//    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 小計、合計を算出し返す
     *
     * @param prmReceiptForm レシートフォーム
     * @return 小計、合計の配列
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getSubtotalAndSumTotalArr(ReceiptForm prmReceiptForm) {
        int subtotal = Stream.of(prmReceiptForm.getATAArr()).filter(e -> e.getAmount() != null).mapToInt(e -> e.getAmount()).sum();
        
        int sumTotal = subtotal;
        
        if (prmReceiptForm.getTaxAmountFor08() != null) {
            sumTotal += prmReceiptForm.getTaxAmountFor08();
        }
        
        if (prmReceiptForm.getTaxAmountFor10() != null) {
            sumTotal += prmReceiptForm.getTaxAmountFor10();
        }
        
        return new String[] {String.valueOf(subtotal), String.valueOf(sumTotal)};
    }
    
    
    
    
    
    //----------------------------------------------------------------------------------------------------
    /**
     * レシートフォームの内容をファイルに登録(書き込み)し、登録したレシートの概要を返す
     *
     * @param prmReceiptForm レシートフォーム
     * @return 登録したレシートの概要
     */
    //----------------------------------------------------------------------------------------------------
    //TODO:メソッド名変更
    public Registered getRegistered(ReceiptForm prmReceiptForm) {
        // 戻り値を作る処理
        Registered registered = new Registered();
        registered.setDate(apnd(prmReceiptForm.getYear(), prmReceiptForm.getMonth(), prmReceiptForm.getDay()));
        
        String[] totalArr = getSubtotalAndSumTotalArr(prmReceiptForm);
        
        registered.setSubtotal(totalArr[0]);
        registered.setSumTotal(totalArr[1]);
        
        registered.setTaxAmount1(intToStr(prmReceiptForm.getTaxAmountFor08()));
        registered.setTaxAmount2(intToStr(prmReceiptForm.getTaxAmountFor10()));

        return registered;
    }

    //----------------------------------------------------------------------------------------------------
    // privateメソッド
    //----------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------------
    /**
     * メッセージプロパティを取得する
     * 
     * @return 取得したメッセージプロパティ
     */
    //----------------------------------------------------------------------------------------------------TODO:記述位置OK
    private Map<String, String> getErrMsgPrpMap() {
        Map<String, String> prpMap = new HashMap<>();

//        String[] codeArr = { AMOUNT, apnd(TAX, Cnst.US, AMOUNT), AMOUNT_RANGE,
//                F_DOT, DATE, ACCOUNT, apnd(TAX, Cnst.US, RATE), INCORRECT, NOT_ENTERED };
//        for (String elem : codeArr) {
//            prpMap.put(snakeToCamel(elem), messageSource.getMessage(PREFIX + elem, null, Locale.JAPAN));
//        }

        Stream<String> codeStream = Stream.of(AMOUNT, apnd(TAX, Cnst.US, AMOUNT), AMOUNT_RANGE,
                F_DOT, DATE, ACCOUNT, apnd(TAX, Cnst.US, RATE), INCORRECT, NOT_ENTERED);
        
        codeStream.forEach(e -> prpMap.put(snakeToCamel(e), messageSource.getMessage(PREFIX + e, null, Locale.JAPAN)));
        
        return prpMap;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 日付として適当か判定する
     * 
     * @param prmDate 年月日の文字列配列
     * @return 日付として適当な場合、真。不適当な場合、偽
     */
    //----------------------------------------------------------------------------------------------------
    private boolean isCorrectDate(String... prmDate) {
        DateFormat format = DateFormat.getDateInstance();
        format.setLenient(false);

        try {
            format.parse(apnd(prmDate[0], Cnst.SL, prmDate[1], Cnst.SL, prmDate[2]));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //----------------------------------------------------------------------------------------------------
    //TODO:メソッド名検討
    private FieldError createFieldError(String prmField) {
        return new FieldError(RECEIPT_FORM, prmField, null);
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //----------------------------------------------------------------------------------------------------
    //TODO:未入力の項目をピックアップする旨の名称に変更
    //TODO:問題ない場合：sizeが0のMapが返る。
    //問題ある場合：1以上、引数の長さ以下のMapが返る。
    //未入力の場合：nullを返す
    private Map<Integer, List<String>> pickUpUnenteredItemMap(AccountTaxrateAmount[] prmATAArr) {
        int uentrdCounter = 0;
        Map<Integer, List<String>> uentrdItemMap = new LinkedHashMap<>();

        int length = prmATAArr.length;
        for (int i = 0; i < length; i++) {
            AccountTaxrateAmount elem = prmATAArr[i];
            List<String> uentrdItemLst = new ArrayList<>();

            if (Cnst.EMPTY.equals(elem.getAccount())) {
                uentrdItemLst.add(ACCOUNT);
            }

            if (Cnst.EMPTY.equals(elem.getTaxRate())) {
                uentrdItemLst.add(buildCamelCase(TAX, RATE));
            }

            if (elem.getAmount() == null) {
                uentrdItemLst.add(AMOUNT);
            }

            switch (uentrdItemLst.size()) {
            case 0:
                continue;

            case 1:
            case 2:
                uentrdItemMap.put(i, uentrdItemLst);
                continue;

            case 3:
                uentrdCounter++;
                continue;

            default:
                continue;
            }
        }

        if (uentrdCounter == length) {
            return null;
        }

        return uentrdItemMap;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * フィールドエラーのフィールドをキーに、エラーメッセージを組み立てて返す
     * 
     * @param prmErrLst フィールドエラーのリスト
     * @return エラーメッセージ
     */
    //----------------------------------------------------------------------------------------------------
    private String buildErrMsg(List<FieldError> prmErrLst) {
        StringBuilder errMsgBuilder = new StringBuilder();

        for (FieldError elem : prmErrLst) {
            String key = null;
            if (elem.getField().contains(TAX)) {
                //                key = TAX_AMOUNT;
                key = apnd(TAX, Cnst.US, AMOUNT);
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

    //----------------------------------------------------------------------------------------------------
    /**
     * 科目・税率・金額(の組み合わせ)で1または2個の入力の場合にエラーメッセージを組み立てて返す
     * 20230831ここ
     */
    //----------------------------------------------------------------------------------------------------
    private String buildRltErrMsg(int prmKey, List<String> prmValLst) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02d", prmKey + 1)).append("の").append(errMsgPropMap.get(prmValLst.get(0)));

        if (prmValLst.size() == 2) {
            builder.append("と").append(errMsgPropMap.get(prmValLst.get(1)));
        }

        return builder.append(Cnst.F_COMMA).toString();
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //----------------------------------------------------------------------------------------------------
    //TODO:メソッド名変更検討
    private List<FieldError> buildRltFldErrLst(int prmKey, List<String> prmValLst) {
        List<FieldError> errLst = new ArrayList<>();

        for (String elem : prmValLst) {
            FieldError err = new FieldError("receiptForm", apnd(String.format("aTAArr[%01d].", prmKey), elem), null);
            errLst.add(err);
        }

        return errLst;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //----------------------------------------------------------------------------------------------------
    //科目・税率・金額の組み合わせで未入力項目がある場合
    private Pair<List<String>, List<FieldError>> buildCombiErrPair(Map<Integer, List<String>> prmErrItemMap) {
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

    //----------------------------------------------------------------------------------------------------
    /**
     * スネークケースからキャメルケースに変換して返却する
     *
     * @param prmSnakeCase スネークケースの文字列
     * @return キャメルケースに変換した文字列
     */
    //----------------------------------------------------------------------------------------------------
    private String snakeToCamel(String prmSnakeCase) {
        String[] split = prmSnakeCase.split(Cnst.US);
        StringBuilder builder = new StringBuilder(split[0]);

        for (int i = 1; i < split.length; i++) {
            builder.append(split[i].substring(0, 1).toUpperCase()).append(split[i].substring(1));
        }

        return builder.toString();
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 引数の文字列配列を連結してキャメルケースの文字列を作成し返却する
     * 
     * @param prmStrArr 処理対象の文字列配列
     * @return 作成したキャメルケースの文字列
     */
    //----------------------------------------------------------------------------------------------------
    private String buildCamelCase(String... prmStrArr) {
        StringBuilder builder = new StringBuilder();

        for (String elem : prmStrArr) {
            builder.append(elem).append(Cnst.US);
        }

        return snakeToCamel(builder.deleteCharAt(builder.length() - 1).toString());
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //----------------------------------------------------------------------------------------------------
    private String apnd(String... prmStrArr) {
        StringBuilder builder = new StringBuilder();

        for (String elem : prmStrArr) {
            builder.append(elem);
        }

        return builder.toString();
    }

    //TODO:以上値確認用privateメソッド。以下値加工用privateメソッド

    //----------------------------------------------------------------------------------------------------
    /**
     * 金額の和を求めて返す
     *
     * @param prmATAArr 科目・税率・金額の配列
     * @return 金額の和
     */
    //----------------------------------------------------------------------------------------------------
    private int sumAmount(AccountTaxrateAmount[] prmATAArr) {
        int sumOfAmount = 0;

        for (AccountTaxrateAmount elem : prmATAArr) {
            Integer amount = elem.getAmount();

            if (amount != null) {
                sumOfAmount += amount;
            }
        }

        return sumOfAmount;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 外税額の文字列化と外税額の和を求めて返す
     * 
     * @param prmTaxAmountArr 外税額の配列
     * @return first：文字列化した外税額の配列。second：外税額の和
     */
    //----------------------------------------------------------------------------------------------------
    private Pair<String[], Integer> toStringAndSumForTaxAmount(Integer... prmTaxAmountArr) {
        String[] strArr = new String[prmTaxAmountArr.length];
        int sum = 0;

        for (int i = 0; i < prmTaxAmountArr.length; i++) {
            Integer elem = prmTaxAmountArr[i];

            if (elem == null) {
                strArr[i] = Cnst.DASH;

                continue;
            }

            strArr[i] = String.valueOf(elem);
            sum += elem;
        }

        return Pair.of(strArr, sum);
    }
    
    private String intToStr(Integer prmInt) {
        return prmInt == null ? Cnst.DASH : String.valueOf(prmInt);
    }
    
    
    
    
}
