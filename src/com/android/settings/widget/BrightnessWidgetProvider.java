package com.android.settings.widget;

import com.android.settings.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import android.net.Uri;

public class BrightnessWidgetProvider extends AppWidgetProvider {
	
    // TAG
    public static final String TAG = "Evervolv_BrightnessWidget";
    private boolean DBG = false;
    // Intent Actions
    public static String BRIGHTNESS_CHANGED = "com.evervolv.widget.BRIGHTNESS_CLICKED";

    /**
     * Minimum and maximum brightnesses.  Don't go to 0 since that makes the display unusable
     */
    private static final int MINIMUM_BACKLIGHT = android.os.Power.BRIGHTNESS_DIM + 10;
    private static final int MAXIMUM_BACKLIGHT = android.os.Power.BRIGHTNESS_ON;
    private static final int DEFAULT_BACKLIGHT = (int) (android.os.Power.BRIGHTNESS_ON * 0.4f);
    private static final int BUTTON_BRIGHTNESS = 1;
    
    private int mBrightnessMode = 1;
    private static final int BRIGHTNESS_AUTO = 1;
    private static final int BRIGHTNESS_DEFAULT = 2;
    private static final int BRIGHTNESS_MAXIMUM = 3;
    private static final int BRIGHTNESS_MINIMUM = 4;
    
    @Override
    public void onEnabled(Context context){
		PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.BrightnessWidgetProvider"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    
    @Override
    public void onDisabled(Context context) {
    	if (DBG) Log.d(TAG,"Received request to remove last widget");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("com.android.settings",
                ".widget.BrightnessWidgetProvider"),
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
    	if (BRIGHTNESS_CHANGED.equals(intent.getAction())){
    		toggleBrightness(context);
    		updateWidgetView(context);
    	}
    	
    	if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            if (buttonId == BUTTON_BRIGHTNESS) {
                toggleBrightness(context);
        		updateWidgetView(context);
            } 
        }
    }
    
	/**
	* Method to update the widgets GUI
	*/
	private void updateWidgetView(Context context){
	
	    Intent intent = new Intent(context, BrightnessWidgetProvider.class);
		intent.setAction(BRIGHTNESS_CHANGED);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
	    RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.power_widget);
	    views.setOnClickPendingIntent(R.id.power_panel,
	    				pendingIntent);
		views.setOnClickPendingIntent(R.id.power_press,
						pendingIntent);
		views.setOnClickPendingIntent(R.id.power_item,
						pendingIntent);
		views.setOnClickPendingIntent(R.id.power_trigger,
						pendingIntent);
		views.setImageViewResource(R.id.power_trigger,
						R.drawable.brightness_switch_auto);
		views.setTextViewText(R.id.power_label,context.getString(
						R.string.brightness_gadget_caption));
		
		// We need to update the Widget GUI
		if (mBrightnessMode == BRIGHTNESS_AUTO)  {
			views.setImageViewResource(R.id.power_trigger,
							R.drawable.brightness_switch_auto);
			views.setImageViewResource(R.id.power_item,
							R.drawable.widget_brightness_icon);
		} else if (mBrightnessMode == BRIGHTNESS_MINIMUM) {
			views.setImageViewResource(R.id.power_trigger,
							R.drawable.brightness_switch_min);
			views.setImageViewResource(R.id.power_item,
							R.drawable.widget_brightness_icon_02);
		} else if (mBrightnessMode == BRIGHTNESS_MAXIMUM) {
			views.setImageViewResource(R.id.power_trigger,
							R.drawable.brightness_switch_max);
			views.setImageViewResource(R.id.power_item,
							R.drawable.widget_brightness_icon_03);
		} else if (mBrightnessMode == BRIGHTNESS_DEFAULT) {
			views.setImageViewResource(R.id.power_trigger,
							R.drawable.brightness_switch_default);
			views.setImageViewResource(R.id.power_item,
							R.drawable.widget_brightness_icon_04);
		}
		
		ComponentName cn = new ComponentName(context, BrightnessWidgetProvider.class);  
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
			
    		updateWidgetView(context);
		}
    }
    
    /**
     * Gets state of brightness.
     *
     * @param context
     * @return true if more than moderately bright.
     */
    private static boolean getBrightness(Context context) {
        try {
            IPowerManager power = IPowerManager.Stub.asInterface(
                    ServiceManager.getService("power"));
            if (power != null) {
                int brightness = Settings.System.getInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS);
                return brightness > 100;
            }
        } catch (Exception e) {
            Log.d(TAG, "getBrightness: " + e);
        }
        return false;
    }
    
    /**
     * Gets state of brightness mode.
     *
     * @param context
     * @return true if auto brightness is on.
     */
    private static boolean getBrightnessMode(Context context) {
        try {
            IPowerManager power = IPowerManager.Stub.asInterface(
                    ServiceManager.getService("power"));
            if (power != null) {
                int brightnessMode = Settings.System.getInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE);
                return brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            }
        } catch (Exception e) {
            Log.d(TAG, "getBrightnessMode: " + e);
        }
        return false;
    }

    /**
     * Increases or decreases the brightness.
     *
     * @param context
     */
    private void toggleBrightness(Context context) {
        try {
            IPowerManager power = IPowerManager.Stub.asInterface(
                    ServiceManager.getService("power"));
            if (power != null) {
                ContentResolver cr = context.getContentResolver();
                int brightness = Settings.System.getInt(cr,
                        Settings.System.SCREEN_BRIGHTNESS);
                int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
                //Only get brightness setting if available
                if (context.getResources().getBoolean(
                        com.android.internal.R.bool.config_automatic_brightness_available)) {
                    brightnessMode = Settings.System.getInt(cr,
                            Settings.System.SCREEN_BRIGHTNESS_MODE);
                }

                // Rotate AUTO -> MINIMUM -> DEFAULT -> MAXIMUM
                // Technically, not a toggle...
                if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    brightness = MINIMUM_BACKLIGHT;
                    brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
                    mBrightnessMode = BRIGHTNESS_MINIMUM;
                } else if (brightness < DEFAULT_BACKLIGHT) {
                    brightness = DEFAULT_BACKLIGHT;
                    mBrightnessMode = BRIGHTNESS_DEFAULT;
                } else if (brightness < MAXIMUM_BACKLIGHT) {
                    brightness = MAXIMUM_BACKLIGHT;
                    mBrightnessMode = BRIGHTNESS_MAXIMUM;
                } else {
                    brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
                    brightness = MINIMUM_BACKLIGHT;
                    mBrightnessMode = BRIGHTNESS_AUTO;
                }

                if (context.getResources().getBoolean(
                        com.android.internal.R.bool.config_automatic_brightness_available)) {
                    // Set screen brightness mode (automatic or manual)
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE,
                            brightnessMode);
                } else {
                    // Make sure we set the brightness if automatic mode isn't available
                    brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
                }
                if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                    power.setBacklightBrightness(brightness);
                    Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "toggleBrightness: " + e);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(TAG, "toggleBrightness: " + e);
        }
    }
}

