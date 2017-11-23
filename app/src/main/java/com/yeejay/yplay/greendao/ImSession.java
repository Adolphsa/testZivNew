package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 会话表
 * Created by Administrator on 2017/11/23.
 */

@Entity
public class ImSession {

    @Id(autoincrement = true)
    private Long id;
    @Index(unique = true)
    private String sessionId;           //会话ID
    private  String chater;             //聊天对象
    private int status;
    private String nickName;            //聊天对象昵称
    private String headerImgUrl;        //头像URL
    private long lastMsgId;             //最新消息ID
    private String lastSender;          //最新一条消息的发送者
    private int msgType;                //消息类型
    private String msgContent;          //消息内容
    private long msgTs;                 //消息时间戳
    private long lastReadMsgId;         //最近已读的消息Id;
    @Generated(hash = 1848677176)
    public ImSession(Long id, String sessionId, String chater, int status,
            String nickName, String headerImgUrl, long lastMsgId, String lastSender,
            int msgType, String msgContent, long msgTs, long lastReadMsgId) {
        this.id = id;
        this.sessionId = sessionId;
        this.chater = chater;
        this.status = status;
        this.nickName = nickName;
        this.headerImgUrl = headerImgUrl;
        this.lastMsgId = lastMsgId;
        this.lastSender = lastSender;
        this.msgType = msgType;
        this.msgContent = msgContent;
        this.msgTs = msgTs;
        this.lastReadMsgId = lastReadMsgId;
    }
    @Generated(hash = 1305805177)
    public ImSession() {
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
    public String getChater() {
        return this.chater;
    }
    public void setChater(String chater) {
        this.chater = chater;
    }
    public String getNickName() {
        return this.nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getHeaderImgUrl() {
        return this.headerImgUrl;
    }
    public void setHeaderImgUrl(String headerImgUrl) {
        this.headerImgUrl = headerImgUrl;
    }
    public long getLastMsgId() {
        return this.lastMsgId;
    }
    public void setLastMsgId(long lastMsgId) {
        this.lastMsgId = lastMsgId;
    }
    public String getLastSender() {
        return this.lastSender;
    }
    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
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
    public long getLastReadMsgId() {
        return this.lastReadMsgId;
    }
    public void setLastReadMsgId(long lastReadMsgId) {
        this.lastReadMsgId = lastReadMsgId;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
