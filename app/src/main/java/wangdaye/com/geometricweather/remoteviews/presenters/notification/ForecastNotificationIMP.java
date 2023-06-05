package wangdaye.com.geometricweather.remoteviews.presenters.notification;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import wangdaye.com.geometricweather.GeometricWeather;
import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.common.basic.models.Location;
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit;
import wangdaye.com.geometricweather.common.basic.models.weather.Weather;
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode;
import wangdaye.com.geometricweather.common.utils.LanguageUtils;
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter;
import wangdaye.com.geometricweather.settings.SettingsManager;
import wangdaye.com.geometricweather.theme.resource.ResourceHelper;
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory;
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider;

/**
 * Forecast notification utils.
 * */

public class ForecastNotificationIMP extends AbstractRemoteViewsPresenter {

    public static void buildForecastAndSendIt(Context context, Location location, boolean today) {
        Weather weather = location.getWeather();
        if (weather == null) {
            return;
        }

        ResourceProvider provider = ResourcesProviderFactory.getNewInstance();

        LanguageUtils.setLanguage(
                context,
                SettingsManager.getInstance(context).getLanguage().getLocale()
        );
        
        // create channel.
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    GeometricWeather.NOTIFICATION_CHANNEL_ID_FORECAST,
                    GeometricWeather.getNotificationChannelName(
                            context, GeometricWeather.NOTIFICATION_CHANNEL_ID_FORECAST),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel);
        }

        // get builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, GeometricWeather.NOTIFICATION_CHANNEL_ID_FORECAST);

        // set notification level.
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        // set notification visibility.
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        WeatherCode weatherCode;
        boolean daytime;
        if (today) {
            daytime = location.isDaylight();
            weatherCode = daytime 
                    ? weather.getDailyForecast().get(0).getDay().getWeatherCode() 
                    : weather.getDailyForecast().get(0).getNight().getWeatherCode();
        } else {
            daytime = true;
            weatherCode = weather.getDailyForecast().get(1).getDay().getWeatherCode() ;
        }

        // set small icon.
        builder.setSmallIcon(
                ResourceHelper.getDefaultMinimalXmlIconId(weatherCode, daytime));

        // large icon.
        builder.setLargeIcon(
                drawableToBitmap(
                        ResourceHelper.getWeatherIcon(provider, weatherCode, daytime)
                )
        );

        // sub text.
        if (today) {
            builder.setSubText(context.getString(R.string.today));
        } else {
            builder.setSubText(context.getString(R.string.tomorrow));
        }

        TemperatureUnit temperatureUnit = SettingsManager.getInstance(context).getTemperatureUnit();

        // title and content.
        if (today) {
            builder.setContentTitle(context.getString(R.string.daytime)
                    + " " + weather.getDailyForecast().get(0).getDay().getWeatherText()
                    + " " + weather.getDailyForecast().get(0).getDay().getTemperature().getTemperature(context, temperatureUnit)
            ).setContentText(context.getString(R.string.nighttime)
                    + " " + weather.getDailyForecast().get(0).getNight().getWeatherText()
                    + " " + weather.getDailyForecast().get(0).getNight().getTemperature().getTemperature(context, temperatureUnit)
            );
        } else {
            builder.setContentTitle(context.getString(R.string.daytime)
                    + " " + weather.getDailyForecast().get(1).getDay().getWeatherText()
                    + " " + weather.getDailyForecast().get(1).getDay().getTemperature().getTemperature(context, temperatureUnit)
            ).setContentText(context.getString(R.string.nighttime)
                    + " " + weather.getDailyForecast().get(1).getNight().getWeatherText()
                    + " " + weather.getDailyForecast().get(1).getNight().getTemperature().getTemperature(context, temperatureUnit)
            );
        }

        // set intent.
        builder.setContentIntent(
                getWeatherPendingIntent(
                        context,
                        null,
                        today
                                ? GeometricWeather.NOTIFICATION_ID_TODAY_FORECAST
                                : GeometricWeather.NOTIFICATION_ID_TOMORROW_FORECAST
                )
        );

        // set sound & vibrate.
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);

        // set badge.
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);

        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                notification.getClass()
                        .getMethod("setSmallIcon", Icon.class)
                        .invoke(
                                notification,
                                ResourceHelper.getMinimalIcon(
                                        provider, weather.getCurrent().getWeatherCode(), daytime)
                        );
            } catch (Exception ignore) {
                // do nothing.
            }
        }

        // commit.
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED) {
            manager.notify(
                    today
                            ? GeometricWeather.NOTIFICATION_ID_TODAY_FORECAST
                            : GeometricWeather.NOTIFICATION_ID_TOMORROW_FORECAST,
                    notification
            );
        }
    }

    public static boolean isEnable(Context context, boolean today) {
        if (today) {
            return SettingsManager.getInstance(context).isTodayForecastEnabled();
        } else {
            return SettingsManager.getInstance(context).isTomorrowForecastEnabled();
        }
    }
}
