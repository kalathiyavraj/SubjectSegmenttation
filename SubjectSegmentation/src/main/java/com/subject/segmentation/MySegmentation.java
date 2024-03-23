package com.subject.segmentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentationResult;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MySegmentation {
    public interface SegmentationCallback {
        void onSegmentationComplete(Bitmap bitmap, int timeElapsed, String error);
    }
    public static InputImage image;
    public static ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    public static Context Main_segment_context;
    public static Uri Main_segment_uri;



    private static SegmentationCallback segmentationCallback;

    public static void SubjectSegment(Context context, Uri uri, SegmentationCallback segCallback) {
        Main_segment_context = context;
        Main_segment_uri = uri;
        segmentationCallback = segCallback;
        NextCameraMode();
    }

    public static void NextCameraMode() {
        long startTime = System.currentTimeMillis();
        cameraProviderFuture = ProcessCameraProvider.getInstance(Main_segment_context);
        new YourAnalyzer(startTime);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, startTime);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(Main_segment_context));
    }

    public static void bindPreview(@NonNull ProcessCameraProvider cameraProvider, long startTime) {

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280,720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(Main_segment_context), new YourAnalyzer(startTime));

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)Main_segment_context, cameraSelector, imageAnalysis);
    }

    public static class YourAnalyzer implements ImageAnalysis.Analyzer {
        private long startTime;

        public YourAnalyzer(long startTime) {
            this.startTime = startTime;
        }

        @Override
        @ExperimentalGetImage
        public void analyze(ImageProxy imageProxy) {
            Image mediaImage = imageProxy.getImage();

            if (mediaImage != null) {
                long endTime = System.currentTimeMillis();
                int timeElapsed = (int) (endTime - startTime);

                try {
                    image = InputImage.fromFilePath(Main_segment_context, Main_segment_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (segmentationCallback != null) {
                        segmentationCallback.onSegmentationComplete(null, timeElapsed, "Error loading image");
                    }
                    return;
                }
                SubjectSegmenterOptions options = new SubjectSegmenterOptions.Builder()
                        .enableForegroundBitmap()
                        .build();
                SubjectSegmenter segmenter = SubjectSegmentation.getClient(options);
                segmenter.process(image).addOnSuccessListener(new OnSuccessListener<SubjectSegmentationResult>() {
                    @Override
                    public void onSuccess(SubjectSegmentationResult subjectSegmentationResult) {
                        Bitmap foregroundBitmap = subjectSegmentationResult.getForegroundBitmap();
                        if (segmentationCallback != null) {
                            segmentationCallback.onSegmentationComplete(foregroundBitmap, timeElapsed, null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (segmentationCallback != null) {
                            segmentationCallback.onSegmentationComplete(null, timeElapsed, e.getMessage());
                        }
                    }
                });
            }
        }
    }
}
