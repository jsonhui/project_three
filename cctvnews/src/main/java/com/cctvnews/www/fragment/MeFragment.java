package com.cctvnews.www.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.cctvnews.www.R;
import com.cctvnews.www.tool.alipay.SignUtils;
import com.cctvnews.www.tool.commontool.HttpUtils;
import com.cctvnews.www.tool.logtool.LogTool;
import com.cctvnews.www.ui.LoginActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author ：Created by Jason on 2016/7/12 13:25
 * @Email : 320175912@qq.com
 * @desc : 我
 */
public class MeFragment extends Fragment {
    // 商户PID
    public static final String PARTNER = "2088511239479533";
    // 商户收款账号
    public static final String SELLER = "1836601457@qq.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANNYEukdH0GIcp5pImcRezZeKb3CtpoTCpdstua3odfI2erfPxJaFGxWvnwE0ges5hJRpGtO4ORNyCoyNKdri18tm9OhfDJJrWcDoW4fWCaZkEeNnjXUHA6Cev6Y1r0ret4krbF2+Vz5qQdJsYlyDpJinvjnGTXxrX8ynITSESsNAgMBAAECgYAyvGEFz3TycYQ6nTiiD6NJoP9aS8U0Zb/ULEgYSRs0R0ZxSRjGGhPvEj/2W93j89Djsu/KxwvcIwQbhSP40SuKxRaphRQdtUoKpm+QMXA6I6ARLTBu0QiGD/ojjjUGle9vQbY9Kp+Y7AIZdH2H4bhu1Rh+ulWuug40zEU/rw5DYQJBAPAZdNAzzcRhY5ikmqLNIaFsscBsBO0Oth0Oiae72566mMd/PxE9mmeNnhfq9GsRd9o5273wXBj4ru5LQVKrzBkCQQDhVxxaPwYS+islLeQNBwPuKURkEnH2/iE+auLViBUp6t3f6kVoqokv/Ujm8XOZ+wc1uvL4sj8scNEz7mYJgnUVAkEAg3asy0NSK3DXw8B9Gx8OhwCo4x9CIzqm5IoNPVZTDjpFZRZ7RclhPcoBAj+XzPgnk8mSVBHDm7iur7Ns9QM0IQJAQcQBs1kHdcxrgStWjnLIs955Zld3yWU74JKjZzyTKKuyW6Js5XI4HbhnaXd4jK0V2pmYRfsHsvmuJODkCMx5TQJAaVs3V3IwYsqjXYPNZsl4RwD05W631IYsw9q5SBOpYmdGUm2DmPGezIyM7vbo4ilr6I7eabPcVmwYiTDp13SGZg==";
    // 支付宝公钥
    @BindView(R.id.key_news_switch)
    Switch newsSwitch;
    @BindView(R.id.net_switch)
    Switch netSwitch;
    @BindView(R.id.me_fragmen_clear_cache_right)
    TextView cacheTV;
    @BindView(R.id.me_remove_rl)
    RelativeLayout cacheRL;
    @BindView(R.id.me_about_rl)
    RelativeLayout aboutRL;
    @BindView(R.id.me_fragment_login_iv)
    ImageView login;
    @BindView(R.id.me_fragment_profile_image)
    CircleImageView profileimage;
    @BindView(R.id.me_attention_rl)
    RelativeLayout attentionRL;
    private ProgressDialog progressDialog;

    public MeFragment() {
    }

    public static MeFragment newInstance(String path) {

        Bundle args = new Bundle();
        args.putSerializable("path", path);
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        /**TODO  **/
        /**初始化缓存大小数据**/
        try {
            String cacheSize = HttpUtils.getTotalCacheSize(getContext());
            cacheTV.setText("当前缓存大小为" + cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /***********监听***********/
        aboutRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
        /**cache的监听**/
        cacheRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("你确定清除缓存吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new AsyncTask<Void, Integer, String>() {
                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        progressDialog = ProgressDialog.show(getContext(), "", "正在清除...");
                                        LogTool.log(getClass(), progressDialog.toString());
                                    }

                                    @Override
                                    protected String doInBackground(Void... params) {
                                        /**清除缓存**/
                                        try {
                                            Thread.sleep(3000);
                                            HttpUtils.clearAllCache(getContext());
                                            String cacheSize = HttpUtils.getTotalCacheSize(getContext());
                                            return cacheSize;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return "";
                                    }

                                    @Override
                                    protected void onPostExecute(String temp) {
                                        progressDialog.dismiss();
                                        if (!"".equals(temp)) {
                                            cacheTV.setText("当前缓存大小为" + temp);
                                        } else {
                                            cacheTV.setText("当前缓存大小为" + "???");
                                        }
                                    }
                                }.execute();
                            }
                        })
                        .show();
            }
        });
        /**newSwitch的监听**/
        newsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "要闻推送开启", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "要闻推送关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /**netSwitch的监听**/
        netSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "移动网络开关已开启，使用移动网络播放音视频将不再提醒", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "移动网络开关关闭", Toast.LENGTH_SHORT).show();
                    /**TODO**/
                    /**写关闭移动网络的代码**/
                }
            }
        });
        /**login的监听**/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        /**profileimage的监听**/
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        attentionRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-------------------------begin---------------------------
                String orderInfo = getOrderInfo("戴尔 电脑 外星人系列", "29999外星人", "0.01");

                /**
                 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
                 */
                String sign = sign(orderInfo);
                try {
                    /**
                     * 仅需对sign 做URL编码
                     */
                    sign = URLEncoder.encode(sign, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                /**
                 * 完整的符合支付宝参数规范的订单信息
                 */
                final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
                //---------------------------end-----------------------------------------

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PayTask payTask = new PayTask(getActivity());
                        //在实际开发中的payInfo，是通过Http请求获得的后台数据
                        String pay = payTask.pay(payInfo, true);
                        LogTool.log(getClass(), "run: " + pay);
                        if ("9000".equals(pay)) {
                            LogTool.log(getClass(), "run: success");
                        }
                    }
                }).start();
            }
        });
        return view;
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        //此属性应该修改成自己公司后台服务器的一个链接
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    private void showShare() {
        ShareSDK.initSDK(getContext());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        //oks.disableSSOWhenAuthorize();
// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("Jason的分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://user.qzone.qq.com/1206133471/main");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("其实我只是一个分享而已");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("../image/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://user.qzone.qq.com/1206133471/main");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("输入你的评论吧...");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("Jason.com");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://user.qzone.qq.com/1206133471/main");
        // 启动分享GUI
        oks.show(getContext());
    }

}
