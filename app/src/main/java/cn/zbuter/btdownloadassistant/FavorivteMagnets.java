package cn.zbuter.btdownloadassistant;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class FavorivteMagnets extends AppCompatActivity {
    private MyDatabaseHelper myDatabaseHelper;

    private RecyclerView recyclerView;
    private List<MagnetUri> magnetUriList;
    private FavoriteMagnetUriAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorivte_magnets);
        getSupportActionBar().setTitle("搜兔SosoTool  By: Zbuter");
        initView();
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        Cursor cur = database.query("MagnetUris",null,null,null,null,null,null);
        while(cur.moveToNext()){

            String name = cur.getString(cur.getColumnIndex("name"));
            String magnet = cur.getString(cur.getColumnIndex("magnet"));
            String id = cur.getString(cur.getColumnIndex("id"));
            String createTime = cur.getString(cur.getColumnIndex("createTime"));
            String tips = cur.getString(cur.getColumnIndex("tips"));
            long length = cur.getLong(cur.getColumnIndex("length"));


            MagnetUri magnetUri =  new MagnetUri(id, name, tips,magnet, length, createTime);
            magnetUriList.add(magnetUri);
        }

    }

    public void initView(){
        magnetUriList = new ArrayList<>();

        myDatabaseHelper = new MyDatabaseHelper(this,"FavoriteMagnets.db",null ,1);

        recyclerView = findViewById(R.id.rv_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(FavorivteMagnets.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FavoriteMagnetUriAdapter(this,magnetUriList);

        recyclerView.setAdapter(adapter);

    }
}
