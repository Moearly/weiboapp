package com.weibo.martn.weiboapp.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.weibo.martn.weiboapp.R;
import com.weibo.martn.weiboapp.sdk.AccessTokenKeeper;
import com.weibo.martn.weiboapp.sdk.Constants;
import com.weibo.martn.weiboapp.utils.ToastUtils;

import java.io.File;


/**
 * Created by Administrator on 2015/5/8.
 */
public class PostActivity extends Activity {
    private ImageView iv_pic, iv_send, iv_gally;
    private ProgressBar pBar;
    //用户写入的博文
    private EditText eText;
    /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;

    private Bitmap pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_post);
        initViews();
        initToken();
        initActionBar();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.flip_horizontal_in,
                R.anim.flip_horizontal_out);
    }

    void initViews() {
        this.eText = (EditText) findViewById(R.id.et_bloginfo);
    }

    /**
     * style actionBar and find views here
     */
    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.color.white));

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar_post);
        this.pBar = (ProgressBar) findViewById(R.id.pb_action_circle);
        this.iv_pic = (ImageView) findViewById(R.id.iv_action_camera);
        this.iv_pic.setOnClickListener(new View.OnClickListener() {
            //选取图片操作
            @Override
            public void onClick(View v) {
                File fos = null;
                try {
                    fos = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath()
                            + File.separator
                            + "TMP_SINA.jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri u = Uri.fromFile(fos);
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                i.putExtra(MediaStore.EXTRA_OUTPUT, u);
                startActivityForResult(i, 9);

            }
        });

        this.iv_gally = (ImageView) findViewById(R.id.iv_action_gally);
        this.iv_gally.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 2);
            }
        });
        //微博发送逻辑
        this.iv_send = (ImageView) findViewById(R.id.iv_send);
        this.iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pBar.setVisibility(View.VISIBLE);
                String string = eText.getText().toString();
                if (string != null && !string.trim().isEmpty()) {
                    if (pic != null) {
                        //如果有图片，则图片一同上传
                        mStatusesAPI.upload(string, pic, null, null, mListener);
                    } else
                        mStatusesAPI.update(string, null, null, mListener);
                } else {
                    ToastUtils.showToast(getApplicationContext(), "您什么都没写...");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 9) {

                this.pic = null;
                File bb = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "TMP_SINA.jpg");

                Intent i = new Intent("com.android.camera.action.CROP");
                i.setType("image/*");

                // i.putExtra("data", bb);
                i.setDataAndType(Uri.fromFile(bb), "image/jpeg");

                i.putExtra("crop", "true");

                i.putExtra("aspectX", 1);

                i.putExtra("aspectY", 1);

                // i.putExtra("outputX", 500);
                //
                // i.putExtra("outputY", 500);

                i.putExtra("return-data", true);

                this.startActivityForResult(i, 7);

            }

            if (requestCode == 2) {
                this.pic = null;
                this.pic = (Bitmap) data.getExtras().get("data");
                if (pic != null)
                    iv_gally.setImageBitmap(pic);
            }
            if (requestCode == 7) {
                this.pic = data.getParcelableExtra("data");
                iv_pic.setImageBitmap(pic);
            }
        }

    }

    private void initToken() {
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.SINA_APP_KEY, mAccessToken);
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                if (response.startsWith("{\"created_at\"")) {
                    ToastUtils.showToast(getApplicationContext(), "发表成功");
                    finish();
                    overridePendingTransition(R.anim.flip_horizontal_in,
                            R.anim.flip_horizontal_out);
                } else {
                }
            }
        }


        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Log.e("error_sina_weibo_exp", info.toString());
        }
    };
}
