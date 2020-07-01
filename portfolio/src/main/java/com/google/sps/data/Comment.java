package com.google.sps.data;

public final class Comment {

  public final long id;
  public final String nickname;
  public final String comment;
  public final long timestamp;

  public Comment(long id, String nickname, String comment, long timestamp) {
    this.id = id;
    this.nickname = nickname;
    this.comment = comment;
    this.timestamp = timestamp;
  }

  public long getId() {
      return id;
  }

  public String getNickname() {
      return nickname;
  }

  public String getComment() {
      return comment;
  }
}