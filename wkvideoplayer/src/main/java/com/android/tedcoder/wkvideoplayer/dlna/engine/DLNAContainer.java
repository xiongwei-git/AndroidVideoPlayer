/*
 *
 * Copyright 2015 TedXiong xiong-wei@hotmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tedcoder.wkvideoplayer.dlna.engine;

import com.android.tedcoder.wkvideoplayer.dlna.util.DLNAUtil;
import com.android.tedcoder.wkvideoplayer.dlna.util.LogUtil;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

public class DLNAContainer {
	private List<Device> mDevices;
	private Device mSelectedDevice;
	private DeviceChangeListener mDeviceChangeListener;
	private static final DLNAContainer mDLNAContainer = new DLNAContainer();
	private static final String TAG = "DLNAContainer";

	private DLNAContainer() {
		mDevices = new ArrayList<Device>();
	}

	public static DLNAContainer getInstance() {
		return mDLNAContainer;
	}

	public synchronized void addDevice(Device d) {
		if (!DLNAUtil.isMediaRenderDevice(d))
			return;
		int size = mDevices.size();
		for (int i = 0; i < size; i++) {
			String udnString = mDevices.get(i).getUDN();
			if (d.getUDN().equalsIgnoreCase(udnString)) {
				return;
			}
		}
		
		mDevices.add(d);
		LogUtil.d(TAG, "Devices add a device" + d.getDeviceType());
		if (mDeviceChangeListener != null) {
			mDeviceChangeListener.onDeviceChange(d);
		}
	}

	public synchronized void removeDevice(Device d) {
		if (!DLNAUtil.isMediaRenderDevice(d)) {
			return;
		}
		int size = mDevices.size();
		for (int i = 0; i < size; i++) {
			String udnString = mDevices.get(i).getUDN();
			if (d.getUDN().equalsIgnoreCase(udnString)) {
				Device device = mDevices.remove(i);
				LogUtil.d(TAG, "Devices remove a device");

				boolean ret = false;
				if (mSelectedDevice != null) {
					ret = mSelectedDevice.getUDN().equalsIgnoreCase(
							device.getUDN());
				}
				if (ret) {
					mSelectedDevice = null;
				}
				if (mDeviceChangeListener != null) {
					mDeviceChangeListener.onDeviceChange(d);
				}
				break;
			}
		}
	}

	public synchronized void clear() {
		if (mDevices != null) {
			mDevices.clear();
			mSelectedDevice = null;
		}
	}

	public Device getSelectedDevice() {
		return mSelectedDevice;
	}

	public void setSelectedDevice(Device mSelectedDevice) {
		this.mSelectedDevice = mSelectedDevice;
	}

	public void setDeviceChangeListener(
			DeviceChangeListener deviceChangeListener) {
		mDeviceChangeListener = deviceChangeListener;
	}

	public List<Device> getDevices() {
		return mDevices;
	}

	public interface DeviceChangeListener {
		void onDeviceChange(Device device);
	}

}
