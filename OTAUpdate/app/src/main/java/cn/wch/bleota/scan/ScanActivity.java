package cn.wch.bleota.scan;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.touchmcu.ui.DialogUtil;

import cn.wch.bleota.R;
import cn.wch.bleota.scan.ui.DeviceAdapter;
import cn.wch.bleota.scan.ui.DownloadFileDialog;
import cn.wch.otalib.WCHOTAManager;
import cn.wch.otalib.utils.LocationUtil;
import cn.wch.otalib.utils.LogTool;
import cn.wch.otaupdate.MainActivity;
import cn.wch.otaupdate.other.Constant;

public class ScanActivity extends ScanBaseActivity {

    RecyclerView recyclerView;
    DeviceAdapter adapter;
    boolean isScanning=false;
    Handler handler=new Handler(Looper.getMainLooper());
    @Override
    void initWidget() {
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.search_ble);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.rvDevice);

        adapter=new DeviceAdapter(this, new DeviceAdapter.Listener() {
            @Override
            public void onClick(BluetoothDevice device) {
                toAnotherActivity(device.getAddress());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void autoRun() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                startScan();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        menu.findItem(R.id.startScan).setVisible(!isScanning);
        menu.findItem(R.id.stopScan).setVisible(isScanning);
        menu.findItem(R.id.indicator).setVisible(isScanning);
        menu.findItem(R.id.indicator).setActionView(R.layout.menu_progress);
        //目前不支持download功能
        menu.findItem(R.id.download).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.startScan){
            startScan();
        }else if(itemId==R.id.stopScan){
            stopScan();
        }else if(itemId==R.id.download){
            showDownloadDialog();
        }
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    void startScan(){
        LogTool.d(Thread.currentThread().getId()+"");
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //大于安卓10，需要检查定位服务
            LogTool.d("location service open?：" + LocationUtil.isLocationEnable(this));
            if (!LocationUtil.isLocationEnable(this)) {
                DialogUtil.getInstance().showSimpleDialog(this, getString(R.string.scanning_need_location), new DialogUtil.IResult() {
                    @Override
                    public void onContinue() {
                        LocationUtil.requestLocationService(ScanActivity.this);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

                return;
            }
        }

        if (!isBluetoothAdapterOpened()) {
            showToast(getString(R.string.open_ble));
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            showToast("定位权限未开启");
            return;
        }
        isScanning=true;

        try {
            WCHOTAManager.getInstance().startScan(scanCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

        invalidateOptionsMenu();
    }

    void stopScan(){
        isScanning=false;
        LogTool.d("stop scan");

        try {
            WCHOTAManager.getInstance().stopScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
        invalidateOptionsMenu();
    }

    void toAnotherActivity(String mac){
        stopScan();
        Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra(Constant.ADDRESS,mac);
        startActivity(intent);
    }

    ScanCallback scanCallback=new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if(adapter!=null && device.getType()==BluetoothDevice.DEVICE_TYPE_LE){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.update(device,result.getRssi());
                    }
                });

            }
        }
    };

    private void showDownloadDialog(){
        DownloadFileDialog dialog=DownloadFileDialog.newInstance();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(),DownloadFileDialog.class.getName());
    }

}