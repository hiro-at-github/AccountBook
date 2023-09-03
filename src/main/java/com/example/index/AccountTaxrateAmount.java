package com.example.index;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

//----------------------------------------------------------------------------------------------------
/**
 * 勘定科目、消費税率、金額のクラス
 */
//----------------------------------------------------------------------------------------------------
@Data
public class AccountTaxrateAmount {
    /** 勘定科目 */
    private String account;

    /** 消費税率 */
    private String taxRate;

    /** 金額 */
    @Min(0)
    @Max(9999999)
    private Integer amount;

    /** 番号(確認用) */
    private String numForCnfrm;

    /** 金額(確認用) */
    private String amntForCnfrm;
}
