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
    
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(Cnst.EMPTY.equals(account) ? Cnst.ZERO : Cnst.ONE)
        .append(Cnst.EMPTY.equals(taxRate) ? Cnst.ZERO : Cnst.ONE)
        .append(amount == null ? Cnst.ZERO : Cnst.ONE);
        
        return sb.toString();
    }
    
    public String getStatus2() {
        StringBuilder sb = new StringBuilder();
        
        if (account.equals(Cnst.EMPTY)) {
            sb.append("account");
        }
        
        if (taxRate.equals(Cnst.EMPTY)) {
            sb.append(Cnst.US).append("taxRate");
        }
        
        if (amount == null) {
            sb.append("amount");
        }
        
        return sb.toString();
    }
    
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
