package com.example.index;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.example.common.Cnst;

import lombok.Data;

//----------------------------------------------------------------------------------------------------
/**
 * 勘定科目、消費税率、金額のクラス
 */
//----------------------------------------------------------------------------------------------------
@Data
public class AccountTaxrateAmount {
    /** 番号 */
    private int no;
    
    /** 勘定科目 */
    private String account;

    /** 消費税率 */
    private String taxRate;

    /** 金額 */
    @Min(0)
    @Max(9999999)
    private Integer amount;

//    /** 番号(確認用) */
//    private String numForCnfrm;
//
//    /** 金額(確認用) */
//    private String amntForCnfrm;
    
    //TODO:キーの定数化
    public List<String> getEmptyItemLst() {
        List<String> emptyItemLst = new ArrayList<>();
        
        if (account.equals(Cnst.EMPTY)) {
            emptyItemLst.add("account");
        }
        
        if (taxRate.equals(Cnst.EMPTY)) {
            emptyItemLst.add("taxRate");
        }
        
        if (amount == null) {
            emptyItemLst.add("amount");
        }
        
        return emptyItemLst;
    }
}
