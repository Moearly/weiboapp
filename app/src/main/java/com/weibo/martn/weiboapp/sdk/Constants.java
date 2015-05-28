package com.weibo.martn.weiboapp.sdk;

public interface Constants {

	public static final String INTENT_ACTION_NOTICE = "com.martn.weibo.action.APPWIDGET_UPDATE";
	public static final String SINA_APP_KEY = "3434427817";
	/**
	 * 申请的App Secret,修改成你自己的才能使用 *
	 */
	public static final String SINA_APP_SECRET = "c4bb2921f622f256d3e2766c8aeb72a1";
	public static final String SINA_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	public static final String SINA_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
	//授权回调地址
	public static final String callback_url = "https://api.weibo.com/oauth2/default.html";
	//新浪微博服务地址
	public static final String base_url = "https://api.weibo.com/2/";
}