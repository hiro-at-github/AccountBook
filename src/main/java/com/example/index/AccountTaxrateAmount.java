package com.example.index;

import lombok.Data;

@Data
public class AccountTaxrateAmount {

    /** 番号 */
    private String number;

    /** 勘定科目 */
    private String account;

    /** 税率 */
    private String taxRate;

    /** 金額 */
//    private String amount;
    private Integer amount;

}
