package com.yeejay.yplay.utils;

/**
 * 动态相关工具
 * Created by Administrator on 2017/10/31.
 */

public class FriendFeedsUtil {

    public static String boyOrGirl(int gender){
        String a = "";
        if (gender == 1){
            a = "男生";
        }else if(gender == 2){
            a = "女生";
        }
        return a;
    }

    public static String schoolType(int schoolType,int grade){
        String b = "";
        if (grade == 100){
            if (schoolType == 1){
                b = "初中毕业";
            }else if(schoolType == 2){
                b = "高中毕业";
            }else if(schoolType == 3){
                b = "大学毕业";
            }
        }else {
            if (schoolType == 1){
                b = "初" + grade(grade);
            }else if(schoolType == 2){
                b = "高" + grade(grade);
            }else if(schoolType == 3){
                b = "大" + grade(grade);
            }
        }
        return b;
    }

    public static String grade(int grade){
        String c = "";
        if (grade == 1){
            c = "一";
        }else if(grade == 2){
            c = "二";
        }else if(grade == 3){
            c = "三";
        }else if(grade == 4){
            c = "四";
        }else if(grade == 5){
            c = "五";
        }
        return c;
    }
}
