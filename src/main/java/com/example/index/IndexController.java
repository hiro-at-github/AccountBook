package com.example.index;

import java.util.ArrayList;
import java.util.List;

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

import com.example.common.Cmn;

@Controller
@RequestMapping("/index")
public class IndexController {
    /** 年の配列のキー */
    private static final String YEAR_ARR = "yearArr";

    /** 月の配列のキー */
    private static final String MONTH_ARR = "monthArr";

    /** 日の配列のキー */
    private static final String DAY_ARR = "dayArr";

    /** 勘定科目のマップのキー */
    private static final String ACCOUNT_MAP = "accountMap";

    /** 消費税率のマップのキー */
    private static final String TAX_RATE_MAP = "taxRateMap";

    /** セレクトボックスの選択肢のキー */
    private static final String OPTION_ARR = "optionArr";

    /** 登録済のレシートのリストのキー */
    private static final String REGISTERED_LST = "registeredLst";
    
    /** レシートフォームのキー */
    private static final String RECEIPT_FORM = "receiptForm";

    /** 遷移先 */
    private static final String INDEX = "index/index";

    /** セッション */
    @Autowired
    private HttpSession httpSession;

    /** インデックスサービス */
    @Autowired
    private IndexService indexService;

    //TODO:ここから作業再開
    
    public IndexController() {

    }

    @GetMapping("/index")
    public String getIndex(Model prmModel/*, @ModelAttribute ReceiptForm prmReceiptForm*/) {
        Object[] optionArr = (Object[]) httpSession.getAttribute(OPTION_ARR);

        if (optionArr == null) {
            optionArr = initOptionArr();
            httpSession.setAttribute(OPTION_ARR, optionArr);
        }

        addOptionArr(optionArr, prmModel);

        ReceiptForm receiptForm = new ReceiptForm(32, (String[]) optionArr[IndexService.CURRENT_DATE]);
        prmModel.addAttribute(RECEIPT_FORM, receiptForm);

        return INDEX;
    }

    //--------------------------------------------------------------------------------
    /**
     * 「確認」ボタン押下の処理
     *
     */
    //--------------------------------------------------------------------------------
    @PostMapping(value = "/index", params = "confirm")
    public String postConfirm(Model prmModel, @ModelAttribute @Validated ReceiptForm prmReceiptForm,
            BindingResult prmBindingResult) {

        addOptionArr((Object[]) httpSession.getAttribute(OPTION_ARR), prmModel);
        prmReceiptForm.setRgstedLst(Cmn.autoCast(httpSession.getAttribute(REGISTERED_LST)));

        if (validateItems(prmReceiptForm, prmBindingResult)) {
            String[] ttlAndTAmntArr = indexService.getTotalAndTaxAmountArr(prmReceiptForm);
            prmReceiptForm.setSubtotal(ttlAndTAmntArr[0]);
            prmReceiptForm.setSumTotal(ttlAndTAmntArr[1]);
        }

        return INDEX;
    }

    //--------------------------------------------------------------------------------
    /**
     * 「登録」ボタン押下の処理
     *
     */
    //--------------------------------------------------------------------------------
    @PostMapping(value = "/index", params = "create")
    public String postCreate(Model prmModel, @ModelAttribute @Validated ReceiptForm prmReceiptForm,
            BindingResult prmBindingResult) {

        Object[] optionArr = (Object[]) httpSession.getAttribute(OPTION_ARR);
        addOptionArr(optionArr, prmModel);

        List<Registered> rgstedLst = Cmn.autoCast(httpSession.getAttribute(REGISTERED_LST));
        
        if (validateItems(prmReceiptForm, prmBindingResult)) {
            rgstedLst = addRgistedToLst(rgstedLst, prmReceiptForm);
            httpSession.setAttribute(REGISTERED_LST, rgstedLst);

            prmReceiptForm = new ReceiptForm(32, (String[]) optionArr[IndexService.CURRENT_DATE]);
        }

        prmReceiptForm.setRgstedLst(rgstedLst);
        prmModel.addAttribute(RECEIPT_FORM, prmReceiptForm);

        return INDEX;
    }

    // privateメソッド ---------------------------------------------------------------
    
    //--------------------------------------------------------------------------------
    /**
     * セレクトボックスの選択肢を初期化して返す
     *
     * @return セレクトボックスの選択肢
     */
    //--------------------------------------------------------------------------------
    private Object[] initOptionArr() {
        Object[] optionArr = new Object[] {
                indexService.getCurrentDate(),
                indexService.getYearArr(),
                indexService.getMonthArr(),
                indexService.getDayArr(),
                indexService.getAccountMap(),
                indexService.getTaxRateMap()
        };

        return optionArr;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * セレクトボックスの選択肢をモデルに登録する
     * 
     * @param prmOptionArr セレクトボックスの選択肢の配列
     * @param prmModel モデル
     */
    //--------------------------------------------------------------------------------
    private void addOptionArr(Object[] prmOptionArr, Model prmModel) {
        prmModel.addAttribute(YEAR_ARR, prmOptionArr[IndexService.YEAR_ARR]);
        prmModel.addAttribute(MONTH_ARR, prmOptionArr[IndexService.MONTH_ARR]);
        prmModel.addAttribute(DAY_ARR, prmOptionArr[IndexService.DAY_ARR]);
        prmModel.addAttribute(ACCOUNT_MAP, prmOptionArr[IndexService.ACCOUNT_MAP]);
        prmModel.addAttribute(TAX_RATE_MAP, prmOptionArr[IndexService.TAX_RATE_MAP]);
    }
    
    //--------------------------------------------------------------------------------
    /**
     * レシートフォームの各項目の入力値の妥当性を検査してその結果を返す
     * 入力値が不適当な場合、レシートフォームにエラーメッセージをセットする
     * 
     * @param prmReceiptForm レシートフォーム
     * @param prmBindingResult バインディングリザルト
     * @return 入力値が適当な場合、真。不適切な場合、偽
     */
    //--------------------------------------------------------------------------------
    private boolean validateItems(ReceiptForm prmReceiptForm, BindingResult prmBindingResult) {
        if (prmBindingResult.hasErrors()) {
            prmReceiptForm.setErrorMessage(indexService.buildFldErrMsg(prmBindingResult));

            return false;
        }

        if (!indexService.isRelatedItemsEntered(prmReceiptForm)) {
            prmReceiptForm.setErrorMessage(indexService.getRltErrMsg());

//            List<FieldError> fldErrLst = indexService.getRltFldErrLst();
//            for (FieldError elem : fldErrLst) {
//                prmBindingResult.addError(elem);
//            }
            //TODO:上記コメントアウト部分の代替処理。問題なければこのTODOと共に上記コメントアウト要削除
            indexService.getRltFldErrLst().forEach(e -> prmBindingResult.addError(e));
            
            return false;
        }

        return true;
    }

    //--------------------------------------------------------------------------------
    /**
     * レシートフォームの各項目の入力値を登録する。
     * その後、登録したレシートの概要をリストに加えて返す
     * 
     * @param prmRgstedLst 登録したレシートの概要のリスト
     * @param prmReceiptForm レシートフォーム
     * @return 登録したレシートの概要のリスト
     */
    //--------------------------------------------------------------------------------
    private List<Registered> addRgistedToLst(List<Registered> prmRgstedLst, ReceiptForm prmReceiptForm) {
        List<Registered> rgstedLst = prmRgstedLst;

        if (rgstedLst == null) {
            rgstedLst = new ArrayList<>();
        }

        rgstedLst.add(0, indexService.getRegistered(prmReceiptForm));

        return rgstedLst;
    }
}
