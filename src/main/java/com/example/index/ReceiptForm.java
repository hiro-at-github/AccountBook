package com.example.index;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

//--------------------------------------------------------------------------------
/**
 * 
 */
//--------------------------------------------------------------------------------
@Data
public class ReceiptForm {
    /** 年 */
    private String year;

    /** 月 */
    private String month;

    /** 日 */
    private String day;

    /** 勘定科目、消費税率、金額の配列 */
    @Valid
    private AccountTaxrateAmount[] aTAArr;

    /** 税額(8%) */
    @Min(0)
    @Max(9999999)
    private Integer taxAmountFor08;

    /** 税額(10%) */
    @Min(0)
    @Max(9999999)
    private Integer taxAmountFor10;

    /** エラーメッセージ */
    private String errorMessage;

    /** 登録したレシートの合計の配列 */
    private Registered[] rgstedArr;
    
    //--------------------------------------------------------------------------------
    /**
     * コンストラクタ
     */
    //--------------------------------------------------------------------------------
    public ReceiptForm(String prmYear, String prmMonth, String prmDay, Integer prmLength) {
        year = prmYear;
        month = prmMonth;
        day = prmDay;

        if (prmLength != null) {
            aTAArr = new AccountTaxrateAmount[prmLength];

            //            for (int i = 0; i < prmLength; i++) {
            //                aTAArr[i].setNumber(String.format("%02d", i));
            //            }
        }
    }
}
