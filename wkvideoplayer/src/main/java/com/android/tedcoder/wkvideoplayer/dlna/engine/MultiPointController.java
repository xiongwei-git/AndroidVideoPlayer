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

import android.text.TextUtils;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import com.android.tedcoder.wkvideoplayer.dlna.inter.IController;


public class MultiPointController implements IController {
	private static final String AVTransport1 = "urn:schemas-upnp-org:service:AVTransport:1";
	private static final String SetAVTransportURI = "SetAVTransportURI";
	private static final String RenderingControl = "urn:schemas-upnp-org:service:RenderingControl:1";
	private static final String Play = "Play";

	@Override
	public boolean play(Device device, String path) {
		Service service = device.getService(AVTransport1);

		if (service == null) {
			return false;
		}

		final Action action = service.getAction(SetAVTransportURI);
		if (action == null) {
			return false;
		}

		final Action playAction = service.getAction(Play);
		if (playAction == null) {
			return false;
		}

		if (TextUtils.isEmpty(path)) {
			return false;
		}

		action.setArgumentValue("InstanceID", 0);
		action.setArgumentValue("CurrentURI", path);
		action.setArgumentValue("CurrentURIMetaData", 0);
		if (!action.postControlAction()) {
			return false;
		}

		playAction.setArgumentValue("InstanceID", 0);
		playAction.setArgumentValue("Speed", "1");
		return playAction.postControlAction();
	}

	@Override
	public boolean goon(Device device, String pausePosition) {

		Service localService = device.getService(AVTransport1);
		if (localService == null)
			return false;

		final Action localAction = localService.getAction("Seek");
		if (localAction == null)
			return false;
		localAction.setArgumentValue("InstanceID", "0");
		// if (mUseRelTime) {
		// } else {
		// localAction.setArgumentValue("Unit", "ABS_TIME");
		// }
		// LogUtil.e(tag, "继续相对时间："+mUseRelTime);
		// 测试解决播放暂停后时间不准确
		localAction.setArgumentValue("Unit", "ABS_TIME");
		localAction.setArgumentValue("Target", pausePosition);
		localAction.postControlAction();

		Action playAction = localService.getAction("Play");
		if (playAction == null) {
			return false;
		}

		playAction.setArgumentValue("InstanceID", 0);
		playAction.setArgumentValue("Speed", "1");
		return playAction.postControlAction();
	}

	@Override
	public String getTransportState(Device device) {
		Service localService = device.getService(AVTransport1);
		if (localService == null) {
			return null;
		}

		final Action localAction = localService.getAction("GetTransportInfo");
		if (localAction == null) {
			return null;
		}

		localAction.setArgumentValue("InstanceID", "0");

		if (localAction.postControlAction()) {
			return localAction.getArgumentValue("CurrentTransportState");
		} else {
			return null;
		}
	}

	public String getVolumeDbRange(Device device, String argument) {
		Service localService = device.getService(RenderingControl);
		if (localService == null) {
			return null;
		}
		Action localAction = localService.getAction("GetVolumeDBRange");
		if (localAction == null) {
			return null;
		}
		localAction.setArgumentValue("InstanceID", "0");
		localAction.setArgumentValue("Channel", "Master");
		if (!localAction.postControlAction()) {
			return null;
		} else {
			return localAction.getArgumentValue(argument);
		}
	}

	@Override
	public int getMinVolumeValue(Device device) {
		String minValue = getVolumeDbRange(device, "MinValue");
		if (TextUtils.isEmpty(minValue)) {
			return 0;
		}
		return Integer.parseInt(minValue);
	}

	@Override
	public int getMaxVolumeValue(Device device) {
		String maxValue = getVolumeDbRange(device, "MaxValue");
		if (TextUtils.isEmpty(maxValue)) {
			return 100;
		}
		return Integer.parseInt(maxValue);
	}

