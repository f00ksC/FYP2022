package com.example.a220309opencv;

import static org.opencv.android.OpenCVLoader.initDebug;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {


    private int STORAGE_PERMISSION_CODE = 1;
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    Mat mat1, mat2, mat3;
    private EditText editText;
    int counter = 0;
    int modeCounter = 10;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.myCamera);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);


        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);


                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };


        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if(modeCounter==3){
                    modeCounter = 5;
                }else if(modeCounter==5){
                    modeCounter = 10;
                }else if(modeCounter==10){
                    modeCounter = 30;
                }else if(modeCounter==30){
                    modeCounter = 3;
                };
            }
        });

        requestStoragePermission();




    }




    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat1 = inputFrame.gray();
        Mat clrCrr = new Mat();
        Imgproc.cvtColor(mat1, clrCrr, Imgproc.COLOR_GRAY2BGR);
        QRCodeDetector decoder = new QRCodeDetector();
        Mat points = new Mat(); // points of QR Mat

        Mat dst = new Mat();    //Output frame
        Imgproc.threshold(mat1, dst, 100, 200, Imgproc.THRESH_BINARY); // Threshold and return dst
        //Imgproc.adaptiveThreshold(mat1,dst,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,11,2);



        Mat m = new Mat();
        Core.extractChannel(dst, m, 0);

        if (counter > modeCounter){
            counter = 0;



            if(decoder.detect(mat1,points)){
//                        String x1 = ""; String x2 = "";
//                        String y1 = ""; String y2 = "";
                String temp1=""; String temp2="";


//                        Mat m2 = new Mat();
//                        String data = decoder.detectAndDecode(clrCrr, points,QR); //Decode QR
//                        Imgproc.cvtColor(QR,QR,Imgproc.THRESH_BINARY);
//                        Core.extractChannel(QR, m2, 0);
//                        int totalSizeQR = (int) QR.total();
//                        int whitePxInQR =Core.countNonZero(m2);
//                        int blackPxInQR = (int) (totalSizeQR-whitePxInQR);
//                        float percentageBLKpx= blackPxInQR/blackPxInQR;

                temp1 = Arrays.toString(points.get(0,0));
                temp2 =Arrays.toString(points.get(0,2));

                temp1 = temp1.substring(1,temp1.length()-1);
                temp2=temp2.substring(1,temp2.length()-1);


                List<String> corner1 = Arrays.asList(temp1.split(","));
                List<String> corner2 = Arrays.asList(temp2.split(","));




                Float coordX1 = Float.parseFloat(corner1.get(0));
                Float coordY1 = Float.parseFloat(corner1.get(1));
                Float coordX2 = Float.parseFloat(corner2.get(0));
                Float coordY2 = Float.parseFloat(corner2.get(1));

                Float area = Math.abs(coordX1-coordX2)*Math.abs(coordY1-coordY2);
                double scale = 21.16/area;




                Core.extractChannel(dst, m, 0);
//                        int n = (int) (m.total()-Core.countNonZero(dst)-blackPXtoRemove);
                int n = (int) (m.total()-Core.countNonZero(dst)-0.52*area); // total - white -blackInQR
//                EditText editText =  findViewById(R.id.editText);




                runOnUiThread(new Runnable() {

                public void run() {
                    EditText editText =  findViewById(R.id.editText);
//                    editText.setText("Area = " + (scale*n));
//                    editText.setText("Area = " + String.valueOf(scale*n)+ " Coord 1 = " + coordX1 + ", " + coordY1 +" Coord 1 = " + coordX2 + ", " + coordY2 );
                    editText.setText("Delay= " + modeCounter +" Frames;"+" Area = " + (scale*n));
                    }
                });


            }


        };
        counter = counter+1;


//        Rect rectangle = new Rect();
////        Boolean flagQR =decoder.detect(mat1,points);
//
////        double[] temp = QR.get(1,1);
//        //int nRows = (int) QR.size().area();
//        //int nCol = (int) QR.size().height;
//
//
//
//
//
//
//
//            runOnUiThread(new Runnable() {
//
//                public void run() {
//                    Handler h = new Handler();
////                    String data = decoder.detectAndDecode(clrCrr, points,QR); //Decode QR
//                    EditText editText =  findViewById(R.id.editText);
//
//
//
//
//
//
//
//                    h.postDelayed(this,3000);
//                }
//            });











        return dst;
    }

    @Override
    public void onCameraViewStopped() {
        mat1.release();
//        mat2.release();
//        mat3.release();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mat1 = new Mat(width,height, CvType.CV_8UC4);
//        mat2 = new Mat(width,height, CvType.CV_8UC4);
//        mat3 = new Mat(width,height, CvType.CV_8UC4);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
        }else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase !=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase !=null){
            cameraBridgeViewBase.disableView();
        }
    }




    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.CAMERA}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }



//    static double[] splitter(String coordinate) {
//        double xCoord; double yCoord;
//        int count=0;
//        String temp1; String temp2;
//        int length = coordinate.length();
//        double[] myCoords = {};
//
//
//        for(int i =0;i<length;i++){
//
//            if(coordinate.charAt(i)=='[' ||coordinate.charAt(i)=='['  ){
//
//                count++;
//
//            }else if(coordinate.charAt(i)==','){
//                temp1=coordinate.substring(1,count);
//                temp2=coordinate.substring(count+2,length-2);
//                xCoord=Double.parseDouble(temp1); yCoord=Double.parseDouble(temp2);
//
//                myCoords= new double[]{xCoord, yCoord};
//
//                break;
//            };
//        }
//        return myCoords;
//    }









}