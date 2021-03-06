package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.util.Base64.encodeToString;

public class AddPostActivity extends AppCompatActivity {

    private ImageView imageViewId;
    private EditText postDescriptionET;
    private EditText postTitleET;
    private EditText postPriceET;

    //길이 받기
    private EditText postWidthET;
    private EditText postHeightET;
    private EditText postDepthET;



    private Button addPostBtn;
    private ProgressBar setUpProgressBar;
    private Uri postImageUri = null;
    private String currentUser;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private Map<String, String> TAG = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        imageViewId = findViewById(R.id.imageViewId);
        postDescriptionET = findViewById(R.id.postDescriptionET);
        postTitleET = findViewById(R.id.postTitleET);
        postPriceET = findViewById(R.id.postPriceET);
        postWidthET = findViewById(R.id.postWidthET);
        postHeightET = findViewById(R.id.postHeightET);
        postDepthET = findViewById(R.id.postDepthET);

        addPostBtn = findViewById(R.id.addPostBtn);
        setUpProgressBar = findViewById(R.id.setUpProgressBar);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser().getUid();

        imageViewId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(AddPostActivity.this);
            }
        });

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String description = postDescriptionET.getText().toString();
                final String title = postTitleET.getText().toString();
                final String price = postPriceET.getText().toString();


                final String width = postWidthET.getText().toString();
                final String height = postHeightET.getText().toString();
                final String depth = postDepthET.getText().toString();

                final Double doubleWidth;
                final Double doubleHeight;
                final Double doubleDepth;


                //빈칸이거나 숫자화 불가능하면 모두 0으로 처리
                if(!(isStringDouble(width) && isStringDouble(height) && isStringDouble(depth)))
                {
                    doubleWidth = (double)0;
                    doubleDepth = (double)0;
                    doubleHeight = (double)0;

                    /**
                     * Size가 모두 0일 경우에는 AR을 실행시킬 수 없도록 하면 될것같습니다.
                     * */

                }
                else
                {
                    doubleWidth = Double.valueOf(width);
                    doubleDepth = Double.valueOf(depth);
                    doubleHeight = Double.valueOf(height);
                }


                if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(price) && postImageUri != null) {

                    setUpProgressBar.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    // Uri to byte array transfer
                    byte[] imageData = getImageDataFromUri(postImageUri, 720, 720, 50);

                    final UploadTask  post_image_path = storageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);
                    post_image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            final String dwonloadUri = task.getResult().getDownloadUrl().toString();

                            if (task.isSuccessful()) {

                                // Uri to byte array transfer
                                byte[] thumbData = getImageDataFromUri(postImageUri, 240, 240, 6);

                                UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg").putBytes(thumbData);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                       final String dwonloadThumbUri = taskSnapshot.getDownloadUrl().toString();

                                        String won = Currency.getInstance(Locale.KOREA).getSymbol();

                                        String priceform;
                                        if(!isStringDouble(price))
                                            priceform = "0";
                                        else {
                                            DecimalFormat df = new DecimalFormat("#,###");
                                            priceform = df.format(Integer.parseInt(price));

                                        }
                                        String swidth = doubleWidth.toString();
                                        String sdepth = doubleDepth.toString();
                                        String sheight = doubleHeight.toString();

                                        Map<String, String> size = new HashMap<>();

                                        size.put("width",swidth);
                                        size.put("height",sheight);
                                        size.put("depth",sdepth);


                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image_url", dwonloadUri);
                                        postMap.put("thumb_url", dwonloadThumbUri);
                                        postMap.put("description", description);
                                        postMap.put("title",title);
                                        postMap.put("price", won.concat(priceform));
                                        postMap.put("current_UserId", currentUser);
                                        postMap.put("post_time", FieldValue.serverTimestamp());
                                        postMap.put("size",size);
                                        postMap.put("TAG",TAG);



                                        //FirebaseFirestore
                                        firebaseFirestore.collection("Posts").add(postMap).
                                                addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(AddPostActivity.this, "Post was added", Toast.LENGTH_SHORT).show();
                                                    imageViewId.setImageResource(R.drawable.memories);
                                                    postDescriptionET.setText("");
                                                    postTitleET.setText("");
                                                    postPriceET.setText("");
                                                    postWidthET.setText("");
                                                    postDepthET.setText("");
                                                    postHeightET.setText("");

                                               /*     Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();*/

                                                } else {
                                                    String errorMessage = task.getException().getMessage();
                                                    Toast.makeText(AddPostActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                }

                                                setUpProgressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                setUpProgressBar.setVisibility(View.INVISIBLE);

                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(AddPostActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                imageViewId.setImageURI(postImageUri);

                // send a request
                new Thread() {
                    public void run() {
                        String url = "https://vision.googleapis.com/v1/images:annotate?key=" + getString(R.string.vision_key);
                        MediaType JSON = MediaType.get("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient();
                        try {
                            byte[] imageData = getImageDataFromUri(postImageUri, 240, 240, 6);

                            JSONObject requestData = encodeJSONRequest(imageData);

                            RequestBody body = RequestBody.create(JSON, requestData.toString());
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .build();
                            Response response = client.newCall(request).execute();

                            String resultStr = response.body().string();
                            Log.i("Result", resultStr);
                            String label = decodeJSONResponse(resultStr);
                            Log.i("Result", label);
                            postDescriptionET.setText(label);
                        } catch (Exception e) {
                            Log.e("REST API", e.toString());
                        }
                    }
                }.start();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private byte[] getImageDataFromUri(Uri imageUri, int maxWidth, int maxHeight, int quality) {
        File actualImageFile = new File(postImageUri.getPath());
        Bitmap compressedImageBitmap = null;
        try {
            compressedImageBitmap = new Compressor(AddPostActivity.this)
                    .setMaxWidth(maxWidth)
                    .setMaxHeight(maxHeight)
                    .setQuality(quality)
                    .compressToBitmap(actualImageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }
    }

    private JSONObject encodeJSONRequest(byte[] imageData) {
        String imageBase64 = Base64.encodeToString(imageData, 0);
        JSONObject finalRequest = null;
        try {
            JSONObject imageObj = new JSONObject();
            imageObj.put("content", imageBase64);
            JSONObject featureObj = new JSONObject();
            featureObj.put("type", "LABEL_DETECTION");

            //몇개를 받을것인가?
            featureObj.put("maxResults", 1);
            JSONArray featureArr = new JSONArray();
            featureArr.put(featureObj);

            JSONObject requestObj = new JSONObject();
            requestObj.put("image", imageObj);
            requestObj.put("features", featureArr);

            JSONArray requestArr = new JSONArray();
            requestArr.put(requestObj);

            finalRequest = new JSONObject();
            finalRequest.put("requests", requestArr);
        } catch(Exception e) {
            Log.e("JSON Parsing", e.toString());
        } finally {
            return finalRequest;
        }
    }

    private String decodeJSONResponse(String responseData) {
        String result = null;
        TAG.clear();
        try {
            JSONObject response = new JSONObject(responseData);

            JSONArray responseArr = response.getJSONArray("responses");
            JSONObject responseSingle = responseArr.getJSONObject(0);
            JSONArray annotationArr = responseSingle.getJSONArray("labelAnnotations");

            // use first annotation only
            JSONObject annotation = annotationArr.getJSONObject(0);
            result = annotation.getString("description");
        } catch(Exception e) {
            Log.e("JSON Parsing", e.toString());
        } finally {
            return result;
        }
    }

    //문자가 숫자화 가능한가?
    public static boolean isStringDouble(String s)
    {
        try{

            if(s.length() <=0)
                return false;

            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }


}
