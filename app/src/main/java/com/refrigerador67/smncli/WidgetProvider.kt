package com.refrigerador67.smncli

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->


            // Set layout
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.widgetlayout
            )

            // Tell the AppWidgetManager to perform an update on the current widget.
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}