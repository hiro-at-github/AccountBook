package com.example.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
public class IndexController {
    /** 現在の年 */
    private static final String CURRENT_YEAR = "currentYear";

    /** 現在の月 */
    private static final String CURRENT_MONTH = "currentMonth";

    /** 現在の日 */
    private static final String CURRENT_DAY = "currentDay";

    /** 年の配列 */
    private static final String YEAR_ARR = "yearArr";

    /** 月の配列 */
    private static final String MONTH_ARR = "monthArr";

    /** 日の配列 */
    private static final String DAY_ARR = "dayArr";

    /** 勘定科目のマップ */
    private static final String ACCOUNT_MAP = "accountMap";

    /** 消費税率のマップ */
    private static final String TAX_RATE_MAP = "taxRateMap";

    /**  */
    private static final String OPTION_ARR = "optionArr";


    /**  */
    private static final String RECEIPT_FORM = "receiptForm";

    /** 遷移先 */
    private static final String INDEX = "index/index";

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private IndexService indexService;

    /**  */
    private String[] attrNameArr;

    /**  */
    private String[] stringKeyArr;
    /**  */
    private String[] arrayKeyArr;
    /**  */
    private String[] mapKeyArr;


    public IndexController() {
        attrNameArr = new String[] {CURRENT_YEAR, CURRENT_MONTH, CURRENT_DAY,
                YEAR_ARR, MONTH_ARR, DAY_ARR, ACCOUNT_MAP, TAX_RATE_MAP};

        stringKeyArr = new String[] {CURRENT_YEAR, CURRENT_MONTH, CURRENT_DAY};
        arrayKeyArr = new String[] {YEAR_ARR, MONTH_ARR, DAY_ARR};
        mapKeyArr = new String[] {ACCOUNT_MAP, TAX_RATE_MAP};


    }




    @GetMapping("/index")
    public String getIndex(Model prmModel/*, @ModelAttribute ReceiptForm prmReceiptForm*/) {
        Object[] optionArr = (Object[]) httpSession.getAttribute(OPTION_ARR);

        if (optionArr == null) {
            optionArr = initOptionArr();
            httpSession.setAttribute(OPTION_ARR, optionArr);
        }

        addOptionArr(optionArr, prmModel);

        String[] date = (String[]) optionArr[Opt.CURRENT_DATE.ordinal()];
        ReceiptForm receiptForm
            = new ReceiptForm(date[CrntDt.YEAR.ordinal()], date[CrntDt.MONTH.ordinal()], date[CrntDt.DAY.ordinal()], 32);
        prmModel.addAttribute(RECEIPT_FORM, receiptForm);

        return INDEX;
    }

    @PostMapping(value = "/index", params = "create")
    public String postIndex(Model prmModel, @ModelAttribute @Validated ReceiptForm prmReceiptForm, BindingResult prmBindingResult) {
//        initOption(prmModel);

        if (prmBindingResult.hasErrors()) {
            prmReceiptForm.setErrorMessage(indexService.buildErrMsg(prmBindingResult));

            return INDEX;
        }

        String errMsg = indexService.checkTmpMtd(prmReceiptForm.getATAArr());

        if (errMsg != null) {
            prmReceiptForm.setErrorMessage(errMsg);

            List<Errors> errLst = new ArrayList<>();
//            Errors err = new Errors();



            return INDEX;
        }




//        int amountSum = 0;
//        AccountTaxrateAmount[] aTAArr = prmReceiptForm.getATAArr();
//
//        for (int i = 0; i < aTAArr.length; i++) {
//            AccountTaxrateAmount elem = aTAArr[i];
//
//            if ("".equals(elem.getAccount()) && "".equals(elem.getTaxRate()) && elem.getAmount() == null) {
//                continue;
//            }
//
//            Integer amount = elem.getAmount();
//            String[] temp = {elem.getAccount(), elem.getTaxRate(), amount == null ? null : amount.toString()};
//
//
//
//        }
//
//        for (AccountTaxrateAmount elem : aTAArr) {
//            Integer amount = elem.getAmount();
//            String[] temp = {elem.getAccount(), elem.getTaxRate(), amount == null ? null : amount.toString()};
//
//
//
//
//        }
//
//
//
//
//
//
//
//
//        for (AccountTaxrateAmount elem : aTAArr) {
//            String amount = String.valueOf(elem.getAmount());
//
//            if (amount == null || amount.length() == 0 ) {
//                continue;
//            }
//
//            Integer intAmount = Integer.valueOf(elem.getAmount());
//
//            amountSum += intAmount;
//        }
//
//        prmModel.addAttribute("amountSum", String.valueOf(amountSum));

        return INDEX;
    }

    @SuppressWarnings("unchecked")
    private void initOption(Model prmModel) {
        //
        String[] yearArr = (String[]) httpSession.getAttribute("");

        if (yearArr == null) {
            yearArr = indexService.getYearArr();

        }

        prmModel.addAttribute(YEAR_ARR, indexService.getYearArr());
        prmModel.addAttribute(MONTH_ARR, indexService.getMonthArr());
        prmModel.addAttribute(DAY_ARR, indexService.getDayArr());

        // 科目の設定
        Map<String, String> accountMap = (Map<String, String>) httpSession.getAttribute(ACCOUNT_MAP);

        if (accountMap == null) {
            accountMap = indexService.getAccountMap();
            httpSession.setAttribute(ACCOUNT_MAP, accountMap);
        }

        prmModel.addAttribute(ACCOUNT_MAP, accountMap);

        // 消費税率の設定
        Map<String, Integer> taxRateMap = (Map<String, Integer>) httpSession.getAttribute(TAX_RATE_MAP);

        if (taxRateMap == null) {
            taxRateMap = indexService.getTaxRateMap();
            httpSession.setAttribute(TAX_RATE_MAP, taxRateMap);
        }

        prmModel.addAttribute(TAX_RATE_MAP, taxRateMap);
    }


    private Map<String, Object> getFrmSession() {
        // 年の取得
        Object currentYear = null;

//        Map<String, Object>

        return null;
    }

    private Object[] initOptionArr() {
        Object[] optionArr = new Object[Opt.values().length];

        optionArr[Opt.CURRENT_DATE.ordinal()] = indexService.getCurrentDate();
        optionArr[Opt.YEAR_ARR.ordinal()] = indexService.getYearArr();
        optionArr[Opt.MONTH_ARR.ordinal()] = indexService.getMonthArr();
        optionArr[Opt.DAY_ARR.ordinal()] = indexService.getDayArr();
        optionArr[Opt.ACCOUNT_MAP.ordinal()] = indexService.getAccountMap();
        optionArr[Opt.TAX_RATE_MAP.ordinal()] = indexService.getTaxRateMap();

        return optionArr;
    }

    private void addOptionArr(Object[] prmOptionArr, Model prmModel) {
        prmModel.addAttribute(YEAR_ARR, prmOptionArr[Opt.YEAR_ARR.ordinal()]);
        prmModel.addAttribute(MONTH_ARR, prmOptionArr[Opt.MONTH_ARR.ordinal()]);
        prmModel.addAttribute(DAY_ARR, prmOptionArr[Opt.DAY_ARR.ordinal()]);
        prmModel.addAttribute(ACCOUNT_MAP, prmOptionArr[Opt.ACCOUNT_MAP.ordinal()]);
        prmModel.addAttribute(TAX_RATE_MAP, prmOptionArr[Opt.TAX_RATE_MAP.ordinal()]);
    }





}
