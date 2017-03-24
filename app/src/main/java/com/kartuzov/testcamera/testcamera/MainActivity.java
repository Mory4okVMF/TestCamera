package com.kartuzov.testcamera.testcamera;



import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    SurfaceView svColor;
    SurfaceHolder shColor;
    ImageView ivBlack;
    HolderCallback holderCallback;
    CameraCallback cameraCallback;
    Camera camera;

    final int CAMERA_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ivBlack = (ImageView) findViewById(R.id.svBlack);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        ivBlack.setColorFilter(filter);

        svColor = (SurfaceView) findViewById(R.id.svColor);
        shColor = svColor.getHolder();


        holderCallback = new HolderCallback();
        cameraCallback = new CameraCallback();
        shColor.addCallback(holderCallback);

    }

    @Override
    protected void onResume() {
        super.onResume();
           try {
               camera = Camera.open(CAMERA_ID);
           } catch (Exception e) {
               Toast toast = Toast.makeText(this, R.string.err, Toast.LENGTH_LONG);
               toast.show();
           }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null)
            camera.release();
        camera = null;
    }

    private void startCamera ( final SurfaceHolder holder ){
        try {
        camera.setPreviewDisplay(holder);
        camera.setPreviewCallback(cameraCallback);
        camera.startPreview();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }


    class HolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(final SurfaceHolder holder) {
                if(camera!=null) {
                    startCamera(holder);
                }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            if(camera!=null) {
                camera.stopPreview();
                startCamera(holder);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    }

    class CameraCallback implements Camera.PreviewCallback {

        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            int width = parameters.getPreviewSize().width;
            int height = parameters.getPreviewSize().height;
            YuvImage yuv = new YuvImage(bytes, parameters.getPreviewFormat(), width, height, null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

            byte[] byt = out.toByteArray();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(byt, 0, byt.length);

            ivBlack.setImageBitmap(bitmap);

        }
    }

}
