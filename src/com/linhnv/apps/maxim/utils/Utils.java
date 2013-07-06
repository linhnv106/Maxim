/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.linhnv.apps.maxim.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * Class containing some static utility methods.
 */
public class Utils {
	private Utils() {
	};

	@TargetApi(11)
	public static void enableStrictMode() {
		if (Utils.hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog();
			//
			// if (Utils.hasHoneycomb()) {
			// threadPolicyBuilder.penaltyFlashScreen();
			// vmPolicyBuilder
			// .setClassInstanceLimit(ImageGridActivity.class, 1)
			// .setClassInstanceLimit(ImageDetailActivity.class, 1);
			// }
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean isSdPresent() {

		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

	}

	public static boolean isValidEmail(String target) {
		try {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		} catch (NullPointerException exception) {
			return false;
		}
	}

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	// Check network available
	public static boolean checkNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable() && ni.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public static void clearCookies(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	/**
	 * 
	 * @param path
	 *            of picture to encode
	 * @return base64 String encode
	 */
	public static String encodeImage(String path) {
		String encodedImage = null;
		FileInputStream in = null;
		try {
			File f = new File(path);
			in = new FileInputStream(f);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer)) != -1) {
				baos.write(buffer, 0, count);
			}
			byte[] content = baos.toByteArray();
			encodedImage = Base64.encodeToString(content, Base64.DEFAULT);
			return encodedImage;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static String encodeImage(Bitmap bmp) {
		if (bmp == null)
			return null;
		String encodedImage = null;
		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the
																// bitmap object
			byte[] content = baos.toByteArray();
			encodedImage = Base64.encodeToString(content, Base64.DEFAULT);
			return encodedImage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static int computeSampleSize(InputStream stream, int maxW, int maxH) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(stream, null, options);
		double w = options.outWidth;
		double h = options.outHeight;
		int sampleSize = (int) Math.ceil(Math.max(w / maxW, h / maxH));
		return sampleSize;
	}

	public static Bitmap resizeBitmapToSmaller(Bitmap input, int destMaxSize) {
		int srcWidth = input.getWidth();
		int srcHeight = input.getHeight();
		int destWidth = 0;
		int destHeight = 0;
		float p;
		boolean needResize = false;
		if (srcWidth > srcHeight) {
			if (srcWidth > destMaxSize) {
				p = (float) destMaxSize / (float) srcWidth;
				destHeight = (int) (srcHeight * p);
				destWidth = destMaxSize;
				needResize = true;
			}
		} else {
			if (srcHeight > destMaxSize) {
				p = (float) destMaxSize / (float) srcHeight;
				destWidth = (int) (srcWidth * p);
				destHeight = destMaxSize;
				needResize = true;
			}
		}

		if (needResize) {
			try {
				Bitmap output = Bitmap.createScaledBitmap(input, destWidth, destHeight, true);
				return output;
			} catch (OutOfMemoryError ex) {
				return null;
			}
		} else {
			return input;
		}

	}

	public static Bitmap loadFromUrl(Context context, String url, int maxSize) {
		File f = new File(url);
		if (!f.exists()) {
			return null;
		}

		final BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap = null;
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(context.getContentResolver().openInputStream(
					Uri.fromFile(new File(url))), 16384);

			options.inSampleSize = computeSampleSize(stream, maxSize, maxSize);
			stream = null;
			stream = new BufferedInputStream(context.getContentResolver().openInputStream(
					Uri.fromFile(new File(url))), 16384);

			options.inDither = false;
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;

			bitmap = BitmapFactory.decodeStream(stream, null, options);
			if (bitmap != null) {
				bitmap = resizeBitmapToSmaller(bitmap, maxSize);
			} else {
				File f1 = new File(url);
				if (f1.exists()) {
					f1.delete();
				}
			}
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
			bitmap = null;
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	public static Bitmap getBitmapFromPath(Context context, String imagePath) {

		if (imagePath == null) {
			return null;
		}
		// /////
		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
			Bitmap imageBitmap = null;
			try {
				imageBitmap = loadFromUrl(context, imageFile.getAbsolutePath(), 640);

			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				imageBitmap = null;
			} catch (Exception e) {
				e.printStackTrace();
				imageBitmap = null;
			}

			if (imageBitmap != null) {
				// Rotate imageBitmap based on the orientation of its taken
				// picture
				int rotationDegrees = 0;
				try {
					rotationDegrees = getPictureRotationDegrees(imagePath);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				try {
					Bitmap rotatedImageBitmap = rotateBitmap(imageBitmap, rotationDegrees);
					// imageBitmap.recycle();
					imageBitmap = rotatedImageBitmap;
					return imageBitmap;
				} catch (OutOfMemoryError oomerr) {

				}
			}

		} else {
			return null;
		}

		return null;
	}

	public static int getPictureRotationDegrees(String picPath) throws IOException {
		int rotationDegree = 0;
		ExifInterface exif = new ExifInterface(picPath);
		int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_NORMAL);
		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotationDegree = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotationDegree = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotationDegree = 270;
			break;
		}
		return rotationDegree;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
		Matrix matrix = new Matrix();
		matrix.postRotate(orientation);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
				true);
	}

	public static String encodeExistImage(Context context, String path) {
		String encodedImage = null;
		InputStream in = null;
		try {
			File f = new File(path);
			in = context.getContentResolver().openInputStream(Uri.parse(path));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer)) != -1) {
				baos.write(buffer, 0, count);
			}
			byte[] content = baos.toByteArray();
			encodedImage = Base64.encodeToString(content, Base64.DEFAULT);
			return encodedImage;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
