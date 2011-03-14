package com.android.settings.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.settings.R;
import android.os.AsyncTask;

public class WifiWidgetProvider extends AppWidgetProvider {
    // TAG
    public static final String TAG = "Evervolv_WifiWidget";
    // States
	public static final int STATE_DISABLED = 0;
    public static final int STATE_ENABLED = 1;
    public static final int STATE_TURNING_ON = 2;
    public static final int STATE_TURNING_OFF = 3;
    public static final int STATE_UNKNOWN = 4;
    public static final int STATE_INTERMEDIATE = 5;
    // Intent Actions
    public static String WIFI_CHANGED = "com.evervolv.widget.WIFI_CLICKED";
    
    @Override
    public void onEnabled(Context context){
		super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context,
			 AppWidgetManager appWidgetManager,
			 int[] appWidgetIds){
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    	updateWidget(context, appWidgetManager, appWidgetIds);
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
	    	
		    RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.power_widget);
		    
			views.setImageViewResource(R.id.power_item,R.drawable.wifi_widget_icon);
			views.setTextViewText(R.id.power_label,context.getString(R.string.wifi_gadget_caption));
			ComponentName cn = new ComponentName(context, WifiWidgetProvider.class);  
			AppWidgetManager.getInstance(context).updateAppWidget(cn, views); 
			
			
		}
    }
    
    
}