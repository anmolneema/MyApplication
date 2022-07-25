package com.attendance.office.tensorflow.lite.examples.detection;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import java.sql.Timestamp;
//import java.util.List;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.hardware.Camera;
//import android.hardware.Camera.CameraInfo;
//import android.hardware.Camera.ErrorCallback;
//import android.hardware.Camera.Parameters;
//import android.hardware.Camera.PictureCallback;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.provider.MediaStore.Images;
//import android.util.Log;
//import android.view.ContextThemeWrapper;
//import android.view.Surface;
//import android.view.SurfaceHolder;
//import android.view.SurfaceHolder.Callback;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.attendance.office.R;
//
//public class CameraDemoActivity extends Activity implements Callback,
//		OnClickListener {
//
//	private SurfaceView surfaceView;
//	private SurfaceHolder surfaceHolder;
//	private Camera camera;
//	private Button flipCamera;
//	private Button flashCameraButton;
//	private Button captureImage;
//	private int cameraId;
//	private boolean flashmode = false;
//	private int rotation;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.camerademo_activity);
//		// camera surface view created
//		cameraId = CameraInfo.CAMERA_FACING_FRONT;
//		flipCamera = (Button) findViewById(R.id.flipCamera);
//		flashCameraButton = (Button) findViewById(R.id.flash);
//		captureImage = (Button) findViewById(R.id.captureImage);
//		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//		surfaceHolder = surfaceView.getHolder();
//		surfaceHolder.addCallback(this);
//		flipCamera.setOnClickListener(this);
//		captureImage.setOnClickListener(this);
//		flashCameraButton.setOnClickListener(this);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//		if (Camera.getNumberOfCameras() > 1) {
//			flipCamera.setVisibility(View.GONE);
//		}
//		if (!getBaseContext().getPackageManager().hasSystemFeature(
//				PackageManager.FEATURE_CAMERA_FLASH)) {
//			flashCameraButton.setVisibility(View.GONE);
//		}
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		if (!openCamera(CameraInfo.CAMERA_FACING_FRONT)) {
//			alertCameraDialog();
//		}
//
//	}
//
//	private boolean openCamera(int id) {
//		boolean result = false;
//		cameraId = id;
//		releaseCamera();
//		try {
//			camera = Camera.open(cameraId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (camera != null) {
//			try {
//				setUpCamera(camera);
//				camera.setErrorCallback(new ErrorCallback() {
//
//					@Override
//					public void onError(int error, Camera camera) {
//
//					}
//				});
//				camera.setPreviewDisplay(surfaceHolder);
//				camera.startPreview();
//				result = true;
//			} catch (IOException e) {
//				e.printStackTrace();
//				result = false;
//				releaseCamera();
//			}
//		}
//		return result;
//	}
//
//	private void setUpCamera(Camera c) {
//		CameraInfo info = new CameraInfo();
//		Camera.getCameraInfo(cameraId, info);
//		rotation = getWindowManager().getDefaultDisplay().getRotation();
//		int degree = 0;
//		switch (rotation) {
//		case Surface.ROTATION_0:
//			degree = 0;
//			break;
//		case Surface.ROTATION_90:
//			degree = 90;
//			break;
//		case Surface.ROTATION_180:
//			degree = 180;
//			break;
//		case Surface.ROTATION_270:
//			degree = 270;
//			break;
//
//		default:
//			break;
//		}
//
//		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
//			// frontFacing
//			rotation = (info.orientation + degree) % 330;
//			rotation = (360 - rotation) % 360;
//		} else {
//			// Back-facing
//			rotation = (info.orientation - degree + 360) % 360;
//		}
//		c.setDisplayOrientation(rotation);
//		Parameters params = c.getParameters();
//
//		showFlashButton(params);
//
//		List<String> focusModes = params.getSupportedFlashModes();
//		if (focusModes != null) {
//			if (focusModes
//					.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//				params.setFlashMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//			}
//		}
//
//		params.setRotation(rotation);
//	}
//
//	private void showFlashButton(Parameters params) {
//		boolean showFlash = (getPackageManager().hasSystemFeature(
//				PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
//				&& params.getSupportedFlashModes() != null
//				&& params.getSupportedFocusModes().size() > 1;
//
//		flashCameraButton.setVisibility(showFlash ? View.VISIBLE
//				: View.INVISIBLE);
//
//	}
//
//	private void releaseCamera() {
//		try {
//			if (camera != null) {
//				camera.setPreviewCallback(null);
//				camera.setErrorCallback(null);
//				camera.stopPreview();
//				camera.release();
//				camera = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.e("error", e.toString());
//			camera = null;
//		}
//	}
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.flash:
//			flashOnButton();
//			break;
//		case R.id.flipCamera:
//			flipCamera();
//			break;
//		case R.id.captureImage:
//			takeImage();
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	private void takeImage() {
//		camera.takePicture(null, null, new PictureCallback() {
//
//			private File imageFile;
//
//			@Override
//			public void onPictureTaken(byte[] data, Camera camera) {
//				try {
//					// convert byte array into bitmap
//					Bitmap loadedImage = null;
//					Bitmap rotatedBitmap = null;
//					loadedImage = BitmapFactory.decodeByteArray(data, 0,
//							data.length);
//
//					// rotate Image
//					Matrix rotateMatrix = new Matrix();
//					rotateMatrix.postRotate(rotation);
//					rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
//							loadedImage.getWidth(), loadedImage.getHeight(),
//							rotateMatrix, false);
//					String state = Environment.getExternalStorageState();
//					File folder = null;
//					if (state.contains(Environment.MEDIA_MOUNTED)) {
//						folder = new File(Environment
//								.getExternalStorageDirectory() + "/Demo");
//					} else {
//						folder = new File(Environment
//								.getExternalStorageDirectory() + "/Demo");
//					}
//
//					boolean success = true;
//					if (!folder.exists()) {
//						success = folder.mkdirs();
//					}
//					if (success) {
//						java.util.Date date = new java.util.Date();
//						imageFile = new File(folder.getAbsolutePath()
//								+ File.separator
//								+ new Timestamp(date.getTime()).toString()
//								+ "Image.jpg");
//
//						imageFile.createNewFile();
//					} else {
//						Toast.makeText(getBaseContext(), "Image Not saved",
//								Toast.LENGTH_SHORT).show();
//						return;
//					}
//
//					ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//
//					// save image into gallery
//					rotatedBitmap.compress(CompressFormat.JPEG, 100, ostream);
//
//					FileOutputStream fout = new FileOutputStream(imageFile);
//					fout.write(ostream.toByteArray());
//					fout.close();
//					ContentValues values = new ContentValues();
//
//					values.put(Images.Media.DATE_TAKEN,
//							System.currentTimeMillis());
//					values.put(Images.Media.MIME_TYPE, "image/jpeg");
//					values.put(MediaStore.MediaColumns.DATA,
//							imageFile.getAbsolutePath());
//
//					CameraDemoActivity.this.getContentResolver().insert(
//							Images.Media.EXTERNAL_CONTENT_URI, values);
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}
//		});
//	}
//
//	private void flipCamera() {
//		int id = (cameraId == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT
//				: CameraInfo.CAMERA_FACING_BACK);
//		if (!openCamera(id)) {
//			alertCameraDialog();
//		}
//	}
//
//	private void alertCameraDialog() {
//		Builder dialog = createAlert(CameraDemoActivity.this,
//				"Camera info", "error to open camera");
//		dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.cancel();
//
//			}
//		});
//
//		dialog.show();
//	}
//
//	private Builder createAlert(Context context, String title, String message) {
//
//		Builder dialog = new Builder(
//				new ContextThemeWrapper(context,
//						android.R.style.Theme_Holo_Light_Dialog));
//		dialog.setIcon(R.drawable.ic_launcher);
//		if (title != null)
//			dialog.setTitle(title);
//		else
//			dialog.setTitle("Information");
//		dialog.setMessage(message);
//		dialog.setCancelable(false);
//		return dialog;
//
//	}
//
//	private void flashOnButton() {
//		if (camera != null) {
//			try {
//				Parameters param = camera.getParameters();
//				param.setFlashMode(!flashmode ? Parameters.FLASH_MODE_TORCH
//						: Parameters.FLASH_MODE_OFF);
//				camera.setParameters(param);
//				flashmode = !flashmode;
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//
//		}
//	}
//}

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.attendance.office.AppUtil;
import com.attendance.office.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;


public class CameraDemoActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView, imgTakePicture;
    Button btnProcessNext, btnTakePicture;
    TextView txtSampleDesc, txtTakenPicDesc;
    private FaceDetector detector;
    Bitmap editedBitmap;
    int currentIndex = 0;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 200;
    private static final int CAMERA_REQUEST = 101;

    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_BITMAP = "bitmap";
    private RelativeLayout rlMainLayout;
    private LinearLayout llProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerademo_activity);

        //	imageArray = new int[]{R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3};
        detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();

        initViews();
        ActivityCompat.requestPermissions(CameraDemoActivity.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);

    }

    private void initViews() {
        imageView = (ImageView) findViewById(R.id.imageView);
        imgTakePicture = (ImageView) findViewById(R.id.imgTakePic);
        btnProcessNext = (Button) findViewById(R.id.btnProcessNext);
        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        txtSampleDesc = (TextView) findViewById(R.id.txtSampleDescription);
        txtTakenPicDesc = (TextView) findViewById(R.id.txtTakePicture);
        rlMainLayout = (RelativeLayout) findViewById(R.id.rlMainLayout);
        llProgress = (LinearLayout)findViewById(R.id.progressBar);
        //	processImage(imageArray[currentIndex]);
        currentIndex++;

        btnProcessNext.setOnClickListener(this);
        btnTakePicture.setOnClickListener(this);
        imgTakePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnProcessNext:
//				imageView.setImageResource(imageArray[currentIndex]);
//				//processImage(imageArray[currentIndex]);
//				if (currentIndex == imageArray.length - 1)
//					currentIndex = 0;
//				else
//					currentIndex++;

                break;

            case R.id.btnTakePicture:

                break;

            case R.id.imgTakePic:
                ActivityCompat.requestPermissions(CameraDemoActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    finish();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                processCameraPicture();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to load Image", Toast.LENGTH_SHORT).show();
            }
        }else{
            finishPreviousActivity(101);
        }
    }

    private void finishPreviousActivity(int value) {
        Intent intent = new Intent();
        setResult(value, intent);
        finish();
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putParcelable(SAVED_INSTANCE_BITMAP, editedBitmap);
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
        }
        super.onSaveInstanceState(outState);
    }


    private void processImage(int image) {

        Bitmap bitmap = decodeBitmapImage(image);
        if (detector.isOperational() && bitmap != null) {
            editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), bitmap.getConfig());
            float scale = getResources().getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.GREEN);
            paint.setTextSize((int) (16 * scale));
            paint.setShadowLayer(1.5f, 0f, 1f, Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10f);
            Canvas canvas = new Canvas(editedBitmap);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
            SparseArray<Face> faces = detector.detect(frame);
            txtSampleDesc.setText(null);

            for (int index = 0; index < faces.size(); ++index) {
                Face face = faces.valueAt(index);
                canvas.drawRect(
                        face.getPosition().x,
                        face.getPosition().y,
                        face.getPosition().x + face.getWidth(),
                        face.getPosition().y + face.getHeight(), paint);


                canvas.drawText("Face " + (index + 1), face.getPosition().x + face.getWidth(), face.getPosition().y + face.getHeight(), paint);

                txtSampleDesc.setText(txtSampleDesc.getText() + "FACE " + (index + 1) + "\n");
                txtSampleDesc.setText(txtSampleDesc.getText() + "Smile probability:" + " " + face.getIsSmilingProbability() + "\n");
                txtSampleDesc.setText(txtSampleDesc.getText() + "Left Eye Is Open Probability: " + " " + face.getIsLeftEyeOpenProbability() + "\n");
                txtSampleDesc.setText(txtSampleDesc.getText() + "Right Eye Is Open Probability: " + " " + face.getIsRightEyeOpenProbability() + "\n\n");

                for (Landmark landmark : face.getLandmarks()) {
                    int cx = (int) (landmark.getPosition().x);
                    int cy = (int) (landmark.getPosition().y);
                    canvas.drawCircle(cx, cy, 8, paint);
                }


            }

            if (faces.size() == 0) {
                txtSampleDesc.setText("Scan Failed: Found nothing to scan");
            } else {
                imageView.setImageBitmap(editedBitmap);
                txtSampleDesc.setText(txtSampleDesc.getText() + "No of Faces Detected: " + " " + String.valueOf(faces.size()));
            }
        } else {
            txtSampleDesc.setText("Could not set up the detector!");
        }
    }

    private Bitmap decodeBitmapImage(int image) {
        int targetW = 300;
        int targetH = 300;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), image,
                bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeResource(getResources(), image,
                bmOptions);
    }

    private void processCameraPicture() throws Exception {
        try {
            AppUtil.INSTANCE.showProgress(llProgress);
            Bitmap bitmap = decodeBitmapUri(this, imageUri);
            if (detector.isOperational() && bitmap != null) {
                editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                        .getHeight(), bitmap.getConfig());
                float scale = getResources().getDisplayMetrics().density;
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.BLUE);
                paint.setTextSize((int) (16 * scale));
                paint.setShadowLayer(1.5f, 0f, 1f, Color.BLUE);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10f);
                Canvas canvas = new Canvas(editedBitmap);
                canvas.drawBitmap(bitmap, 0, 0, paint);
                Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
                SparseArray<Face> faces = detector.detect(frame);
                txtTakenPicDesc.setText(null);

                for (int index = 0; index < faces.size(); ++index) {
                    Face face = faces.valueAt(index);
//                canvas.drawRect(
//                        face.getPosition().x,
//                        face.getPosition().y,
//                        face.getPosition().x + face.getWidth(),
//                        face.getPosition().y + face.getHeight(), paint);


                    //  canvas.drawText("Face " + (index + 1), face.getPosition().x + face.getWidth(), face.getPosition().y + face.getHeight(), paint);

                    txtTakenPicDesc.setText("FACE " + (index + 1) + "\n");
                    txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "Smile probability:" + " " + face.getIsSmilingProbability() + "\n");
                    txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "Left Eye Is Open Probability: " + " " + face.getIsLeftEyeOpenProbability() + "\n");
                    txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "Right Eye Is Open Probability: " + " " + face.getIsRightEyeOpenProbability() + "\n\n");

                    for (Landmark landmark : face.getLandmarks()) {
                        int cx = (int) (landmark.getPosition().x);
                        int cy = (int) (landmark.getPosition().y);
                        // canvas.drawCircle(cx, cy, 8, paint);
                    }


                }

                if (faces.size() == 0) {
                    AppUtil.INSTANCE.cancelProgress(llProgress);
                    Snackbar.make(rlMainLayout, "No face detected", Snackbar.LENGTH_SHORT).show();
                    finishPreviousActivity(101);
                } else {
                    imgTakePicture.setImageBitmap(editedBitmap);
                    txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "No of Faces Detected: " + " " + String.valueOf(faces.size()));
                    if (editedBitmap != null) {
                        AppUtil.INSTANCE.cancelProgress(llProgress);
                        AppUtil.INSTANCE.saveBitmapInPreferences(editedBitmap);
                        finishPreviousActivity(100);
                    } else {
                        Snackbar.make(rlMainLayout, "No face detected", Snackbar.LENGTH_SHORT).show();
                        finishPreviousActivity(101);
                    }
                }
            } else {
                Snackbar.make(rlMainLayout, "No face detected", Snackbar.LENGTH_SHORT).show();
                finishPreviousActivity(101);
            }
        }catch (Exception e){
            e.printStackTrace();
            finishPreviousActivity(101);
        }
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 300;
        int targetH = 300;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.release();
    }
}

