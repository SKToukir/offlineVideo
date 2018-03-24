package currencyconverter.solution.com.filedownload;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.downloader.Status;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import currencyconverter.solution.com.filedownload.db.DBHandler;
import currencyconverter.solution.com.filedownload.db.DbModelClass;
import currencyconverter.solution.com.filedownload.download.DownloadTask;

public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private DbModelClass dbModelClass;

    private Button btnDownload;
    private String imageDownloadPath;
    private VideoView videoView;
    File apkStorage;
    String imageFileName;
    List<DbModelClass> list = new ArrayList<>();
    String imageUrl = "https://images.pexels.com/photos/248797/pexels-photo-248797.jpeg?auto=compress&cs=tinysrgb&h=650&w=940";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        getAllDataFromDataBase();

        btnDownload = findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Please wait!",Toast.LENGTH_LONG).show();
                downloadImage();
            }
        });
    }

    private void getAllDataFromDataBase() {

        dbHandler = new DBHandler(this);
        dbModelClass = new DbModelClass();

        list = dbHandler.getAllContacts();

        for (int i = 0; i<list.size(); i++){
            Log.d("Response",list.get(i).getImageFileName()+"\n"+list.get(i).getVideoFileName()+"\n"+list.get(i).getVideoFilePath()+"\n"+list.get(i).getImageFilePath());
        }

    }

    private void downloadImage() {

                Picasso.with(this)
                .load(imageUrl)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                              try {
                              ContextWrapper cw = new ContextWrapper(getApplicationContext());
                              File directory = cw.getDir("imagedir", MODE_PRIVATE);

                              if (!directory.exists()) {
                                  directory.mkdir();
                              }

                              imageFileName = "losiidl.jpg";
                              File myPath = new File(directory, imageFileName);
                              FileOutputStream fos = null;

                              try {
                                  fos = new FileOutputStream(myPath);
                                  bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                              } catch (Exception e) {
                                  e.printStackTrace();
                              } finally {
                                  try {
                                      fos.close();
                                  } catch (Exception e) {
                                      e.printStackTrace();
                                  }
                              }
                                imageDownloadPath = directory.getPath();
                              Log.d("FilePath", "dd "+myPath.getPath());
                              new DownloadTask(MainActivity.this,"http://androhub.com/demo/demo.mp4", imageDownloadPath, imageFileName);

                          }

                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                          }
                      }
                );

    }

    private void download(String downloadVideoUrl) {

        apkStorage = new File(
                Environment.getExternalStorageDirectory() + "/"
                        + "Toukir");
        //If File is not present create directory
        if (!apkStorage.exists()) {
            apkStorage.mkdir();
            Log.e("Response", "Directory Created.");
        }

        int downloadId = PRDownloader.download(downloadVideoUrl, apkStorage.getPath(), "LOLO.mp4")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        Log.d("Response",progress.toString());
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Toast.makeText(getApplicationContext(), "Complete",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
        Status status = PRDownloader.getStatus(downloadId);
        Log.d("Response",status.toString());
    }


    public void btnPlayVideo(View view) {

        ImageView imageView = findViewById(R.id.imageView);
       // Picasso.with(this).load(new File(imageDownloadPath)).into(imageView);
        Log.d("FilePath", list.get(0).getImageFilePath() +" "+ list.get(0).getImageFileName());
        try {
            File file = new File(list.get(0).getImageFilePath(), list.get(0).getImageFileName());
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            imageView.setImageBitmap(bitmap);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        //Log.d("Response", apkStorage.getPath());

        videoView = (VideoView)findViewById(R.id.videoView);
        videoView.setVideoPath(list.get(0).getVideoFilePath());
        videoView.start();

    }
}
