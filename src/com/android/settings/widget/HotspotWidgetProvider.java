package com.android.settings.widget;

import com.android.settings.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class HotspotWidgetProvider extends AppWidgetProvider {
    // TAG
    public static final String TAG = "Evervolv_WifiApWidget";
    // Intent Actions
    public static String WIFIAP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static String WIFIAP_CHANGED = "com.evervolv.widget.WIFIAP_CLICKED";
    
    private static final StateTracker sWifiApState = new WifiApStateTracker();
    
    @Override
    public void onEnabled(Context context){
		PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.TetherWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDisabled(Context context) {
        Log.d(TAG,"Received request to remove last widget");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.TetherWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context,appWidgetIds);
        Log.d(TAG,"Received request to remove a widget");
    }

    @Override
    public void onUpdate(Context context,
			 AppWidgetManager appWidgetManager,
			 int[] appWidgetIds){
    	Log.d(TAG, "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    	updateWidget(context, appWidgetManager, appWidgetIds);
    }
    
    /**
	* this method will receive all Intents that it register fors in
	* the android manifest file.
	*/
    @Override
    public void onReceive(Context context, Intent intent){
    	Log.d(TAG, "onReceive - " + intent.toString());
    	super.onReceive(context, intent);
    	if (WIFIAP_CHANGED.equals(intent.getAction())){
	    	int result = sWifiApState.getActualState(context);
	    	if (result == StateTracker.STATE_DISABLED){
    	    	sWifiApState.requestStateChange(context,true);
    	    } else if (result == StateTracker.STATE_ENABLED){
    	    	sWifiApState.requestStateChange(context,false);
    	    } else {
    	        // we must be between on and off so we do nothing
    	    }
    	}
        if (WIFIAP_STATE_CHANGED.equals(intent.getAction())){
            int wifiApState = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE, -1);
            updateWidgetView(context,WifiApStateTracker.wifiApStateToFiveState(wifiApState));    
            sWifiApState.onActualStateChange(context,intent);                                             
        }
    }
    
	/**
	* Method to update the widgets GUI
	*/
	private void updateWidgetView(Context context,int state){
	
	    Intent intent = new Intent(context, HotspotWidgetProvider.class);
		intent.setAction(WIFIAP_CHANGED);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
	    RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.power_widget);
	    views.setOnClickPendingIntent(R.id.power_panel,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_press,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_item,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_trigger,pendingIntent);
		views.setImageViewResource(R.id.power_item,R.drawable.widget_hotspot_icon);
		views.setTextViewText(R.id.power_label,context.getString(R.string.hotspot_gadget_caption));
		// We need to update the Widget GUI
		if (state == StateTracker.STATE_DISABLED){
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_hotspot_icon_03);
		} else if (state == StateTracker.STATE_ENABLED) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_on);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_hotspot_icon);
		} else if (state == StateTracker.STATE_TURNING_ON) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_tween);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_hotspot_icon_02);
		} else if (state == StateTracker.STATE_TURNING_OFF) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_tween);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_hotspot_icon_02);
		} else if (state == StateTracker.STATE_UNKNOWN) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_hotspot_icon_03);
		}
		
		ComponentName cn = new ComponentName(context, HotspotWidgetProvider.class);  
		AppWidgetManager.getInstance(context).updateAppWidget(cn, views); 
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
			
			int wifiApState = sWifiApState.getActualState(context);
    		updateWidgetView(context,wifiApState);
		}
    }
    
    /**
     * Subclass of StateTracker to get/set Wifi AP state.
     */
    private static final class WifiApStateTracker extends StateTracker {
        @Override
        public int getActualState(Context context) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                return wifiApStateToFiveState(wifiManager.getWifiApState());
            }
            return StateTracker.STATE_UNKNOWN;
        }

        @Override
        protected void requestStateChange(Context context, final boolean desiredState) {

            final WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                Log.d(TAG, "No wifiManager.");
                return;
            }

            // Actually request the Wi-Fi AP change and persistent
            // settings write off the UI thread, as it can take a
            // user-noticeable amount of time, especially if there's
            // disk contention.
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... args) {
                    /**
                     * Disable Wifi if enabling tethering
                     */
                    int wifiState = wifiManager.getWifiState();
                    if (desiredState
                            && ((wifiState == WifiManager.WIFI_STATE_ENABLING) || (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
                        wifiManager.setWifiEnabled(false);
                    }

                    wifiManager.setWifiApEnabled(null, desiredState);
                    return null;
                }
            }.execute();
        }

		@Override
		public void onActualStateChange(Context context, Intent intent) {

            if (!WifiManager.WIFI_AP_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                return;
            }
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE, -1);
            setCurrentState(context, wifiApStateToFiveState(wifiState));
		}
        /**
         * Converts WifiManager's state values into our
         * Wifi/WifiAP/Bluetooth-common state values.
         */
        private static int wifiApStateToFiveState(int wifiState) {
            switch (wifiState) {
                case WifiManager.WIFI_AP_STATE_DISABLED:
                    return StateTracker.STATE_DISABLED;
                case WifiManager.WIFI_AP_STATE_ENABLED:
                    return StateTracker.STATE_ENABLED;
                case WifiManager.WIFI_AP_STATE_DISABLING:
                    return StateTracker.STATE_TURNING_OFF;
                case WifiManager.WIFI_AP_STATE_ENABLING:
                    return StateTracker.STATE_TURNING_ON;
                default:
                    return StateTracker.STATE_UNKNOWN;
            }
        }


    }
}
