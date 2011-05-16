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

public class AirplaneModeWidgetProvider extends AppWidgetProvider{
    
    // TAG
    public static final String TAG = "Evervolv_AirplaneModeWidget";
    private boolean DBG = false;
    // Intent Actions
    public static String AIRPLANEMODE_CHANGED = "com.evervolv.widget.AIRPLANEMODE_CLICKED";
    
    @Override
    public void onEnabled(Context context){
		PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.AirplaneModeWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDisabled(Context context) {
        if (DBG) Log.d(TAG,"Received request to remove last widget");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.AirplaneModeWidgetProvider"),
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
    	
    	if (AIRPLANEMODE_CHANGED.equals(intent.getAction())){
    		toggleState(context);
    	}
    	if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
			int airplanemodeState = getAirplaneModeState(context) ? 1 : 0;
    		updateWidgetView(context,airplanemodeState);
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
			int airplanemodeState = getAirplaneModeState(context) ? 1 : 0;
    		updateWidgetView(context,airplanemodeState);
		}
    }
    
	/**
	* Method to update the widgets GUI
	*/
	private void updateWidgetView(Context context,int state){
	
	    Intent intent = new Intent(context, AirplaneModeWidgetProvider.class);
		intent.setAction(AIRPLANEMODE_CHANGED);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
	    RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.power_widget);
	    views.setOnClickPendingIntent(R.id.power_panel,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_press,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_item,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_trigger,pendingIntent);
		views.setImageViewResource(R.id.power_item,R.drawable.widget_airplanemode_icon_on);
		views.setTextViewText(R.id.power_label,context.getString(R.string.airplanemode_gadget_caption));
		// We need to update the Widget GUI
		if (state == StateTracker.STATE_DISABLED){
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_airplanemode_icon_off);
		} else if (state == StateTracker.STATE_ENABLED) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_allon);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_airplanemode_icon_on);
		}
		
		ComponentName cn = new ComponentName(context, AirplaneModeWidgetProvider.class);  
		AppWidgetManager.getInstance(context).updateAppWidget(cn, views); 
	}

    /**
     * Gets the state of Airplane.
     *
     * @param context
     * @return true if enabled.
     */
    private static boolean getAirplaneModeState(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 1;
    }

    /**
     * Toggles the state of Airplane
     *
     * @param context
     */
    public void toggleState(Context context) {
        boolean state = getAirplaneModeState(context);
        Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
                state ? 0 : 1);
        // notify change
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", state);
        context.sendBroadcast(intent);
    }
    
}
