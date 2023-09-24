package com.example.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.common.Cmn;
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
    //----------------------------------------------------------------------------------------------------
    public String buildFldErrMsg(BindingResult prmResult) {
        // エラーメッセージ用プロパティが取得済みか確認
        if (errMsgPropMap == null) {
            // エラーメッセージ用プロパティの取得
            errMsgPropMap = getErrMsgPrpMap();
        }
        
        // フィールドエラーのフィールドをキーに、エラーメッセージを組み立てて返す
        //TODO:キーの定数化2ヵ所
        Stream<String> strStr =
                prmResult.getFieldErrors().stream().map(e -> errMsgPropMap.get(e.getField().contains(TAX) ? "taxAmount" : AMOUNT)).distinct();
            
        String joined = String.join(Cnst.F_COMMA, strStr.collect(Collectors.toList()));
            
        return String.join(Cnst.EMPTY, joined, errMsgPropMap.get(Cmn.arrToCamel(AMOUNT_RANGE.split(Cnst.US))));
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
        boolean isCrctDt = Cmn.isCorrectDate(prmReceiptForm.getYear(), prmReceiptForm.getMonth(),
                prmReceiptForm.getDay());

        // 科目・税率・金額の入力の有無の確認
//        Map<Integer, List<String>> uentrdItemMap = pickUpUnenteredItemMap(prmReceiptForm.getATAArr());
        List<AccountTaxrateAmount> uentrdItemLst = pickUpUnenteredItem(prmReceiptForm.getATAArr());

        // 税額の取得
        Integer taxAmountFor08 = prmReceiptForm.getTaxAmountFor08();
        Integer taxAmountFor10 = prmReceiptForm.getTaxAmountFor10();

        if (isCrctDt && uentrdItemLst != null && uentrdItemLst.size() == 0
                && (taxAmountFor08 != null || taxAmountFor10 != null)) {
            // 日付、科目・税率・金額、税額の入力に不備がなければ真を返却
            return true;
        }

        // 以下、入力に不備がある場合の処理
        // エラーメッセージ、フィールドエラーの作成、偽の返却

        // レシートフォーム用のフィールドエラーを生成して返す
        Function<String, FieldError> cretErr = s -> new FieldError(RECEIPT_FORM, s, null);

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
            rltErrMsgLst.add(String.join(Cnst.EMPTY, errMsgPropMap.get(DATE), errMsgPropMap.get(INCORRECT), Cnst.SPRT));
            rltFldErrLst.add(cretErr.apply(DAY_S));
        }

        if (uentrdItemLst == null) {
            // 全ての科目・税率・金額が未入力の場合
            rltErrMsgLst.add(String.join(Cnst.EMPTY, errMsgPropMap.get(ACCOUNT),
                    errMsgPropMap.get(Cmn.arrToCamel(F_DOT.split(Cnst.US))),
                    errMsgPropMap.get(Cmn.arrToCamel(TAX, RATE)),
                    errMsgPropMap.get(Cmn.arrToCamel(F_DOT.split(Cnst.US))),
                    errMsgPropMap.get(AMOUNT), errMsgPropMap.get(Cmn.arrToCamel(NOT_ENTERED.split(Cnst.US))),
                    Cnst.SPRT));
            rltFldErrLst.add(cretErr.apply(String.join(Cnst.EMPTY, A_T_A_0, ACCOUNT)));
            rltFldErrLst.add(cretErr.apply(String.join(Cnst.EMPTY, A_T_A_0, Cmn.arrToCamel(TAX, RATE))));
            rltFldErrLst.add(cretErr.apply(String.join(Cnst.EMPTY, A_T_A_0, AMOUNT)));
        } else {
            // 科目・税率・金額の組み合わせで未入力項目がある場合
            rltErrMsgLst.addAll(buildRltErrMsgLst(uentrdItemLst));
            rltFldErrLst.addAll(buildRltFldErrLst(uentrdItemLst));
        }

        if (taxAmountFor08 == null && taxAmountFor10 == null) {
            // 税額のいずれもが未入力の場合
            rltErrMsgLst.add(String.join(Cnst.EMPTY, errMsgPropMap.get(Cmn.arrToCamel(TAX, AMOUNT)),
                    errMsgPropMap.get(Cmn.arrToCamel(NOT_ENTERED.split(Cnst.US))), Cnst.SPRT));
            rltFldErrLst.add(cretErr.apply(String.join(Cnst.EMPTY, Cmn.arrToCamel(TAX, AMOUNT), FOR, P08)));
            rltFldErrLst.add(cretErr.apply(String.join(Cnst.EMPTY, Cmn.arrToCamel(TAX, AMOUNT), FOR, P10)));
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
     * 小計、合計を算出し返す
     *
     * @param prmReceiptForm レシートフォーム
     * @return 小計、合計の配列
     */
    //----------------------------------------------------------------------------------------------------
    public String[] getSubtotalAndSumTotalArr(ReceiptForm prmReceiptForm) {
        int subtotal = Stream.of(prmReceiptForm.getATAArr()).filter(e -> e.getAmount() != null)
                .mapToInt(e -> e.getAmount()).sum();

        int sumTotal = subtotal;

        if (prmReceiptForm.getTaxAmountFor08() != null) {
            sumTotal += prmReceiptForm.getTaxAmountFor08();
        }

        if (prmReceiptForm.getTaxAmountFor10() != null) {
            sumTotal += prmReceiptForm.getTaxAmountFor10();
        }

        return new String[] { String.valueOf(subtotal), String.valueOf(sumTotal) };
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
        registered.setDate(
                String.join(Cnst.EMPTY, prmReceiptForm.getYear(), prmReceiptForm.getMonth(), prmReceiptForm.getDay()));

        String[] totalArr = getSubtotalAndSumTotalArr(prmReceiptForm);

        registered.setSubtotal(totalArr[0]);
        registered.setSumTotal(totalArr[1]);

        Function<Integer, String> intToStr = i -> i == null ? Cnst.DASH : String.valueOf(i);
        registered.setTaxAmount1(intToStr.apply(prmReceiptForm.getTaxAmountFor08()));
        registered.setTaxAmount2(intToStr.apply(prmReceiptForm.getTaxAmountFor10()));

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
    //----------------------------------------------------------------------------------------------------
    //TODO:230910StreamからMapへの変換方法がないか？
    private Map<String, String> getErrMsgPrpMap() {
        Map<String, String> prpMap = new HashMap<>();

        //        String[] codeArr = { AMOUNT, apnd(TAX, Cnst.US, AMOUNT), AMOUNT_RANGE,
        //                F_DOT, DATE, ACCOUNT, apnd(TAX, Cnst.US, RATE), INCORRECT, NOT_ENTERED };
        //        for (String elem : codeArr) {
        //            prpMap.put(snakeToCamel(elem), messageSource.getMessage(PREFIX + elem, null, Locale.JAPAN));
        //        }

        Stream<String> codeStream = Stream.of(AMOUNT, String.join(Cnst.EMPTY, TAX, Cnst.US, AMOUNT), AMOUNT_RANGE,
                F_DOT, DATE, ACCOUNT, String.join(Cnst.EMPTY, TAX, Cnst.US, RATE), INCORRECT, NOT_ENTERED);

        codeStream.forEach(e -> prpMap.put(Cmn.arrToCamel(e.split(Cnst.US)),
                messageSource.getMessage(PREFIX + e, null, Locale.JAPAN)));

        return prpMap;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 科目・税率・金額(の組み合わせ)で1または2個の入力の場合に未入力の項目をピックアップして返す
     * 
     * @param prmATAArr　科目・税率・金額の配列
     * @return 配列の全ての科目・税率・金額が未入力の場合：null。
     *         入力の状態に不備がある場合：1以上、引数の長さ以下のList
     *         入力の状態に不備がない場合：size0のList
     */
    //----------------------------------------------------------------------------------------------------
    private List<AccountTaxrateAmount> pickUpUnenteredItem(AccountTaxrateAmount[] prmATAArr) {
        List<AccountTaxrateAmount> tmpLst = new ArrayList<>();

        int emptyCnt = 0;

        for (int i = 0; i < prmATAArr.length; i++) {
            AccountTaxrateAmount elem = prmATAArr[i];

            switch (elem.getEmptyItemLst().size()) {
            case 3:
                emptyCnt++;

                continue;

            case 0:
                continue;

            default:
                elem.setNo(i);

                tmpLst.add(elem);
            }

        }

//        if (emptyCnt == prmATAArr.length) {
//            return null;
//        }

        Stream<Integer> sizeStr = Arrays.stream(prmATAArr).map(e -> e.getEmptyItemLst().size());
        
        //TODO:230922ここ
//        if (sizeStr.filter(e -> e == 3).count() == prmATAArr.length) {
//            return null;
//        }
//        
//        if (sizeStr.filter(e -> e == 0).count() == 0) {
//            return new ArrayList<AccountTaxrateAmount>();
//        }
        
        List<Integer> tmpList = sizeStr.collect(Collectors.toList());
        
//        if (tmpList.stream().filter(e -> e == 3).count() == prmATAArr.length) {
//            return null;
//        }
//        
//        if (tmpList.stream().filter(e -> e == 0).count() == prmATAArr.length) {
//            return new ArrayList<AccountTaxrateAmount>();
//        }
        
        
        
        List<AccountTaxrateAmount> aTALst = Arrays.asList(prmATAArr);
        
        AtomicInteger aI = new AtomicInteger(1);
        
        aTALst.forEach(e -> e.setNo(aI.getAndIncrement()));
        
//        Stream<AccountTaxrateAmount> objStr = aTALst.stream().filter(e -> e.getEmptyItemLst().size() != 3);
//        
//        if (objStr.count() == 0) {
//            return null;
//        }
        
        Stream<AccountTaxrateAmount> abcStr = aTALst.stream().filter(e -> e.getEmptyItemLst().size() != 0);
        
        long leng = abcStr.count();
        
        if (leng == 0) {
            return new ArrayList<AccountTaxrateAmount>();
        }
        
        
        
        
        return abcStr.collect(Collectors.toList());
    }

    private List<String> buildRltErrMsgLst(List<AccountTaxrateAmount> prmATALst) {
        //        // 科目・税率・金額の組み合わせで1または2個の入力の場合にエラーメッセージを組み立てて返す
        //        Function<AccountTaxrateAmount, String> func = t -> {
        //            List<String> emptyItemLst = t.getEmptyItemLst();
        //            StringBuilder sb = new StringBuilder();
        //            sb.append(String.format("%02d", t.getNo() + 1)).append("の").append(errMsgPropMap.get(emptyItemLst.get(0)));
        //
        //            if (emptyItemLst.size() == 2) {
        //                sb.append("と").append(errMsgPropMap.get(emptyItemLst.get(1)));
        //            }
        //
        //            return sb.toString();
        //        };
        //
        //        List<String> appliedLst = prmATALst.stream().map(e -> func.apply(e)).collect(Collectors.toList());

        Stream<String> strStr = prmATALst.stream().map(e -> {
            List<String> emptyItemLst = e.getEmptyItemLst();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%02d", e.getNo() + 1)).append("の").append(errMsgPropMap.get(emptyItemLst.get(0)));

            if (emptyItemLst.size() == 2) {
                sb.append("と").append(errMsgPropMap.get(emptyItemLst.get(1)));
            }

            return sb.toString();

        });

        List<String> appliedLst = strStr.collect(Collectors.toList());

        // 末尾の要素を除き、各要素の文字列の末尾に全角カンマを加える
        int lastIndex = appliedLst.size() - 1;
        String lastElem = appliedLst.get(lastIndex);
        appliedLst.replaceAll(e -> String.join(Cnst.EMPTY, e, Cnst.F_COMMA));
        appliedLst.set(lastIndex, lastElem);

        // 「…が未入力」を加える
        //TODO:キーの定数化
        appliedLst.add(errMsgPropMap.get("notEntered"));

        return appliedLst;
    }

    private List<FieldError> buildRltFldErrLst(List<AccountTaxrateAmount> prmATALst) {
        //TODO:キーの定数化
        return prmATALst.stream()
                .flatMap(e -> e.getEmptyItemLst().stream()
                        .map(f -> new FieldError("receiptForm",
                                String.join(Cnst.EMPTY, String.format("aTAArr[%01d].", e.getNo()), f), null)))
                .collect(Collectors.toList());
    }
}
