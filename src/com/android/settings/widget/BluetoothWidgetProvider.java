package com.android.settings.widget;

import com.android.settings.R;
import com.android.settings.bluetooth.LocalBluetoothManager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class BluetoothWidgetProvider extends AppWidgetProvider  {
    // TAG
    public static final String TAG = "Evervolv_BTWidget";
    // Intent Actions
    public static String BLUETOOTH_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
    public static String BLUETOOTH_CHANGED = "com.evervolv.widget.BLUETOOTH_CLICKED";
    
    private static final StateTracker sBluetoothState = new BluetoothStateTracker();
    private static LocalBluetoothManager sLocalBluetoothManager = null;
    
    @Override
    public void onEnabled(Context context){
		PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.BluetoothWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDisabled(Context context) {
        Log.d(TAG,"Received request to remove last widget");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.BluetoothWidgetProvider"),
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
    	if (BLUETOOTH_CHANGED.equals(intent.getAction())){
	    	int result = sBluetoothState.getActualState(context);
	    	if (result == StateTracker.STATE_DISABLED){
    	    	sBluetoothState.requestStateChange(context,true);
    	    } else if (result == StateTracker.STATE_ENABLED){
    	    	sBluetoothState.requestStateChange(context,false);
    	    } else {
    	        // we must be between on and off so we do nothing
    	    }
    	}
        if (BLUETOOTH_STATE_CHANGED.equals(intent.getAction())){
        	int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            updateWidgetView(context,BluetoothStateTracker.bluetoothStateToFiveState(bluetoothState));    
            sBluetoothState.onActualStateChange(context,intent);       
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
			
			int bluetoothState = sBluetoothState.getActualState(context);
    		updateWidgetView(context,bluetoothState);
		}
    }
    
	/**
	* Method to update the widgets GUI
	*/
	private void updateWidgetView(Context context,int state){
		
	    Intent intent = new Intent(context, BluetoothWidgetProvider.class);
		intent.setAction(BLUETOOTH_CHANGED);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
	    RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.power_widget);
	    views.setOnClickPendingIntent(R.id.power_panel,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_press,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_item,pendingIntent);
		views.setOnClickPendingIntent(R.id.power_trigger,pendingIntent);
		views.setImageViewResource(R.id.power_item,R.drawable.widget_bluetooth_icon);
		views.setTextViewText(R.id.power_label,context.getString(R.string.bluetooth_gadget_caption));
		// We need to update the Widget GUI
		if (state == StateTracker.STATE_DISABLED){
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
		} else if (state == StateTracker.STATE_ENABLED) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_on);
		} else if (state == StateTracker.STATE_TURNING_ON) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_tween);
		} else if (state == StateTracker.STATE_TURNING_OFF) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_tween);
		} else if (state == StateTracker.STATE_UNKNOWN) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
		}
		
		ComponentName cn = new ComponentName(context, BluetoothWidgetProvider.class);  
		AppWidgetManager.getInstance(context).updateAppWidget(cn, views); 
	}
    
    /**
     * Subclass of StateTracker to get/set Bluetooth state.
     */
    private static final class BluetoothStateTracker extends StateTracker {

        @Override
        public int getActualState(Context context) {
            if (sLocalBluetoothManager == null) {
                sLocalBluetoothManager = LocalBluetoothManager.getInstance(context);
                if (sLocalBluetoothManager == null) {
                    return STATE_UNKNOWN;  // On emulator?
                }
            }
            return bluetoothStateToFiveState(sLocalBluetoothManager.getBluetoothState());
        }

        @Override
        protected void requestStateChange(Context context, final boolean desiredState) {
            if (sLocalBluetoothManager == null) {
                Log.d(TAG, "No LocalBluetoothManager");
                return;
            }
            // Actually request the Bluetooth change and persistent
            // settings write off the UI thread, as it can take a
            // user-noticeable amount of time, especially if there's
            // disk contention.
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... args) {
                    sLocalBluetoothManager.setBluetoothEnabled(desiredState);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onActualStateChange(Context context, Intent intent) {
            if (!BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                return;
            }
            int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            setCurrentState(context, bluetoothStateToFiveState(bluetoothState));
        }

        /**
         * Converts BluetoothAdapter's state values into our
         * Wifi/Bluetooth-common state values.
         */
        private static int bluetoothStateToFiveState(int bluetoothState) {
            switch (bluetoothState) {
                case BluetoothAdapter.STATE_OFF:
                    return STATE_DISABLED;
                case BluetoothAdapter.STATE_ON:
                    return STATE_ENABLED;
                case BluetoothAdapter.STATE_TURNING_ON:
                    return STATE_TURNING_ON;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    return STATE_TURNING_OFF;
                default:
                    return STATE_UNKNOWN;
            }
        }
    }
}
