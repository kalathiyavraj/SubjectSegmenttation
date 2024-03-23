# Subject segmentation in Android 
Subject segmentation with ML Kit for Android .
![3324991](https://i.postimg.cc/5tPqZ65S/c16c62332915537faea744ce172472fe.jpg)

> easily separate subjects from the background in a picture

1. Add the JitPack repository to your build file

```gradle
     allprojects {
		repositories {
			
			maven { url 'https://jitpack.io' }
                        maven { url 'https://androidx.dev/snapshots/builds/6787662/artifacts/repository/' }
		}
	}
 ```
2. Add the dependency

```gradle
dependencies {

	implementation 'com.github.kalathiyavraj:SubjectSegmenttation:1.0.0'


            }
  ```
 > * Add Permission
  ```gradle
  
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CAMERA" />
 ```
 > * How is it Work
  ```gradle
  
  MySegmentation.SubjectSegment(MainActivity.this, Imageuri, new MySegmentation.SegmentationCallback() {
            @Override
            public void onSegmentationComplete(Bitmap bitmap, int timeElapsed, String error) {
                if (bitmap != null) {
                    // Handle the segmented bitmap

                    ImageView imageView = findViewById(R.id.output_image);
                    imageView.setImageBitmap(bitmap);
                   
                } else {
                    // Handle the error

                }
                // Handle the time elapsed
                
            }
        });
 ```
