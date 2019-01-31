package cn.zbuter.btdownloadassistant;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MagnetUriAdapter extends RecyclerView.Adapter<MagnetUriAdapter.ViewHolder> {

    private static final String TAG ="MagnetUriAdapter" ;
    private List<MagnetUri> magnetUriList;
    private Context context;

    public MagnetUriAdapter(Context context,List<MagnetUri> magnetUriList){
        this.context = context;
        this.magnetUriList = magnetUriList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.magnet_uri_item,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);





        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MagnetUriAdapter.ViewHolder viewHolder, int i) {
        MagnetUri magnetUri = magnetUriList.get(i);
        viewHolder.tvTitle.setText(magnetUri.getName());
        viewHolder.tvTips.setText(magnetUri.getTips());

        final int index = i;
        Log.d(TAG, "onCreateViewHolder: "+index);
        viewHolder.magnetUriView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String magnet = magnetUriList.get(index).getMagnet();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(magnet));
                    intent.addCategory("android.intent.category.DEFAULT");
                    context.startActivity(intent);  // 点击这个按钮就调用能解析磁力链接的程序去下载
                }catch (Exception e) {
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", magnet);
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(context, "没有找到可以使用磁力链接的软件，已将磁力链接复制到剪切板", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.magnetUriView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final MagnetUri m = magnetUriList.get(index);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage(m.getName()+"\n\n"+m.getTips()
                        +"\n\n"+"收录时间："+m.getCreate_time()
                        +"\n\n"+"磁力链接："+m.getMagnet()
                );
                final String magnet = m.getMagnet();
                builder.setPositiveButton("仅复制磁力链接", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", magnet);
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(context, "已将磁力链接复制到剪切板", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() { return magnetUriList.size();    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView tvTips;
        View magnetUriView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            magnetUriView=itemView;
            tvTitle =itemView.findViewById(R.id.tv_title);
            tvTips = itemView.findViewById(R.id.tv_tips);
        }
    }
}
