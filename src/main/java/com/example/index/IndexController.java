package com.example.index;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
public class IndexController {

    /** 勘定科目のマップ */
    private static final String ACCOUNT_MAP = "accountMap";

    /** 消費税率のマップ */
    private static final String TAX_RATE_MAP = "taxRateMap";

    /** 年の配列 */
    private static final String YEAR_ARR = "yearArr";

    /** 月の配列 */
    private static final String MONTH_ARR = "monthArr";

    /** 日の配列 */
    private static final String DAY_ARR = "dayArr";

    /**  */
    private static final String RECEIPT_FORM = "receiptForm";

    /** 遷移先 */
    private static final String INDEX = "index/index";

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private IndexService indexService;

    @GetMapping("/index")
    public String getIndex(Model prmModel, @ModelAttribute ReceiptForm prmReceiptForm) {

        IndexCalendar indexCalendar = indexService.getIndexCalendar();

        initOption(indexCalendar, prmModel);

        ReceiptForm receiptForm = new ReceiptForm(indexCalendar.getCurrentYear(),
                indexCalendar.getCurrentMonth(),
                indexCalendar.getCurrentDay(),
                32);

        prmModel.addAttribute(RECEIPT_FORM, receiptForm);

        return INDEX;

    }

    @PostMapping(value = "/index", params = "create")
    public String postIndex(Model prmModel, @ModelAttribute @Validated ReceiptForm prmReceiptForm, BindingResult prmBindingResult) {

        IndexCalendar indexCalendar = indexService.getIndexCalendar();

        initOption(indexCalendar, prmModel);

        if (prmBindingResult.hasErrors()) {

//            //TODO:チェックはビジネスか？←ビジネス
//            List<String> itemList = new ArrayList<>();
//            List<FieldError> fErrList = prmBindingResult.getFieldErrors();
//
//            for(FieldError elem : fErrList) {
//                String field = elem.getField();
//                String[] splitArr = field.split(java.util.regex.Pattern.quote("."));
//
//
//
//
//                switch (splitArr.length) {
//                case 1:
//                    if (splitArr[0].equals("taxAmount")) {
//                        itemList.add("税額");
//                    }
//
//                    break;
//
//                case 2:
//                    if (splitArr[1].equals("amount")) {
//                        itemList.add("金額");
//                    }
//
//                    break;
//
//                default:
//                    break;
//                }
//            }
//
//            if (itemList.size() > 0) {
//                List<String> tempList = new ArrayList<>(new HashSet<>(itemList));
//
//                List<String> temp2List = tempList;
//
//
//            }

            String errMsg = indexService.buildErrMsg(prmBindingResult);

            String errMsgCp = errMsg;


            return INDEX;
        }

        int amountSum = 0;
        AccountTaxrateAmount[] aTAArr = prmReceiptForm.getATAArr();

        for (AccountTaxrateAmount elem : aTAArr) {
            String amount = String.valueOf(elem.getAmount());

            if (amount == null || amount.length() == 0 ) {
                continue;
            }

            Integer intAmount = Integer.valueOf(elem.getAmount());

            amountSum += intAmount;
        }

        prmModel.addAttribute("amountSum", String.valueOf(amountSum));

        return INDEX;

    }

    @SuppressWarnings("unchecked")
    private void initOption(IndexCalendar prmIndexCalendar, Model prmModel) {

        // 科目を設定
        Map<String, String> accountMap = (Map<String, String>) httpSession.getAttribute(ACCOUNT_MAP);

        if (accountMap == null) {
            accountMap = indexService.getAccountMap();
        }

        prmModel.addAttribute(ACCOUNT_MAP, accountMap);

        // 消費税率を設定
        Map<String, Integer> taxRateMap = (Map<String, Integer>) httpSession.getAttribute(TAX_RATE_MAP);

        if (taxRateMap == null) {
            taxRateMap = indexService.getTaxRateMap();
        }

        prmModel.addAttribute(TAX_RATE_MAP, taxRateMap);

        // 日付を設定
        prmModel.addAttribute(YEAR_ARR, prmIndexCalendar.getYearArr());
        prmModel.addAttribute(MONTH_ARR, prmIndexCalendar.getMonthArr());
        prmModel.addAttribute(DAY_ARR, prmIndexCalendar.getDayArr());

    }

}
