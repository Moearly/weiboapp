package com.weibo.martn.weiboapp.sdk;

public interface Constants {
	public static final String SINA_APP_KEY = "3434427817";
	/** 申请的App Secret,修改成你自己的才能使用 **/
	public static final String SINA_APP_SECRET = "b0c74d0a1e78e9bf3367313245f44aed";
	public static final String SINA_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	public static final String SINA_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
}
