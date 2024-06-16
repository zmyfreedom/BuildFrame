package com.zmy.common;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Test {
    private static String REGEX = "a*b";
    private static String INPUT = "aaab=aab==ab===bkkk";
    private static String REPLACE = "|";
    public static void main(String[] args) {
        Pattern p = Pattern.compile(REGEX);
        // 获取 matcher 对象
        Matcher m = p.matcher(INPUT);
//        System.out.println(Matcher.quoteReplacement(REPLACE));
//        System.out.println(m.replaceFirst(REPLACE));
        StringBuffer sb = new StringBuffer();
        while(m.find()){
//            System.out.println(m.group());
            System.out.println(m.start());
            System.out.println(m.end());
            //用于将匹配到的字符串替换为指定的字符串，并将结果追加到StringBuilder对象sb中
            m.appendReplacement(sb,REPLACE);
            System.out.println(sb);
        }
        m.appendTail(sb);
        System.out.println(sb.toString());
    }
}
