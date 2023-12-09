package com.example.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileFiles {
    //--------------------------------------------------------------------------------
    /**
     * ダミー
     */
    //--------------------------------------------------------------------------------
    public static List<String> readAllLines(String prmPath) {
        try {
            return Files.readAllLines(Paths.get(prmPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------------
    /**
     * 文字列のリストをファイルに書き込む
     * 
     * @param prmPathname パス文字列またはパス文字列の最初の部分
     * @param prmLineLst ファイルに書き込む文字列のリスト
     * @param prmOption ファイルを開く方法
     */
    //----------------------------------------------------------------------------------------------------
//    public static void write(String prmPathname, List<String> prmLineLst, StandardOpenOption prmOption) {
//        try {
//            if (prmOption != null && new File(prmPathname).exists()) {
//                Files.write(Paths.get(prmPathname), prmLineLst, prmOption);
//            } else {
//                Files.write(Paths.get(prmPathname), prmLineLst);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public static void write(String prmPathname, List<String> prmLineLst) {
        try {
            if (new File(prmPathname).exists()) {
                Files.write(Paths.get(prmPathname), prmLineLst, StandardOpenOption.APPEND);
            } else {
                Files.write(Paths.get(prmPathname), prmLineLst, StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
