package com.example.index;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
    @Min(0)
    @Max(9999999)
    private Integer amount;

}
