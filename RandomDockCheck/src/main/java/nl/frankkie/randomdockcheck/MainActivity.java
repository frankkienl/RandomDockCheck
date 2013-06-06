package nl.frankkie.randomdockcheck;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends Activity {

    TextView tv;
    TextView ipTv;
    public static MainActivity thisAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.main_tv);
        ipTv = (TextView) findViewById(R.id.main_iptv);
        refreshDockState();
        thisAct = this;
    }

    protected void refreshDockState() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_DOCK_EVENT);
        Intent dockStatus = registerReceiver(null, intentFilter);
        int dockState = dockStatus.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
        boolean isDocked = dockState != Intent.EXTRA_DOCK_STATE_UNDOCKED;
        boolean isCar = dockState == Intent.EXTRA_DOCK_STATE_CAR;
        boolean isDesk = dockState == Intent.EXTRA_DOCK_STATE_DESK ||
                dockState == Intent.EXTRA_DOCK_STATE_LE_DESK ||
                dockState == Intent.EXTRA_DOCK_STATE_HE_DESK;
        StringBuilder sb = new StringBuilder();
        sb.append("Dock State:\nis docked: ").append(isDocked).append("\nis car: ").append(isCar).append("\nis desk:").append(isDesk);
        tv.setText(sb.toString());
        tv.invalidate();

        String ip = getIPAddress(true);
        ipTv.setText("IP: " + ip);
    }

    protected String getIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        //inetAddress.
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }


    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
