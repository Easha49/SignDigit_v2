package com.example.youtubeactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.youtubeactivity.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ModelActivity extends AppCompatActivity {

    TextView result, confidence;
    ImageView imageView;
    Button picture,upload;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        this.getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar ().hide ();
        setContentView(R.layout.activity_model);

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.buttonCapture);
        upload=findViewById (R.id.buttonUpload);

        /*upload.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent (Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult (intent,3);
            }
        });*/
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
    }
    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext ());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer= ByteBuffer.allocate (4*imageSize*imageSize*3);
            byteBuffer.order (ByteOrder.nativeOrder ());

            int [] intValues=new int[imageSize*imageSize];
            image.getPixels (intValues,0,image.getWidth (),0,0,image.getWidth (),image.getHeight ());
            int pixel=0;
            for(int i=0;i<imageSize;i++){
                for(int j=0;j<imageSize;j++){
                    int val=intValues[pixel++];
                    byteBuffer.putFloat (((val>>16)& 0xFF)*(1.f/255.f));
                    byteBuffer.putFloat (((val>>8)& 0xFF)*(1.f/255.f));
                    byteBuffer.putFloat ((val>>16& 0xFF)*(1.f/255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences=outputFeature0.getFloatArray ();
            int maxPos=0;
            float maxConfidence=0;
            for (int i=0;i<confidences.length;i++){
                if(confidences[i]>maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes={"Digit 1","Digit 0","Digit 2","Digit 3","Digit 5","Digit 6","Digit 7","Digit 8","Digit 9","Digit 4"};
            result.setText (classes[maxPos]);

            String s="";
            for (int i=0;i<classes.length;i++){
                s+=String.format ("%s: %.1f%%\n",classes[i],confidences[i]*100);

            }
            confidence.setText(s);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image=(Bitmap) data.getExtras ().get("data");
            int dimension=Math.min(image.getWidth(),image.getHeight ());
            image=ThumbnailUtils.extractThumbnail (image,dimension,dimension);
            imageView.setImageBitmap (image);

            image=Bitmap.createScaledBitmap (image,imageSize,imageSize,false);
            classifyImage(image);
        }
        else if(requestCode==10){
            if(data!=null){
                Uri uri = data.getData();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    int dimension = Math.min(image.getWidth(),image.getHeight()) ;
                    image = ThumbnailUtils.extractThumbnail(image,dimension,dimension) ;
                    imageView.setImageBitmap(image);
                    image = Bitmap.createScaledBitmap(image , imageSize , imageSize , false  ) ;
                    classifyImage(image) ;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}