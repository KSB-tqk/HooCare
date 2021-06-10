package cf.khanhsb.icare_v2.Model;

import android.graphics.drawable.Drawable;

public class AppUsageInfo {
    Drawable appIcon; // You may add get this usage data also, if you wish.
    String appName;
    public String packageName;
    public long timeInForeground;
    public int launchCount;

    public AppUsageInfo(String pName) {
        this.packageName=pName;
    }
}
