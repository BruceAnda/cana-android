package zhaoliang.com.uploadfile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoliang on 2017/4/20.
 */

public class UploadUtils {

    private static Context mContent;
    private static String mEndpoint = "http://oss-cn-qingdao.aliyuncs.com";
    private static String mCallbackAddress = "http://oss-cn-qingdao.aliyuncs.com:23450";
    private static String mBucket = "ivita-files";
    //private static String mBucket = "xdua-files/Avatar/";
    // private static String mBucket = "xdua-files";
    private static String mStsServer = "http://sts.xdua.org:3000";
    private static OSS ossClient;
    private static List<OSSAsyncTask> tasks = new ArrayList<>();
    private static String TAG = UploadUtils.class.getSimpleName();

    /**
     * 初始化 这些参数都是阿里云的参数，具体获取方式请查看阿里云 https://help.aliyun.com/document_detail/32046.html?spm=5176.doc32047.6.699.mzSLfm
     *
     * @param endpoint
     * @param callbackAddress
     * @param bucket
     * @param stsServer
     */
    public static void init(Context context, String endpoint, String callbackAddress, String bucket, String stsServer) {
        mContent = context;
        mEndpoint = endpoint;
        mCallbackAddress = callbackAddress;
        mBucket = bucket;
        mStsServer = stsServer;

        initOSS();
    }

    /**
     * 初始化OSS
     */
    public static void initOSS() {
        //使用自己的获取STSToken的类
        OSSCredentialProvider credentialProvider = new STSGetter(mStsServer);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

        OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv

        //OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider("<StsToken.AccessKeyId>", "<StsToken.SecretKeyId>", "<StsToken.SecurityToken>");

        ossClient = new OSSClient(mContent, mEndpoint, credentialProvider, conf);
    }

    public static void asyncPutFile(String object,
                                    String localFile, final OSSCompletedCallback<PutObjectRequest, PutObjectResult> userCallback,
                                    final OSSProgressCallback<PutObjectRequest> userProgressCallback) {
        // Construct an upload request
        PutObjectRequest put = new PutObjectRequest(mBucket, object, localFile);
        put.setProgressCallback(userProgressCallback);
        ossClient.asyncPutObject(put, userCallback);


       /* // You can set progress callback during asynchronous upload
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // Request exception
                if (clientExcepion != null) {
                    // Local exception, such as a network exception
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // Service exception
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });*/
    }

   /* *//**
     * 上传文件
     *
     * @param object
     * @param localFile
     * @param userCallback
     * @param userProgressCallback
     *//*
    public static void asyncPutFile(String object,
                                    String localFile,
                                    @NonNull final OSSCompletedCallback<PutObjectRequest, PutObjectResult> userCallback,
                                    final OSSProgressCallback<PutObjectRequest> userProgressCallback) {
        Log.i(TAG, "asyncPutFile");
        if (object.equals("")) {
            Log.w("AsyncPutFile", "ObjectNull");
            return;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("AsyncPutImage", "FileNotExist");
            Log.w("LocalFile", localFile);
            return;
        }


        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(mBucket, object, localFile);

        if (mCallbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", mCallbackAddress);
                    //callbackBody可以自定义传入的信息
                    put("callbackBody", "filename=${object}");
                }
            });
        }

        // 异步上传时可以设置进度回调
        if (userProgressCallback != null) {
            put.setProgressCallback(userProgressCallback);
        }
        *//*
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                int progress = (int) (100 * currentSize / totalSize);

                ImageDisplayer.updateProgress(progress);
                ImageDisplayer.displayInfo("上传进度: " + String.valueOf(progress) + "%");
            }
        });*//*

        Log.i(TAG, "开始上传" + object + ":" + localFile);
        tasks.add(ossClient.asyncPutObject(put, userCallback));
    }

    public static void cancelAllTask() {
        for (OSSAsyncTask task : tasks) {
            task.cancel();
        }}
*/
}
