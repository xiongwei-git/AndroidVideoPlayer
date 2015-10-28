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

package com.android.tedcoder.wkvideoplayer.dlna.inter;

import org.cybergarage.upnp.Device;

public interface IController {

	/**
	 * Play the video with the video path.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @param path
	 *            The path of the video.
	 * @return If is success to play the video.
	 */
	boolean play(Device device, String path);

	/**
	 * Go on playing the video from the position.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @param pausePosition
	 *            The format must be 00:00:00.
	 */
	boolean goon(Device device, String pausePosition);

	/**
	 * All the state is "STOPPED" // "PLAYING" // "TRANSITIONING"//
	 * "PAUSED_PLAYBACK"// "PAUSED_RECORDING"// "RECORDING" //
	 * "NO_MEDIA_PRESENT//
	 */
	String getTransportState(Device device);

	/**
	 * Get the min volume value,this must be 0.
	 * 
	 * @param device
	 * @return
	 */
	int getMinVolumeValue(Device device);

	/**
	 * Get the max volume value, usually it is 100.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @return The max volume value.
	 */
	int getMaxVolumeValue(Device device);

	/**
	 * Seek the playing video to a target position.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @param targetPosition
	 *            Target position we want to set.
	 * @return
	 */
	boolean seek(Device device, String targetPosition);

	/**
	 * Get the current playing position of the video.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @return Current playing position is 00:00:00
	 */
	String getPositionInfo(Device device);

	/**
	 * Get the duration of the video playing.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @return The media duration like 00:00:00,if get failed it will return
	 *         null.
	 */
	String getMediaDuration(Device device);

	/**
	 * Mute the device or not.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @param targetValue
	 *            1 is that want mute, 0 if you want make it off of mute.
	 * @return If is success to mute the device.
	 */
	boolean setMute(Device device, String targetValue);

	/**
	 * Get if the device is mute.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @return 1 is mute, otherwise will return 0.
	 */
	String getMute(Device device);

	/**
	 * Set the device's voice.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @param value
	 *            Target voice want be set.
	 * @return
	 */
	boolean setVoice(Device device, int value);

	/**
	 * Get the current voice of the device.
	 * 
	 * @param device
	 *            The device be controlled.
	 * @return Current voice.
	 */
	int getVoice(Device device);

	/**
	 * Stop to play.
	 * 
	 * @param device
	 *            The device to controlled.
	 * @return If if success to stop the video.
	 */
	boolean stop(Device device);

	/**
	 * Pause the playing video.
	 * 
	 * @param device
	 *            The device to controlled.
	 * @return If if success to pause the video.
	 */
	boolean pause(Device device);
}
