package com.example.e_krushi.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.e_krushi.R;
import com.example.e_krushi.ml.DiseaseDetection;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class DiseaseDetectionActivity extends AppCompatActivity {
    TextView result, demoText, classified, clickHere;
    ImageView imageView, arrowImage;
    Button picture;

    int imageSize = 224; // default image size
    private int numClasses;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_detection);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Disease Detection");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);

        demoText = findViewById(R.id.demoText);
        clickHere = findViewById(R.id.click_here);
        arrowImage = findViewById(R.id.demoArrow);
        classified = findViewById(R.id.classified);

        demoText.setVisibility(View.VISIBLE);
        clickHere.setVisibility(View.GONE);
        arrowImage.setVisibility(View.VISIBLE);
        classified.setVisibility(View.GONE);
        result.setVisibility(View.GONE);

        picture.setOnClickListener(view -> {
            // Launch Camera or Gallery if we have permission
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                // Create intent to capture image
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Create intent to select image from gallery
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Create chooser intent to provide options for camera and gallery
                Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { cameraIntent });

                startActivityForResult(chooserIntent, 1);
            } else {
                // Request camera and storage permissions if not granted
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            demoText.setVisibility(View.GONE);
            clickHere.setVisibility(View.VISIBLE);
            arrowImage.setVisibility(View.GONE);
            classified.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void classifyImage(Bitmap image) {
        try {
            DiseaseDetection model = DiseaseDetection.newInstance(getApplicationContext());

            // Load the TensorFlow Lite model and class labels
            Interpreter.Options options = new Interpreter.Options();
            Interpreter interpreter = new Interpreter(loadModelFile(), options);
            List<String> labels = loadClassLabels();

            // Create input TensorBuffer
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // Get 1D array of 224x224 pixels in the image
            int[] intValue = new int[imageSize * imageSize];
            image.getPixels(intValue, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // Iterate over pixels and extract R, G, B values, add to bytebuffer
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValue[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Run model inference and get the result
            TensorBuffer outputFeature0 = TensorBuffer.createFixedSize(new int[]{1, numClasses}, DataType.FLOAT32);
            interpreter.run(inputFeature0.getBuffer(), outputFeature0.getBuffer());

            // Release model resources
            interpreter.close();

            // Find the index of the class with the highest confidence
            float[] confidence = outputFeature0.getFloatArray();
            if (confidence != null && confidence.length > 0) {
                int maxPos = 0;
                float maxConfidence = 0;
                for (int i = 0; i < confidence.length; i++) {
                    if (confidence[i] > maxConfidence) {
                        maxConfidence = confidence[i];
                        maxPos = i;
                    }
                }

                // Get the predicted class label
                String predictedLabel = labels.get(maxPos);
                result.setText(predictedLabel);
                result.setOnClickListener(view -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + predictedLabel)));
                });
            } else {
                // Handle case where confidence array is null or empty
                result.setText("Unable to classify");
            }

            model.close();

        } catch (IOException e) {
            // Handle the exception
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getApplicationContext().getAssets().openFd("plant_disease_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadClassLabels() {
        List<String> labels = new ArrayList<>();
        try {
            InputStream labelsInput = getApplicationContext().getAssets().open("plant_labels.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(labelsInput));
            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(line);
            }
            reader.close();
        } catch (IOException e) {
            // Handle the exception
        }
        numClasses = labels.size(); // Set the value of numClasses
        return labels;
    }
}
