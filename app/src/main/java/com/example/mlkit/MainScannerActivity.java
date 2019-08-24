package com.example.mlkit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mlkit.helpers.ImageHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import com.hmomeni.progresscircula.ProgressCircula;
import com.yalantis.ucrop.UCrop;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;


public class MainScannerActivity extends BaseActivity {
    private Bitmap mBitmap;
    private ImageView mImageView;
    private Uri dataUri;
    private ProgressCircula progressCircula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        mImageView = findViewById(R.id.image_view);
        progressCircula = findViewById(R.id.progressBar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBitmap != null) {
                    runTextRecognition();
                }
            }
        });


        FloatingActionButton cropFloatingTextBtn = findViewById(R.id.crop);
        cropFloatingTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performCrop();
            }

        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case RC_STORAGE_PERMS1:
                case RC_STORAGE_PERMS2:
                    checkStoragePermission(requestCode);
                    break;
                case RC_SELECT_PICTURE:
                    dataUri = data.getData();
                    String path = ImageHelper.getPath(this, dataUri);
                    if (path == null) {
                        mBitmap = ImageHelper.resizeImage(imageFile, this, dataUri, mImageView);
                    } else {
                        mBitmap = ImageHelper.resizeImage(imageFile, path, mImageView);
                    }
                    if (mBitmap != null) {
                        mImageView.setImageBitmap(mBitmap);
                    }
                    break;
                case RC_TAKE_PICTURE:
                    mBitmap = ImageHelper.resizeImage(imageFile, imageFile.getPath(), mImageView);
                    dataUri = Uri.fromFile(imageFile);
                    if (mBitmap != null) {
                        mImageView.setImageBitmap(mBitmap);
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    Uri uri = UCrop.getOutput(data);
                    showImage(uri);
                    break;
            }


        }

    }

    private void showImage(Uri imageUri) {
        mBitmap = ImageHelper.resizeImage(imageFile, imageFile.getPath(), mImageView);
        mImageView.setImageBitmap(mBitmap);
    }

    private void performCrop() {
        if (dataUri != null) {
            UCrop.of(dataUri, dataUri)
//                    .withAspectRatio(16, 9)
//                    .withMaxResultSize(100, 100)
                    .start(this);
        }
    }

    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText texts) {
                processTextRecognitionResult(texts);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }


    private void processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {
        progressCircula.setVisibility(View.VISIBLE);

        if (firebaseVisionText.getTextBlocks().size() == 0) {
            return;
        }
        final StringBuilder ocrText = new StringBuilder();
        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {

            //In case you want to extract each line
            for (FirebaseVisionText.Line line : block.getLines()) {
                for (FirebaseVisionText.Element element : line.getElements()) {
                    ocrText.append(element.getText()).append(" ");
                }
            }
        }

        if (!ocrText.toString().isEmpty()) {
            Intent intent = new Intent(this, DetectTextActivity.class);
            intent.putExtra("detectedText", ocrText.toString());
            startActivity(intent);
        }
        progressCircula.setVisibility(View.INVISIBLE);

    }
}
