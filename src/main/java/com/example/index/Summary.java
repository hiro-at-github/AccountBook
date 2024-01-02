package com.example.index;

import lombok.Data;

//----------------------------------------------------------------------------------------------------
/**
 * 登録したレシートの概要を表すクラス
 */
//----------------------------------------------------------------------------------------------------
@Data
public class Summary {
    /** 日付 */
    private String date;

    /** 小計 */
    private String subtotal;

    /** 外税額1 */
    private String taxAmount1;

    /** 外税額2 */
    private String taxAmount2;

    /** 合計 */
    private String sumTotal;
}
