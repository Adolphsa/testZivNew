package com.yeejay.yplay.utils;

import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.greendao.ImSession;
import com.yeejay.yplay.model.MsgContent1;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 查询数据库相关属性的值
 * Created by Administrator on 2017/12/1.
 */

public class DatebaseUtil {


    public static String getNotificationTitle(ImSession imSession) {

        String myselfNickName = (String) SharePreferenceUtil.get(YplayApplication.getInstance(), YPlayConstant.YPLAY_NICK_NAME, "");
        String notificationTitle = "";
        String msgContent = imSession.getMsgContent();
        int status = imSession.getStatus();

        if (0 == status) {   //投票

            try {
                JSONObject jsonObject = new JSONObject(msgContent);
                int dataType = jsonObject.getInt("DataType");
                String data = jsonObject.getString("Data");

                if (dataType == 1) {
                    MsgContent1 msgContent1 = GsonUtil.GsonToBean(data, MsgContent1.class);
                    MsgContent1.SenderInfoBean senderInfoBean = msgContent1.getSenderInfo();
                    int gender = senderInfoBean.getGender();
                    String genderStr = gender == 1 ? "男生" : "女生";
                    int grade = senderInfoBean.getGrade();
                    int schoolType = senderInfoBean.getSchoolType();
                    String gradeAndSchool = FriendFeedsUtil.schoolType(schoolType, grade);
                    notificationTitle = "@" + myselfNickName + ",神秘" +
                            gradeAndSchool + genderStr +
                            "对你说了真心话( ⁼̴̀ .̫ ⁼̴́ )✧";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (status == 1) {
            notificationTitle = "@" + myselfNickName + "你投的人回复了你！( ⁼̴̀ .̫ ⁼̴́ )✧";
        } else if (status == 2) {
            notificationTitle = imSession.getNickName();
        }
        return notificationTitle;
    }

}
