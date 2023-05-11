package com.example.index;

import lombok.Data;

@Data
public class ReceiptForm {

    /** 年 */
    private String year;

    /** 月 */
    private String month;

    /** 日 */
    private String day;

    /** 勘定科目、税率、金額の配列 */
    private AccountTaxrateAmount[] aTAArr;

    /** 税額 */
    private String taxAmount;

    /** コンストラクタ */
    public ReceiptForm(String prmYear, String prmMonth, String prmDay, Integer prmLength) {

        year = prmYear;
        month = prmMonth;
        day = prmDay;

        if (prmLength != null) {
            aTAArr = new AccountTaxrateAmount[prmLength];
        }

    }

}
