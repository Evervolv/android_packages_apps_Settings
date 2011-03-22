package com.android.settings.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.settings.R;
import com.android.wimax.WimaxConstants;
import android.os.AsyncTask;
import com.android.wimax.WimaxSettingsHelper;

/**
 * Author: milesje,preludedrew
 * This class is used to create a widget to toggle the Wimax Settings
 */

public class WimaxWidgetProvider extends AppWidgetProvider {
    // TAG
    public static final String TAG = "Evervolv_WimaxWidget";
    //RemoteViews views = new RemoteViews("com.android.settings.widget.WimaxWidgetProvider",
	//					R.layout.power_widget);
    // Intent Actions
    public static String WIMAX_ENABLED_CHANGED = "com.htc.net.wimax.WIMAX_ENABLED_CHANGED";
    public static String WIMAX_CHANGED = "com.evervolv.widget.WIMAX_CLICKED";
    // State Tracker
    private static final StateTracker sWimaxState = new WimaxStateTracker();

    @Override
    public void onEnabled(Context context){
		Log.d(TAG,"WimaxWidgetProvider::onEnabled");
		PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.WimaxWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDisabled(Context context) {
        Log.d(TAG,"Received request to remove last widget");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.WimaxWidgetProvider"),
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
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG,"WimaxWidgetProvider::onUpdate appWidgetIds"+appWidgetIds);
    	updateWidget(context, appWidgetManager, appWidgetIds);
    	//updateWidget(context);
    }
    
    public void updateWidget(Context context){
        Log.d(TAG,"WimaxWidgetProvider::updateWidget(context)");
        ComponentName thisWidget = new ComponentName(context,WimaxWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        updateWidget(context,appWidgetManager,appWidgetIds);
    }
    
    /**
	* this method will receive all Intents that it register fors in
	* the android manifest file.
	*/
    @Override
    public void onReceive(Context context, Intent intent){
    	super.onReceive(context, intent);
        Log.d(TAG,"WimaxWidgetProvider::onReceive\n"+intent.getAction());
        
    	if (WIMAX_CHANGED.equals(intent.getAction())){
	    	int result = sWimaxState.getActualState(context);
	    	if (result == StateTracker.STATE_DISABLED){
    	    	sWimaxState.requestStateChange(context,true);
    	    } else if (result == StateTracker.STATE_ENABLED){
    	    	sWimaxState.requestStateChange(context,false);
    	    } else {
    	        // we must be between on and off so we do nothing
    	    }
    	    
        } 
        if (WIMAX_ENABLED_CHANGED.equals(intent.getAction())){
            int wimaxState = intent.getIntExtra(WimaxConstants.CURRENT_WIMAX_ENABLED_STATE,
                                                 WimaxConstants.WIMAX_ENABLED_STATE_UNKNOWN);
            updateWidgetView(context,WimaxStateTracker.wimaxStateToFiveState(wimaxState));    
            sWimaxState.onActualStateChange(context,intent);                                             
        }
    }
	
	/**
	* Method to update the widgets GUI
	*/
	private void updateWidgetView(Context context,int state/*,int appWidgetId*/){
	    
        Log.d(TAG,"WimaxWidgetProvider::updateWidgetView");
        Intent intent = new Intent(context, WimaxWidgetProvider.class);
		intent.setAction(WIMAX_CHANGED);
	    //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
	    RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.power_widget);
		// set the image view icon
		views.setImageViewResource(R.id.power_item,R.drawable.widget_wimax_icon);
		// set the caption
		String caption = context.getString(R.string.wimax_gadget_caption);
		views.setTextViewText(R.id.power_label,caption);
        // set pending intents		
	    views.setOnClickPendingIntent(R.id.power_panel,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_press,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_item,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_trigger,pendingIntent);
		// set the trigger according to the state of the wimax radio
		if (state == StateTracker.STATE_DISABLED){
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_wimax_icon_03);
		} else if (state == StateTracker.STATE_ENABLED) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_on);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_wimax_icon);
		} else if (state == StateTracker.STATE_TURNING_ON) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_tween);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_wimax_icon_02);
		} else if (state == StateTracker.STATE_TURNING_OFF) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_tween);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_wimax_icon_02);
		} else if (state == StateTracker.STATE_UNKNOWN) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_wimax_icon_03);
		}
		// update the widget
    	ComponentName cn = new ComponentName(context, WimaxWidgetProvider.class);  
        AppWidgetManager.getInstance(context).updateAppWidget(cn, views);
	}

	/**
	* this method is called when the widget is added to the home
	* screen, and so it contains the initial setup of the widget.
	*/
    public void updateWidget(Context context,
    			 AppWidgetManager appWidgetManager,
    			 int[] appWidgetIds){

    	for (int i=0;i<appWidgetIds.length;i++){
		
	    	int appWidgetId = appWidgetIds[i];
	    	Log.d(TAG,"appWidgetId: "+appWidgetId);
			int wimaxState = sWimaxState.getActualState(context);
    		updateWidgetView(context,wimaxState);
		}
		
    }
   
    
    
    /**
     * Subclass of StateTracker to get/set WiMAX state.
     */
    private static final class WimaxStateTracker extends StateTracker {

        /**
        * Uses the WimaxSettingsHelper to get the actual state
        */
        @Override
        public int getActualState(Context context) {
            final WimaxSettingsHelper helper = new WimaxSettingsHelper(context);
            if (helper.isWimaxSupported()) {
                return wimaxStateToFiveState(helper.getWimaxState());
            }
            return StateTracker.STATE_UNKNOWN;
        }
        
        /**
        * Request the change of the wimax between on/off
        */
        @Override
        protected void requestStateChange(Context context, final boolean desiredState) {
            final WimaxSettingsHelper helper = new WimaxSettingsHelper(context);
            if (!helper.isWimaxSupported()) {
                Log.e(WimaxWidgetProvider.TAG, "WiMAX is not supported");
                return;
            }

            // Actually request the wimax change and persistent
            // settings write off the UI thread, as it can take a
            // user-noticeable amount of time, especially if there's
            // disk contention.
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... args) {
                    helper.setWimaxEnabled(desiredState);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onActualStateChange(Context context, Intent intent) {
            if (!WimaxConstants.WIMAX_ENABLED_CHANGED_ACTION.equals(intent.getAction())) {
                return;
            }
            int wimaxState = intent.getIntExtra(WimaxConstants.CURRENT_WIMAX_ENABLED_STATE, 
                                                WimaxConstants.WIMAX_ENABLED_STATE_UNKNOWN);
            int widgetState = wimaxStateToFiveState(wimaxState);
            setCurrentState(context, widgetState);
        }

        /**
         * Converts WimaxController's state values into our
         * WiMAX-common state values.
         */
        private static int wimaxStateToFiveState(int wimaxState) {
            switch (wimaxState) {
                case WimaxConstants.WIMAX_ENABLED_STATE_DISABLED:
                    return StateTracker.STATE_DISABLED;
                case WimaxConstants.WIMAX_ENABLED_STATE_ENABLED:
                    return StateTracker.STATE_ENABLED;
                case WimaxConstants.WIMAX_ENABLED_STATE_ENABLING:
                    return StateTracker.STATE_TURNING_ON;
                case WimaxConstants.WIMAX_ENABLED_STATE_DISABLING:
                    return StateTracker.STATE_TURNING_OFF;
                default:
                    return StateTracker.STATE_UNKNOWN;
            }
        }
    }
}
