/*
    CameraActivity performs the OCR required to extract the results when the user is showing their
    medical device to the robot
 */
package com.example.healthreadingsdemo;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {



    TextView textView;
    String detectedText;
    String taskName;
    Button confirmButton;
    PreviewView previewView;
    SurfaceHolder holder;
    SurfaceView surfaceView;
    private ImageCapture imageCapture;
    Bitmap cropped;
    private ImageAnalysis imageAnalysis;
    Canvas canvas;
    Paint paint;
    List<Rect> TextBoxes = new ArrayList<>() ;
    int boundingBoxColor = Color.parseColor("#CAF0F8");
    int cameraHeight, cameraWidth, xOffset, yOffset, boxWidth, boxHeight;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int PERMISSIONS_REQUEST_CODE = 1;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public final int RESULTS_REQ = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);

        // Asking for perms to use camera
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CODE);
        }
        // Getting the task name from the previous activity
        taskName = getIntent().getStringExtra("taskName");
        confirmButton = findViewById(R.id.confirm_button);
        textView = findViewById(R.id.confirm_text);

        /* Captures what is on the screen
           when user presses the confirm button */
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detectedText != null && taskName != null)
                {
                    String text = detectedText;
                    //imageAnalysis.clearAnalyzer();
                    Intent i = new Intent(CameraActivity.this,
                                            ResultsActivity.class);

                    /* Sends the extracted result and task name to
                       the results display activity */
                    i.putExtra("result", textView.getText());
                    i.putExtra("taskName", taskName);
                    startActivityForResult(i, RESULTS_REQ);

                }

            }
        });



        //Create the bounding box
        surfaceView = findViewById(R.id.overlay);
        surfaceView.setZOrderOnTop(true);
        holder = surfaceView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);



    }

    @Override
    protected void onResume(){
        super.onResume();
        //Start Camera
        startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    void startCamera(){
        previewView = findViewById(R.id.previewView);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    // Load camera
                    CameraActivity.this.loadCamera(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Load camera function
    private void loadCamera(ProcessCameraProvider cameraProvider) {
        //unbind all previous binds
        cameraProvider.unbindAll();

        //implement preview
        Preview preview = new Preview.Builder()
                .build();

        //select default camera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        //attach textureview to flip preview (since using front facing camera)
        previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        //flipping the previewView
        previewView.setScaleX(-1);


        /* Image Analysis Function
            Analyzes the latest image/frame given from camera and converts to bitmap
             There is code written to crop the frame to analyze a specific section but was not used
             for demo purposes */
        imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(720, 1488))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @SuppressLint({"UnsafeExperimentalUsageError", "UnsafeOptInUsageError"})
            @Override
            public void analyze(@NonNull ImageProxy image) {
                if (image == null || image.getImage() == null) {
                    return;
                }
                final Image mediaImage = image.getImage();
                InputImage images = InputImage.fromMediaImage(mediaImage,
                                    image.getImageInfo().getRotationDegrees());
                // Converted to bitmap
                Bitmap bmp = BitmapUtil.getBitmap(image);

                FrameMetadata frameMetadata =
                        new FrameMetadata.Builder()
                                .setWidth(image.getWidth())
                                .setHeight(image.getHeight())
                                .setRotation(image.getImageInfo().getRotationDegrees())
                                .build();

                /* Following code allow the camera to only analyze/capture the frame within the
                    displayed rectangle */

                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = bmp.getHeight();
                int width = bmp.getWidth();

                int left, right, top, bottom, topOffset, sideOffset;


                if (height > width) {
                    sideOffset = (int) (0.2 * width);
                    topOffset = (int) (0.3 * height);
                } else {
                    topOffset = (int) (0.2 * height);
                    sideOffset = (int) (0.3 * width);
                }

                left = sideOffset;
                top = topOffset;
                right = width - sideOffset;
                bottom = height - topOffset;
                boxHeight = bottom - top;
                boxWidth = right - left;


                //Creating new cropped bitmap
                cropped = Bitmap.createBitmap(bmp, left, top, boxWidth , boxHeight);

                TextRecognizer detector =
                        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                // Replace bmp with cropped bitmap to only analyze within the rectangle
                Task<Text> result =  detector.process(InputImage.fromBitmap(bmp, 0))
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text VisionText) {
                                // When task completed successfully
                                textView = findViewById(R.id.confirm_text);
                                //OCR text is extracted
                                detectedText = VisionText.getText();
                                //Show the OCR text in the textview for user to see
                                textView.setText(detectedText);
                                //for getting blocks and line elements
                                for (Text.TextBlock block: VisionText.getTextBlocks()) {
                                    String blockText = block.getText();
                                    //lines
                                    for (Text.Line line: block.getLines()) {
                                        String lineText = line.getText();
                                        //TextBoxes.add(line.getBoundingBox());
                                        //elements
                                        for (Text.Element element: line.getElements()) {
                                            String elementText = element.getText();
                                        }
                                    }//drawTextRect();
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Log.e("Error",e.toString());

                                    }
                                }).addOnCompleteListener(res -> image.close()) ;
            }
        });

        // Binds analyzer and preview to camera
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this,
                cameraSelector,imageAnalysis,preview);
    }

    // Code to capturePhoto, unused currently but could be useful in the future
    private void capturePhoto() {

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(ImageProxy imageProxy) { //where imageProxy.getFormat() returns the constant for Compressed JPEG format


//
                        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
                        if (mediaImage != null) {
                            if (detectedText != null)
                            {
                                Intent intent = new Intent(CameraActivity.this, ResultsActivity.class);
                                intent.putExtra("detected", detectedText);
                                startActivity(intent);

                            }
                        }
                    }

                    @Override
                    public void onError(ImageCaptureException error) {
                        Toast.makeText(CameraActivity.this, "Error taking image, please try again", Toast.LENGTH_SHORT).show();

                    }
                }
        );


    }

    private Bitmap getCroppedBitmap(Bitmap bmp){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = bmp.getHeight();
        int width = bmp.getWidth();

        int left, right, top, bottom, topOffset, sideOffset;


        if (height > width) {
            sideOffset = (int) (0.2 * width);
            topOffset = (int) (0.3 * height);
        } else {
            topOffset = (int) (0.2 * height);
            sideOffset = (int) (0.3 * width);
        }

        left = sideOffset;
        top = topOffset;
        right = width - sideOffset;
        bottom = height - topOffset;
        boxHeight = bottom - top;
        boxWidth = right - left;


        //Creating new cropped bitmap
        return Bitmap.createBitmap(bmp, left, top, boxWidth , boxHeight);

    }


