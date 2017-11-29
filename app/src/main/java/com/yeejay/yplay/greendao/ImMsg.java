package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 消息表
 * Created by Administrator on 2017/11/23.
 */

@Entity(indexes = {
    @Index(value = "sessionId DESC, msgId DESC", unique = true)
})
public class ImMsg {

    @Id(autoincrement = true)
    private Long id;
    private String sessionId;   //回话ID
    private  long msgId;         //消息ID
    private String sender;      //发送者
    private int msgType;        //消息类型
    private String msgContent;  //消息内容
    private long msgTs;          //消息时间戳
    private int msgSucess;          //消息是否发送成功
    @Generated(hash = 1162226783)
    public ImMsg(Long id, String sessionId, long msgId, String sender, int msgType,
            String msgContent, long msgTs, int msgSucess) {
        this.id = id;
        this.sessionId = sessionId;
        this.msgId = msgId;
        this.sender = sender;
        this.msgType = msgType;
        this.msgContent = msgContent;
        this.msgTs = msgTs;
        this.msgSucess = msgSucess;
    }
    @Generated(hash = 2125460713)
    public ImMsg() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSessionId() {
        return this.sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public long getMsgId() {
        return this.msgId;
    }
    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }
    public String getSender() {
        return this.sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public int getMsgType() {
        return this.msgType;
    }
    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
    public String getMsgContent() {
        return this.msgContent;
    }
    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }
    public long getMsgTs() {
        return this.msgTs;
    }
    public void setMsgTs(long msgTs) {
        this.msgTs = msgTs;
    }
    public int getMsgSucess() {
        return this.msgSucess;
    }
    public void setMsgSucess(int msgSucess) {
        this.msgSucess = msgSucess;
    }


}
