package com.android.settings.widget;


import android.app.PendingIntent;
import com.android.settings.R;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

public class OrientationWidgetProvider  extends AppWidgetProvider{
    
    // TAG
    public static final String TAG = "Evervolv_OrientationWidget";
    private boolean DBG = false;
    // Intent Actions
    public static String ORIENTATION_CHANGED = "com.evervolv.widget.ORIENTATION_CLICKED";
    
    @Override
    public void onEnabled(Context context){
		PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.OrientationWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDisabled(Context context) {
        if (DBG) Log.d(TAG,"Received request to remove last widget");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.OrientationWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context,appWidgetIds);
        if (DBG) Log.d(TAG,"Received request to remove a widget");
    }
    
    @Override
    public void onUpdate(Context context,
			 AppWidgetManager appWidgetManager,
			 int[] appWidgetIds){
    	if (DBG) Log.d(TAG, "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    	updateWidget(context, appWidgetManager, appWidgetIds);
    }
    
    /**
	* this method will receive all Intents that it registers for in
	* the android manifest file.
	*/
    @Override
    public void onReceive(Context context, Intent intent){
    	if (DBG) Log.d(TAG, "onReceive - " + intent.toString());
    	super.onReceive(context, intent);
    	if (ORIENTATION_CHANGED.equals(intent.getAction())){
    		toggleState(context);
    	}
    }
    
	/**
	* this method is called when the widget is added to the home
	* screen, and so it contains the initial setup of the widget.
	*/
    public void updateWidget(Context context,
    			 AppWidgetManager appWidgetManager,
    			 int[] appWidgetIds){
    	for (int i=0;i<appWidgetIds.length;++i){
		
	    	int appWidgetId = appWidgetIds[i];
			
	    	//on or off
			int orientationState = getOrientationState(context);
    		updateWidgetView(context,orientationState);
		}
    }
    
	/**
	* Method to update the widgets GUI
	*/
	private void updateWidgetView(Context context,int state){
	
	    Intent intent = new Intent(context, OrientationWidgetProvider.class);
		intent.setAction(ORIENTATION_CHANGED);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
	    RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.power_widget);
	    views.setOnClickPendingIntent(R.id.power_panel,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_press,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_item,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_trigger,pendingIntent);
		views.setImageViewResource(R.id.power_item,R.drawable.widget_orientation_icon_on);
		views.setTextViewText(R.id.power_label,context.getString(R.string.orientation_gadget_caption));
		// We need to update the Widget GUI
		if (state == StateTracker.STATE_DISABLED){
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_orientation_icon_off);
		} else if (state == StateTracker.STATE_ENABLED) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_allon);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_orientation_icon_on);
		}
		
		ComponentName cn = new ComponentName(context, OrientationWidgetProvider.class);  
		AppWidgetManager.getInstance(context).updateAppWidget(cn, views); 
	}
    
    public static int getOrientationState(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0);
    }
    
    public void toggleState(Context context) {
        if (getOrientationState(context) == 0) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, 1);
        } else {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, 0);
        }
		int orientationState = getOrientationState(context);
		updateWidgetView(context,orientationState);
    }
}
