package cn.zbuter.btdownloadassistant;



import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//OKHttp工具类
public class OKHttpUtil {
    private static OKHttpUtil okHttpUtil;
    private final OkHttpClient okHttpClient;


    public OKHttpUtil() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    public static OKHttpUtil getInstance() {
        if (null == okHttpUtil) {
            synchronized (OKHttpUtil.class) {
                if (null == okHttpUtil) {
                    okHttpUtil = new OKHttpUtil();
                }
            }
        }
        return okHttpUtil;
    }

    //get 请求
    public void get(String urlString, Callback callback) {

        Request request = new Request.Builder().url(urlString).get().build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    //post请求
    public void post(String urlString, Map<String,String> bodyParams, final Callback callback) {
        //1构造RequestBody
        RequestBody body=setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(urlString)
                .addHeader("User-Agent","Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Mobile Safari/537.36")
                .build();
        //3 将Request封装为Call
        Call call = okHttpClient.newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               callback.onFailure(call,e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(call,response);
            }
        });
        okHttpClient.newCall(request).enqueue(callback);
    }


    private RequestBody setRequestBody(Map<String, String> BodyParams){
        RequestBody body=null;
        FormBody.Builder formEncodingBuilder=new FormBody.Builder();
        if(BodyParams != null){
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {

                key = iterator.next().toString();
                formEncodingBuilder.add(key, BodyParams.get(key));
                Log.d("post http", "post_Params==="+key+"===="+BodyParams.get(key));
            }
        }
        body=formEncodingBuilder.build();
        return body;
    }


    //添加拦截器
    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String method = request.method();
            Response proceed = chain.proceed(request);
            return proceed;
        }
    }
}

