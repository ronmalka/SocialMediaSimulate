package bgu.spl.net;

import bgu.spl.net.Message.NotifiacationMessage;

import java.util.LinkedList;

public class User {
    private int cilentId;
    private String clientName;
    private String password;
    private int countPosts;
    private boolean connect;
    private LinkedList<String> clientPost;
    private LinkedList<String> followList;
    private LinkedList<NotifiacationMessage> PostsToRead;
    private LinkedList<String> followingList;
    public User(int cilentId, String clientName,String password ){
        this.cilentId = cilentId;
        this.clientName= clientName;
        this.password = password;
        this.countPosts = 0;
        this.connect = false;
        clientPost = new LinkedList<>();
        followList = new LinkedList<>();
        followingList = new LinkedList<>();
        PostsToRead = new LinkedList<>();
    }

    public String getPassword() {
        return password;
    }

    public boolean isConnect() {
        return connect;
    }
    public void Connect(){
        connect = true;
    }
    public void disConnect(){
        connect = false;
    }


    public int getCountPosts() {
        return countPosts;
    }
    public void increaseCountPosts(){
        countPosts++;
    }
    public LinkedList<NotifiacationMessage> getPostsToRead() {
        return PostsToRead;
    }

    public LinkedList<String> getFollowList() {
        return followList;
    }

    public void setClientPost(String post) {
        clientPost.add(post);
    }

    public LinkedList<String> getClientPost() {
        return clientPost;
    }
    public void setPostsToRead(NotifiacationMessage post){
        PostsToRead.add(post);
    }

    public void setCilentId(int cilentId) {
        this.cilentId = cilentId;
    }

    public int getCilentId() {
        return cilentId;
    }

    public LinkedList<String> getFollowingList() {
        return followingList;
    }

}
