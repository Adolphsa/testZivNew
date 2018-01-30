package com.yeejay.yplay.utils;

/**
 * 应用中的常量
 * Created by Administrator on 2017/10/29.
 */

public class YPlayConstant {

    public static final String YPLAY_UUID = "yplay_uuid";
    public static final String YPLAY_UIN = "yplay_uin";
    public static final String YPLAY_TOKEN = "yplay_token";
    public static final String YPLAY_PHONE_NUMBER = "yplay_phone_number";
    public static final String YPLAY_VER = "yplay_ver";
    public static final String YPLAY_USER_NAME = "yplay_user_name";
    public static final String YPLAY_NICK_NAME = "yplay_nick_name";
    public static final String YPLAY_FIRST_LATITUDE = "yplay_first_latitude";
    public static final String YPLAY_FIRST_LONGITUDE = "yplay_first_longitude";
    public static final String YPLAY_LATITUDE = "yplay_latitude";
    public static final String YPLAY_LONGITUDE = "yplay_longitude";

    public static final String YPLAY_LOGIN_MODE = "yplay_login_mode";

    public static final String YPLAY_SCHOOL_TYPE = "yplay_school_type";
    public static final String YPLAY_SCHOOL_GRADE = "yplay_school_grade";
    public static final String YPLAY_HEADER_IMG = "yplay_header_img";

    public static final String TEMP_NICK_NAME = "temp_nick_name";
    public static final String TEMP_HEAD_IMAGE_URL = "temp_head_image_url";
    public static final String TEMP_GENDER = "temp_gender";
    public static final String TEMP_GRADE = "temp_grade";
    public static final String TEMP_SCHOOL_ID = "temp_school_id";

    public static final String YPLAY_NO_MORE_SHOW = "no_more_show";

    public static final String YPLAY_IM_SIGNATURE = "im_signature";
    public static final String SP_KEY_IM_SIG = "img_signature";

    public static final int YPLAY_FEEDS_TYPE = 1;
    public static final int YPLAY_MY_INFO_TYPE = 2;

    public static final int YPLAY_OFFINE_MSG_COUNT = 1000;

    public static int IM_ERROR_CODE = 0;

    public static String MI_PUSH_APP_ID = "2882303761517630988";
    public static String MI_PUSH_APP_KEY = "5841763037988";

    public static String IMEI = "";

    //yplay api
    public static final String YPLAY_API_BASE = "http://yplay.vivacampus.com";

    //获取我的资料
    public static final String API_MY_INFO_URL = "/api/user/getmyprofile";

    //发送验证码
    public static final String API_SEND_SMS_URL = "/api/account/sendsms";

    //登录
    public static final String API_LOGIN_URL = "/api/account/login2";

    //设置资料相关
    public static final String API_SET_AGE_URL = "/api/user/updateuserprofile";

    //查询剩余修改次数
    public static final String API_QUERY_LEFT_COUNT_URL = "/api/user/getmyprofilemodquota";

    //搜索学校
    public static final String API_SEARCH_SCHOOL_URL = "/api/account/searchschools";

    //获取学校列表
    public static final String API_GET_SCHOOL_URL = "/api/account/getnearestschools";

    //选择学校
    public static final String API_CHOICE_SCHOOL_URL = "/api/user/updateschoolinfo";

    //同校好友
    public static final String API_GET_SCHOOL_FRIEND_URL = "/api/sns/getrecommends";

    //添加好友
    public static final String API_ADD_FRIEND_URL = "/api/sns/addfriend";

    //检查邀请码
    public static final String API_CHECK_INVIDE_CODE_URL = "/api/account/checkinvitecode";

    //获取钻石信息
    public static final String API_GET_DIAMOND_URL = "/api/user/getusergemstatinfo";

    //获取未读好友的消息数
    public static final String API_GET_UNREAD_MESSAGE_COUNT_URL = "/api/sns/getaddfriendnewmsgcnt";

    //拉取添加好友消息数组
    public static final String API_GET_FRIEND_COUNT_URL = "/api/sns/getaddfriendmsgs";

    //接受好友请求
    public static final String API_ACCEPT_FRIEND_URL = "/api/sns/acceptaddfriend";

    //获取已经点击加好友的列表
    public static final String API_ALREADY_CLICK_ADD_URL = "/api/sns/getreqaddfrienduins";

    //退出登录
    public static final String API_LOGIN_OUT_URL = "/api/account/logout";



    //---------------------------------------------------------------------
    public static final String BASE_URL = "http://yplay.vivacampus.com";

    //获取好友动态
    public static final String API_GETFEEDS_URL = "/api/feed/getfeeds";

    //通过短信邀请好友
    public static final String API_INVITEFRIENDSBYSMS_URL = "/api/sns/invitefriendsbysms";

    //删除好友
    public static final String API_REMOVEFRIEND_URL = "/api/sns/removefriend";

    //空页面随机推荐好友
    public static final String API_GETRANDOMRECOMMENDS = "/api/sns/getrandomrecommends";

    //发送加好友请求
    public static final String API_ADDFRIEND = "/api/sns/addfriend";

    //获取用户的资料
    public static final String API_GETUSERPROFILE = "/api/user/getuserprofile";

    //确认feeds收到
    public static final String API_ACKFEEDS = "/api/feed/ackfeeds";

    //拉取问题
    public static final String API_GETQUESTIONANDOPTIONS = "/api/vote/getquestionandoptions";

    //某个问题的拉取候选者---新
    public static final String API_GETOPTIONS = "/api/vote/getoptions";

    //投票
    public static final String API_DOVOTE = "/api/vote/dovote";

    //跳过下个问题
    public static final String API_DOSKIP = "/api/vote/doskip";

    //用户投稿
    public static final String API_SUBMITQUESTION = "/api/vote/submitquestion";

    //删除选择的未审核通过列表项
    public static final String API_SUBMIT_DELETE = "/api/submit/delete";

    //查询未上线的投稿列表
    public static final String API_QUERYLISTNOTONLINE = "/api/submit/querylistnotonline";

    //查询已经上线的题目投票详情
    public static final String API_QUERYDETAIL = "/api/submit/querydetail";

    //查询所有类型的投稿列表
    public static final String API_QUERYLIST = "/api/submit/querylist";

    //获取im签名
    public static final String API_GENEUSERSIG = "/api/im/geneusersig";

    //获取用户新通知
    public static final String API_GETNEWNOTIFYSTAT = "/api/notify/getnewnotifystat";

    //获取我的好友列表
    public static final String API_GETMYFRIENDS = "/api/user/getmyfriends";

    //通讯录上传或更新
    public static final String API_CONTACTS_UPDATE = "/api/addr/update";

    //查询用户的注册状态
    public static final String API_QUERYBYPHONE = "/api/addr/querybyphone";

    //通讯录remove
    public static final String API_CONTACTS_REMOVE = "/api/addr/remove";

    //投票消息回复
    public static final String API_SENDVOTEREPLYMSG = "/api/im/sendvotereplymsg";

    //获取上传头像的签名
    public static final String API_GETHEADIMGUPLOADSIG= "/api/user/getheadimguploadsig";
}
