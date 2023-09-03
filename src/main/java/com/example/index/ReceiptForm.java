package com.example.index;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

//----------------------------------------------------------------------------------------------------
/**
 *
 */
//----------------------------------------------------------------------------------------------------
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

    /** 小計 */
    private String subtotal;

    /** 合計 */
    private String sumTotal;

    /** エラーメッセージ */
    private String errorMessage;

    /** 登録したレシートの合計のリスト */
    private List<Registered> rgstedLst;

    //----------------------------------------------------------------------------------------------------
    /**
     * コンストラクタ
     */
    //----------------------------------------------------------------------------------------------------
    public ReceiptForm(Integer prmLength, String... prmDate) {
        if (prmLength != null) {
            aTAArr = new AccountTaxrateAmount[prmLength];
        }

        if (prmDate != null) {
            year = prmDate[0];
            month = prmDate[1];
            day = prmDate[2];
        }
    }
}