/* Updated UI to draw boxes around the extracted text. This ended up looking really annoying so it
    was taken out */
    private void drawTextRect( ){



        canvas = holder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);


        //border's properties
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);

        for (Rect rect : TextBoxes){
            canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
        }


        //start at center and then scale by 90% of shortest side

        holder.unlockCanvasAndPost(canvas);

    }

    // Draws the bounding box that would be used to create the cropped bitmap
    private void drawRect() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = previewView.getHeight();
        int width = previewView.getWidth();

        int left, right, top, bottom, topOffset, sideOffset;


        if (height > width) {
            sideOffset = (int) (0.2 * width);
            topOffset = (int) (0.3 * height);
        } else {
            topOffset = (int) (0.2 * height);
            sideOffset = (int) (0.3 * width);
        }

        canvas = holder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(boundingBoxColor);
        paint.setStrokeWidth(5);

        //start at center and then scale by 90% of shortest side
        left = sideOffset;
        top = topOffset;
        right = width - sideOffset;
        bottom = height - topOffset;

        boxHeight = bottom - top;
        boxWidth = right - left;
        //Changing the value of x in diameter/x will change the size of the box ; inversely proportionate to x
        canvas.drawRect(left, top, right, bottom, paint);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);




        holder.unlockCanvasAndPost(canvas);
    }

    /*
     * Callback functions for the surface Holder
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Drawing rectangle
        drawRect();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* If user presses confirm in the results display activity,
             return to Main activity */
        if (requestCode == RESULTS_REQ) {
            if (resultCode == Activity.RESULT_OK) {
                imageAnalysis.clearAnalyzer();
                String result = data.getStringExtra("result");
                String taskName = data.getStringExtra("taskName");
                Intent returnIntent = new Intent();
                // Send formatted results and task name to Main Activity
                returnIntent.putExtra("result",result);
                returnIntent.putExtra("taskName", taskName);
                setResult(Activity.RESULT_OK,returnIntent);
                //push and return intent
                finish();
            }
            // User wants to retake picture
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(CameraActivity.this,
                        "Returning to retake picture",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}