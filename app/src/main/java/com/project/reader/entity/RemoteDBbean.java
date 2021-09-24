package com.project.reader.entity;

public class RemoteDBbean {
    private long bookId;
    private String nickName;
    private String content;
    private String createDate;
    private String replyPerson;
    private int numberFloor;//第几层楼的回复

    public RemoteDBbean() {
    }

    public RemoteDBbean(long bookId, String nickName, String content, String createDate, String replyPerson, int numberFloor) {
        this.bookId=bookId;
        this.nickName = nickName;
        this.content = content;
        this.createDate = createDate;
        this.replyPerson = replyPerson;
        this.numberFloor=numberFloor;
    }

    public int getNumberFloor() {
        return numberFloor;
    }

    public void setNumberFloor(int numberFloor) {
        this.numberFloor = numberFloor;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getReplyPerson() {
        return replyPerson;
    }

    public void setReplyPerson(String replyPerson) {
        this.replyPerson = replyPerson;
    }
}
