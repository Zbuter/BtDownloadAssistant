package cn.zbuter.btdownloadassistant;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SponsorAuthor extends AppCompatActivity implements View.OnLongClickListener {
    private ImageView ivZhifubao;
    private ImageView ivZhifubaohongbao;
    private ImageView ivWeixin;
    private TextView textView;
    private static final String TAG = "SponsorAuthor" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor_author);
        ivZhifubao = (ImageView)findViewById(R.id.iv_zhifubao);
        ivWeixin = (ImageView)findViewById(R.id.iv_weixin);
        ivZhifubaohongbao = (ImageView)findViewById(R.id.iv_zhifubaohongbao);
        textView = (TextView)findViewById(R.id.tv_tips);
        textView.setText("如果觉得这款软件用着还不错的话可以给作者鼓励一下。\n" +
                "\n" +
                "长按以下图片将保存相应图片到本地，并自动跳转到相应的扫一扫界面\n" +
                "\n" +
                "1分也是爱。 1. 支付宝   2. 微信   3. 支付宝红包\n" +
                "\n" +
                "最后感谢您的支持。");

        ivZhifubao.setOnLongClickListener(this);
        ivWeixin.setOnLongClickListener(this);
        ivZhifubaohongbao.setOnLongClickListener(this);

        if (Build.VERSION.SDK_INT >= 26 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }


    public void saveBitmap(View view, String fileName, String path)   {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        BufferedOutputStream bos=null;
        try {
            String subForder = path;
            File foder = new File(subForder);
            if (!foder.exists()) {
                foder.mkdirs();
            }
            File myCaptureFile = new File(subForder, fileName);
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            Toast.makeText(this, "图片已保存", Toast.LENGTH_SHORT).show();




            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        myCaptureFile.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(myCaptureFile.getAbsolutePath())));
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "图片保存失败，请检查是否授权。", Toast.LENGTH_SHORT).show();
        }finally {
            try {
                bitmap.recycle();
                if(bos!=null){
                    bos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onLongClick(View v) {
        if (Build.VERSION.SDK_INT >= 26 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        String filepath = Environment.getExternalStorageDirectory()+"/btDownloadAssistant/";
        String fileName = "";
        switch (v.getId()){
            case R.id.iv_zhifubao:
                fileName = "zhifubao.png";
                saveBitmap(v, fileName,filepath);
                toAliPayScan();
                break;
            case R.id.iv_weixin:
                fileName = "weixin.png";

                saveBitmap(v, fileName,filepath);
                toWeChatScan();
                break;
            case R.id.iv_zhifubaohongbao:
                fileName = "zhifubaohongbao.png";
                saveBitmap(v, fileName,filepath);
                toAliPayScan();
                break;

            default:
                break;
        }


        return false;
    }

    private void toWeChatScan() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(335544320);
            intent.setAction("android.intent.action.VIEW");
            startActivity(intent);
        } catch (Exception e) {
            //Toast.makeText(this, "无法启动微信，请检查是否安装了微信。", Toast.LENGTH_SHORT).show();
        }

    }

    private void toAliPayScan() {
        try {
            //利用Intent打开支付宝
            //支付宝跳过开启动画打开扫码和付款码的urlscheme分别是：
                //alipayqr://platformapi/startapp?saId=10000007
            //alipayqr://platformapi/startapp?saId=20000056
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
             //若无法正常跳转，在此进行错误处理
             //Toast.makeText(this, "打开支付宝失败，请检查是否安装了支付宝", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //同意授权。
                }else{
                    Toast.makeText(this, "您取消了授权，将无法保存图片。", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
