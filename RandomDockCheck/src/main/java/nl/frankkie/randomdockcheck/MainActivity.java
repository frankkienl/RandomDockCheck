package nl.frankkie.randomdockcheck;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


public class MainActivity extends Activity {

    public static MainActivity thisAct;
    //Views
    Button btnChangeIme;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisAct = this;
        initUI();
        refreshUI();
    }

    protected void initUI() {
        setContentView(R.layout.activity_main_advanced);
        btnChangeIme = (Button) findViewById(R.id.main_btn_change_ime);
        btnChangeIme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputService = (InputMethodManager) thisAct.getSystemService(INPUT_METHOD_SERVICE);
                inputService.showInputMethodPicker();
            }
        });
        tableLayout = (TableLayout) findViewById(R.id.main_table);
    }

    protected void refreshUI() {
        tableLayout.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();
        //Dock
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_DOCK_EVENT);
        Intent dockStatus = registerReceiver(null, intentFilter);
        int dockState = dockStatus.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
        boolean isDocked = dockState != Intent.EXTRA_DOCK_STATE_UNDOCKED;
        boolean isCar = dockState == Intent.EXTRA_DOCK_STATE_CAR;
        boolean isDesk = dockState == Intent.EXTRA_DOCK_STATE_DESK ||
                dockState == Intent.EXTRA_DOCK_STATE_LE_DESK ||
                dockState == Intent.EXTRA_DOCK_STATE_HE_DESK;
        //Dock
        makeRow(layoutInflater, isDocked, R.string.docked);
        makeRow(layoutInflater, isCar, R.string.car);
        makeRow(layoutInflater, isDesk, R.string.desk);
        //IP
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //
        TableRow row = (TableRow) layoutInflater.inflate(R.layout.inflate_layout, null);
        TextView tv = (TextView) row.findViewById(R.id.inflate_tv);
        tv.setText(getString(R.string.ip_address) + ": " + getIPAddress(true));
        ImageView img = (ImageView) row.findViewById(R.id.inflate_img);
        img.setImageResource((mWifi.isConnected()) ? R.drawable.device_access_network_wifi : R.drawable.device_access_network_cell);
        tableLayout.addView(row);
    }

    protected void makeRow(LayoutInflater layoutInflater, boolean isOk, int stringResource) {
        TableRow row = (TableRow) layoutInflater.inflate(R.layout.inflate_layout, null);
        TextView tv = (TextView) row.findViewById(R.id.inflate_tv);
        tv.setText(getString(stringResource));
        ImageView img = (ImageView) row.findViewById(R.id.inflate_img);
        img.setImageResource((isOk) ? R.drawable.navigation_accept : R.drawable.navigation_cancel);
        tableLayout.addView(row);
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     *         src: http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
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
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent();
                i.setClass(thisAct, SettingsActivity.class);
                startActivity(i);
                return true;
            }
        });
        return true;
    }

}
