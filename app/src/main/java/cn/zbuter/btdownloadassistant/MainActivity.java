package cn.zbuter.btdownloadassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final String MAIN_URL = "https://bthaha.men";
    private Context context;
    private SearchView mSearchView;
    private RecyclerView recyclerView;
    private List<MagnetUri> magnetUriList;
    private MainActivityMagnetUriAdapter adapter;
    private String curUrl = "";
    private boolean isGetPage = false;
    private String keyword="";
    private ProgressDialog processDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                MagnetUri m = (MagnetUri) msg.obj;
                magnetUriList.add(m);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "handleMessage: "+magnetUriList.size());
                processDialog.dismiss();
            }
        }
    };

    //初始化菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return  true;
    }

    // 菜单选项被选中时候调用这里
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.sponsor_authot:
                intent = new Intent(MainActivity.this,SponsorAuthor.class);
                startActivity(intent);
                break;
            case R.id.video:
                intent = new Intent(MainActivity.this,FavorivteMagnets.class);
                startActivity(intent);
                break;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        magnetUriList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.rv_recycler);
        getSupportActionBar().setTitle("搜兔SosoTool  By: Zbuter");

        mSearchView = (SearchView) findViewById(R.id.search);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint("在这里输入需要查找的内容。");
        mSearchView.setFocusable(false);
        mSearchView.clearFocus();

        // 正在加载的对话框
        processDialog = new ProgressDialog(context);
        processDialog.setMessage("正在加载...");
        processDialog.setCanceledOnTouchOutside(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String queryText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String queryText) {
                if(!processDialog.isShowing()){
                    processDialog.show();
                }
                magnetUriList.clear();
                keyword = queryText;
                curUrl = MAIN_URL+"/cn/search/" + keyword + "/";

                getMagnetUriList();

                adapter.notifyDataSetChanged();
                return true;
            }

        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener(){
            //用来标记是否正在向最后一个滑动，既是否向下滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisiblePosition = manager.findLastVisibleItemPosition();

                    // 判断是否滚动到底部
                    Log.d(TAG, "onScrollStateChanged: "+(lastVisiblePosition)+" "+magnetUriList.size());
                    if (magnetUriList.size()-1 == (lastVisiblePosition)) {
                        //加载更多功能的代码
                        if(curUrl.endsWith("#")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "没有更多内容了。", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "正在加载...", Toast.LENGTH_SHORT).show();
                                }
                            });
                            getMagnetUriList();
                        }

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if(dy > 0){
                    //大于0表示，正在向下滚动
                    isSlidingToLast = true;
                }else{
                    //小于等于0 表示停止或向上滚动
                    isSlidingToLast = false;
                }

            }
        });
        adapter = new MainActivityMagnetUriAdapter(this,magnetUriList);
        recyclerView.setAdapter(adapter);


    }

    // 判断列表中的磁力链接是否都已经加载完毕。
    public boolean isMagnetListGetFinished(){
        if(magnetUriList==null){
            return false;
        }
        for(MagnetUri m :magnetUriList){
            if(m.getMagnet()==null){
                return false;
            }
        }
        return true;

    }

    public void getMagnetUriList(){
        if(isGetPage){
            return;
        }
        isGetPage=true;
        OKHttpUtil.getInstance().get(curUrl ,new MyCallBack() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "网络链接错误，请检查您的网络。", Toast.LENGTH_SHORT).show();
                        processDialog.dismiss();
                        isGetPage=false;
                    }
                });

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String html = response.body().string();
                parserMagnetUri(keyword,html);
                if(isMagnetListGetFinished()){
                    /*
                    *
                    *     Marking
                    *
                    *
                    *
                    *
                    *
                    * */
                }
                isGetPage=false;

            }
        });


    }

    public String parserMagnetUri(String keyword,String html){
        String nextUrl = null;
        Document doc = Jsoup.parse(html);
        Elements tables = doc.getElementsByClass("table");
        if(tables.isEmpty()){// 当前页面没有磁力链接
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "没有找到相关的磁力链接", Toast.LENGTH_SHORT).show();
                    if(processDialog.isShowing()){
                        processDialog.dismiss();
                    }
                    isGetPage=false;
                }
            });
        }else{
            Elements trNodes = tables.first().getElementsByTag("tr");
            nextUrl = getNextUrl(keyword,html);
            for(Element trNode : trNodes){
                MagnetUri magnetUri=new MagnetUri();;
                Element tdNode = trNode.getElementsByTag("td").first();
                String title = tdNode.getElementsByTag("div").first()
                        .getElementsByTag("a").first().attr("title");
                String href = tdNode.getElementsByTag("div").first()
                        .getElementsByTag("a").first().attr("href");
                int end = href.lastIndexOf(".");
                int start = href.lastIndexOf("/");
                // 获得这个磁力链接的id
                String id = href.substring(start+1,end);
                String tips = tdNode.getElementsByClass("tail").first()
                        .text().trim();
                magnetUri.setName(title);
                magnetUri.setTips(tips);
                magnetUri.setId(id);
                Log.d(TAG, "parserMagnetUri: "+magnetUri);
//                magnetUriList.add(magnetUri);
                mulThreadParserMagnetUri(magnetUri,id);
                Message msg = Message.obtain();
                msg.what=1;
                msg.obj=magnetUri;
                handler.sendMessage(msg);
            }
        }

        curUrl = nextUrl;
        return nextUrl;
    }
    //解析json格式
    public void mulThreadParserMagnetUri(final MagnetUri magnetUri, final String id){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                OKHttpUtil.getInstance().get("https://bthaha.men/api/json_info?hashes=" + id, new MyCallBack() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "网络链接错误，请检查您的网络。", Toast.LENGTH_SHORT).show();
                                processDialog.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 磁力详细页面的解析
                        String jsonData = response.body().string();
                        try{
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONArray result = jsonObject.getJSONArray("result");
                            JSONObject j = result.getJSONObject(0);
                            String create_time = j.getString("create_time");
                            String magnet = "magnet:?xt=urn:btih:"+j.get("info_hash").toString();   //拼接成磁力链接格式
                            String length = j.get("length").toString();
                            String name = j.get("name").toString();
                            Log.d(TAG, "onResponse: "+magnet);
                            magnetUri.setMagnet(magnet);
                            magnetUri.setLength(Long.parseLong(length));
                            magnetUri.setCreateTime(create_time);

                        }catch (JSONException e){
                            Log.e("now test", "onResponse: ",e );
                        }
                    }
                });
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    public String getNextUrl(String keyword ,String html){
        String url = MAIN_URL+"/cn/search/" + keyword + "/";
        Document doc = Jsoup.parse(html);
        String href = doc.getElementsByClass("pagination").first().getElementsByTag("li").last()
                .getElementsByTag("a").attr("href");
        Log.d(TAG, "getNextUrl: "+url+href);
        return url+href;
    }
}
