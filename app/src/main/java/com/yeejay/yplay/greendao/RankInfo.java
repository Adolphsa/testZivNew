package com.yeejay.yplay.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 排行榜存储
 * Created by Adolph on 2018/2/8.
 */

@Entity(indexes = {
        @Index(value = "uin DESC, questionNumber DESC", unique = true)
})
public class RankInfo {

    @Id(autoincrement = true)
    private Long id;
    private int uin;
    private int questionNumber;
    private String questionText;
    private String result;
@Generated(hash = 1079407657)
public RankInfo(Long id, int uin, int questionNumber, String questionText,
        String result) {
    this.id = id;
    this.uin = uin;
    this.questionNumber = questionNumber;
    this.questionText = questionText;
    this.result = result;
}
@Generated(hash = 1089048424)
public RankInfo() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public int getUin() {
    return this.uin;
}
public void setUin(int uin) {
    this.uin = uin;
}
public int getQuestionNumber() {
    return this.questionNumber;
}
public void setQuestionNumber(int questionNumber) {
    this.questionNumber = questionNumber;
}
public String getQuestionText() {
    return this.questionText;
}
public void setQuestionText(String questionText) {
    this.questionText = questionText;
}
public String getResult() {
    return this.result;
}
public void setResult(String result) {
    this.result = result;
}

}
