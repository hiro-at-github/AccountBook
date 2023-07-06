package com.example.index;

public class Cnst {
    /** 年の選択肢の個数 */
    public static final int LENGTH_OF_YEAR = 2;

    //--------------------------------------------------------------------------------
    /** 改行コード */
    public static final String SPRT = System.getProperty("line.separator");
    /** 空文字列 */
    public static final String EMPTY = "";
    /** ドット */
    public static final String DOT = ".";
    /**  */
    //TODO:名称変更
    public static final String UD_S = "_";
    /**  */
    public static final String F_COMMA = "、";






}

enum Opt {
    CURRENT_DATE,
    YEAR_ARR, MONTH_ARR, DAY_ARR,
    ACCOUNT_MAP, TAX_RATE_MAP
}

enum CrntDt {
    YEAR, MONTH, DAY
}
