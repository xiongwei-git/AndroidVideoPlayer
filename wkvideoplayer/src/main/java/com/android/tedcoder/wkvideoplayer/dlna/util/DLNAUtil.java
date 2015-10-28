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

package com.android.tedcoder.wkvideoplayer.dlna.util;

import org.cybergarage.upnp.Device;

public class DLNAUtil {
	private static final String MEDIARENDER = "urn:schemas-upnp-org:device:MediaRenderer:1";

	/**
	 * Check if the device is a media render device
	 * 
	 * @param device
	 * @return
	 */
	public static boolean isMediaRenderDevice(Device device) {
		if (device != null
				&& MEDIARENDER.equalsIgnoreCase(device.getDeviceType())) {
			return true;
		}

		return false;
	}
}