	@Override
	public boolean seek(Device device, String targetPosition) {
		Service localService = device.getService(AVTransport1);
		if (localService == null)
			return false;

		Action localAction = localService.getAction("Seek");
		if (localAction == null) {
			return false;
		}
		localAction.setArgumentValue("InstanceID", "0");
		// if (mUseRelTime) {
		// localAction.setArgumentValue("Unit", "REL_TIME");
		// } else {
		localAction.setArgumentValue("Unit", "ABS_TIME");
		// }
		localAction.setArgumentValue("Target", targetPosition);
		boolean postControlAction = localAction.postControlAction();
		if (!postControlAction) {
			localAction.setArgumentValue("Unit", "REL_TIME");
			localAction.setArgumentValue("Target", targetPosition);
			return localAction.postControlAction();
		} else {
			return postControlAction;
		}

	}

	@Override
	public String getPositionInfo(Device device) {
		Service localService = device.getService(AVTransport1);

		if (localService == null)
			return null;

		final Action localAction = localService.getAction("GetPositionInfo");
		if (localAction == null) {
			return null;
		}

		localAction.setArgumentValue("InstanceID", "0");
		boolean isSuccess = localAction.postControlAction();
		if (isSuccess) {
			return localAction.getArgumentValue("AbsTime");
		} else {
			return null;
		}
	}

	@Override
	public String getMediaDuration(Device device) {
		Service localService = device.getService(AVTransport1);
		if (localService == null) {
			return null;
		}

		final Action localAction = localService.getAction("GetMediaInfo");
		if (localAction == null) {
			return null;
		}

		localAction.setArgumentValue("InstanceID", "0");
		if (localAction.postControlAction()) {
			return localAction.getArgumentValue("MediaDuration");
		} else {
			return null;
		}

	}

	@Override
	public boolean setMute(Device mediaRenderDevice, String targetValue) {
		Service service = mediaRenderDevice.getService(RenderingControl);
		if (service == null) {
			return false;
		}
		final Action action = service.getAction("SetMute");
		if (action == null) {
			return false;
		}

		action.setArgumentValue("InstanceID", "0");
		action.setArgumentValue("Channel", "Master");
		action.setArgumentValue("DesiredMute", targetValue);
		return action.postControlAction();
	}

	@Override
	public String getMute(Device device) {
		Service service = device.getService(RenderingControl);
		if (service == null) {
			return null;
		}

		final Action getAction = service.getAction("GetMute");
		if (getAction == null) {
			return null;
		}
		getAction.setArgumentValue("InstanceID", "0");
		getAction.setArgumentValue("Channel", "Master");
		getAction.postControlAction();
		return getAction.getArgumentValue("CurrentMute");
	}

	@Override
	public boolean setVoice(Device device, int value) {
		Service service = device.getService(RenderingControl);
		if (service == null) {
			return false;
		}

		final Action action = service.getAction("SetVolume");
		if (action == null) {
			return false;
		}

		action.setArgumentValue("InstanceID", "0");
		action.setArgumentValue("Channel", "Master");
		action.setArgumentValue("DesiredVolume", value);
		return action.postControlAction();

	}

	@Override
	public int getVoice(Device device) {
		Service service = device.getService(RenderingControl);
		if (service == null) {
			return -1;
		}

		final Action getAction = service.getAction("GetVolume");
		if (getAction == null) {
			return -1;
		}
		getAction.setArgumentValue("InstanceID", "0");
		getAction.setArgumentValue("Channel", "Master");
		if (getAction.postControlAction()) {
			return getAction.getArgumentIntegerValue("CurrentVolume");
		} else {
			return -1;
		}

	}

	@Override
	public boolean stop(Device device) {
		Service service = device.getService(AVTransport1);

		if (service == null) {
			return false;
		}
		final Action stopAction = service.getAction("Stop");
		if (stopAction == null) {
			return false;
		}

		stopAction.setArgumentValue("InstanceID", 0);
		return stopAction.postControlAction();

	}

	@Override
	public boolean pause(Device mediaRenderDevice) {

		Service service = mediaRenderDevice.getService(AVTransport1);
		if (service == null) {
			return false;
		}
		final Action pauseAction = service.getAction("Pause");
		if (pauseAction == null) {
			return false;
		}
		pauseAction.setArgumentValue("InstanceID", 0);
		return pauseAction.postControlAction();
	}

}
