package com.android.settings.widget;

import android.app.PendingIntent;
import com.android.settings.R;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;

public class MobileDataWidgetProvider extends AppWidgetProvider {
    // TAG
    public static final String TAG = "Evervolv_MobileDataWidget";
    private boolean DBG = true;
    // Intent Actions
    public static String MOBILE_DATA_STATE_CHANGED = "com.android.internal.telephony.MOBILE_DATA_CHANGED";
    public static String MOBILE_DATA_CHANGED = "com.evervolv.widget.MOBILE_DATA_CLICKED";
    
    @Override
    public void onEnabled(Context context){
		PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.MobileDataWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDisabled(Context context) {
    	if (DBG) Log.d(TAG,"Received request to remove last widget");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.MobileDataWidgetProvider"),
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
	* this method will receive all Intents that it register fors in
	* the android manifest file.
	*/
    @Override
    public void onReceive(Context context, Intent intent){
    	if (DBG) Log.d(TAG, "onReceive - " + intent.toString());
    	super.onReceive(context, intent);
    	if (MOBILE_DATA_CHANGED.equals(intent.getAction())){
    		toggleState(context);
    		
        	int dataState = getDataState(context) ? StateTracker.STATE_ENABLED : StateTracker.STATE_DISABLED;
            updateWidgetView(context,dataState);  
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
			
			int dataState = getDataState(context) ? StateTracker.STATE_ENABLED : StateTracker.STATE_DISABLED;
    		updateWidgetView(context,dataState);
		}
    }
    
	/**
	* Method to update the widgets GUI
	*/
	private void updateWidgetView(Context context,int state){
	
	    Intent intent = new Intent(context, MobileDataWidgetProvider.class);
		intent.setAction(MOBILE_DATA_CHANGED);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
	    RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.power_widget);
	    views.setOnClickPendingIntent(R.id.power_panel,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_press,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_item,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_trigger,pendingIntent);
		views.setTextViewText(R.id.power_label,context.getString(R.string.mobile_data_gadget_caption));
		// We need to update the Widget GUI
		if (state == StateTracker.STATE_DISABLED){
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_mobile_data_icon_off);
		} else if (state == StateTracker.STATE_ENABLED) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_on);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_mobile_data_icon_on);
		}
		
		ComponentName cn = new ComponentName(context, MobileDataWidgetProvider.class);  
		AppWidgetManager.getInstance(context).updateAppWidget(cn, views); 
	}
    
    /**
     * Gets the state of data
     *
     * @return true if enabled.
     */
    private static boolean getDataState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            /* Make sure the state change propagates */
            Thread.sleep(100);
        } catch (java.lang.InterruptedException ie) {
        }
        return cm.getMobileDataEnabled();
    }
 
    /**
     * Toggles the state of data.
     */
    public void toggleState(Context context) {
        boolean enabled = getDataState(context);

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (enabled) {
            cm.setMobileDataEnabled(false);
        } else {
        	cm.setMobileDataEnabled(true);

        }
        
    }
    
}
