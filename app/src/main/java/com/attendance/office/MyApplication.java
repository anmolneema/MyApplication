package com.attendance.office;




import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import java.io.File;

//import com.crashlytics.android.Crashlytics;

//import io.fabric.sdk.android.Fabric;


public class MyApplication extends Application {
	private static final String TAG = MyApplication.class.getSimpleName();
	Context context;

	public MyApplication() { // for dex crash handling
	}


	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();
		try{
			StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
			StrictMode.setVmPolicy(builder.build());
		}catch (Exception e){
			e.printStackTrace();
		}
	}


}
