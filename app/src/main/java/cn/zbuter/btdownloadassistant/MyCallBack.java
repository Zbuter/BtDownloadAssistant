package cn.zbuter.btdownloadassistant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class MyCallBack implements Callback {
    @Override
    public void onFailure(Call call, IOException e){

    }

    @Override
    public abstract  void onResponse(Call call, Response response) throws IOException;
}
