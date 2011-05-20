package com.android.settings.widget;

import java.util.ArrayList;

import android.app.PendingIntent;
import com.android.settings.R;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

public class TetherWidgetProvider extends AppWidgetProvider{
    // TAG
    public static final String TAG = "Evervolv_TetherWidget";
    private boolean DBG = true;
    // Intent Actions
    public static String TETHER_STATE_CHANGED = "android.net.conn.TETHER_STATE_CHANGED";
    public static String TETHER_CHANGED = "com.evervolv.widget.TETHER_CLICKED";
    
    private int UsbTetherStatus; // Going to be STATE_DISABLED, STATE_ENABLED, STATE_UNAVAILABLE
    
    private String[] mUsbRegexs;
    
    @Override
    public void onEnabled(Context context){
		PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.TetherWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        
        ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    
        mUsbRegexs = cm.getTetherableUsbRegexs();
    }
    
    @Override
    public void onDisabled(Context context) {
        if (DBG) Log.d(TAG,"Received request to remove last widget");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.TetherWidgetProvider"),
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
    
    	ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    
        mUsbRegexs = cm.getTetherableUsbRegexs();
    }
    
    /**
	* this method will receive all Intents that it registers for in
	* the android manifest file.
	*/
    @Override
    public void onReceive(Context context, Intent intent){
    	if (DBG) Log.d(TAG, "onReceive - " + intent.toString());
    	super.onReceive(context, intent);
        
    	ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    
        mUsbRegexs = cm.getTetherableUsbRegexs();
        
    	if (TETHER_CHANGED.equals(intent.getAction())){
    		toggleState(context);
    	} else if (TETHER_STATE_CHANGED.equals(intent.getAction())) {
            ArrayList<String> available = intent.getStringArrayListExtra(
                    ConnectivityManager.EXTRA_AVAILABLE_TETHER);
            ArrayList<String> active = intent.getStringArrayListExtra(
                    ConnectivityManager.EXTRA_ACTIVE_TETHER);
            ArrayList<String> errored = intent.getStringArrayListExtra(
                    ConnectivityManager.EXTRA_ERRORED_TETHER);
            updateState(context, available.toArray(), active.toArray(), errored.toArray());
    	} else if (intent.getAction().equals(Intent.ACTION_MEDIA_SHARED) ||
                intent.getAction().equals(Intent.ACTION_MEDIA_UNSHARED)) {
            updateState(context);
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
			
	    	updateState(context);
		}
    }
    
	/**
	* Method to update the widgets GUI
	*/
	private void updateWidgetView(Context context,int state){
	
	    Intent intent = new Intent(context, TetherWidgetProvider.class);
		intent.setAction(TETHER_CHANGED);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
	    RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.power_widget);
	    if (state == StateTracker.STATE_DISABLED || state == StateTracker.STATE_ENABLED) {
		    views.setOnClickPendingIntent(R.id.power_panel,pendingIntent);
			views.setOnClickPendingIntent(R.id.power_press,pendingIntent);
			views.setOnClickPendingIntent(R.id.power_item,pendingIntent);
			views.setOnClickPendingIntent(R.id.power_trigger,pendingIntent);
	    }
		views.setTextViewText(R.id.power_label,context.getString(R.string.tether_gadget_caption));
		// We need to update the Widget GUI
		if (state == StateTracker.STATE_DISABLED){
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_wired_icon_off);
		} else if (state == StateTracker.STATE_ENABLED) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_allon);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_wired_icon_on);
		} else if (state == StateTracker.STATE_UNAVAILABLE) {
			views.setImageViewResource(R.id.power_trigger,R.drawable.power_switch_off);
			views.setImageViewResource(R.id.power_item,R.drawable.widget_wired_icon_unavailable);
		}
		
		ComponentName cn = new ComponentName(context, TetherWidgetProvider.class);  
		AppWidgetManager.getInstance(context).updateAppWidget(cn, views); 
	}
    
    
    private void toggleState(Context context) {
    	ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	
    	// have to do this, otherwise UsbTetherStatus will equal 0... Why?
    	updateState(context);
    	
        if (UsbTetherStatus == StateTracker.STATE_DISABLED) {
            String[] available = cm.getTetherableIfaces();
            String usbIface = findIface(available, mUsbRegexs);
            if (usbIface == null) {
                updateState(context);
            }
            cm.tether(usbIface);
        } else if (UsbTetherStatus == StateTracker.STATE_ENABLED) {
            String [] tethered = cm.getTetheredIfaces();
            String usbIface = findIface(tethered, mUsbRegexs);
            if (usbIface == null) {
                updateState(context);
            }
            cm.untether(usbIface);
        } else {
        	Log.d(TAG, "toggleState: else");
        }
    }
    
    private String findIface(String[] ifaces, String[] regexes) {
        for (String iface : ifaces) {
            for (String regex : regexes) {
                if (iface.matches(regex)) {
                    return iface;
                }
            }
        }
        return null;
    }
    
    private void updateState(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        String[] available = cm.getTetherableIfaces();
        String[] tethered = cm.getTetheredIfaces();
        String[] errored = cm.getTetheringErroredIfaces();
        updateState(context, available, tethered, errored);
    }
    
    private void updateState(Context context, Object[] available, Object[] tethered,
            Object[] errored) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        mUsbRegexs = cm.getTetherableUsbRegexs();
        
        boolean usbTethered = false;
        boolean usbAvailable = false;
        int usbError = ConnectivityManager.TETHER_ERROR_NO_ERROR;
        boolean usbErrored = false;
        boolean massStorageActive =
                Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState());
        for (Object o : available) {
            String s = (String)o;
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) {
                    usbAvailable = true;
                    if (usbError == ConnectivityManager.TETHER_ERROR_NO_ERROR) {
                        usbError = cm.getLastTetherError(s);
                    }
                }
            }
        }
        for (Object o : tethered) {
            String s = (String)o;
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) usbTethered = true;
            }
        }
        for (Object o: errored) {
            String s = (String)o;
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) usbErrored = true;
            }
        }

        if (usbTethered) {
        	updateWidgetView(context,StateTracker.STATE_ENABLED);
        	UsbTetherStatus = StateTracker.STATE_ENABLED;
        } else if (usbAvailable) {
        	updateWidgetView(context,StateTracker.STATE_DISABLED);
        	UsbTetherStatus = StateTracker.STATE_DISABLED;
        } else if (usbErrored) {
        	updateWidgetView(context,StateTracker.STATE_UNAVAILABLE);
        	UsbTetherStatus = StateTracker.STATE_UNAVAILABLE;
        } else if (massStorageActive) {
        	updateWidgetView(context,StateTracker.STATE_UNAVAILABLE);
        	UsbTetherStatus = StateTracker.STATE_UNAVAILABLE;
        } else {
        	// This occurs when the phone is unplugged.. Is there another state we can catch?
        	// but this also occurs intermediately when enabled tethering.
        	updateWidgetView(context,StateTracker.STATE_UNAVAILABLE);
        	UsbTetherStatus = StateTracker.STATE_UNAVAILABLE;
        }
        if (DBG) Log.d(TAG, "updateState() / UsbTetherStatus: " + UsbTetherStatus);
    }
    
}
