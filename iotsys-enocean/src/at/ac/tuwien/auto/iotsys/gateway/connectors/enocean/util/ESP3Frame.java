/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

import java.util.logging.Logger;

public class ESP3Frame {
	private static final Logger log = Logger.getLogger(ESP3Frame.class.getName());
	public enum STATES_GET_PACKET {
		GET_SYNC_STATE(0x01), GET_HEADER_STATE(0x02), CHECK_CRC8H_STATE(0x03), GET_DATA_STATE(
				0x04), CHECK_CRC8D_STATE(0x05);

		private int state;

		private STATES_GET_PACKET(int state) {
			this.state = state;
		}

		public int getState() {
			return this.state;
		}
	}

	private byte[] packetData;
	private int packetLength;
	private ESP3PacketHeader header;
	private ESP3Packet packet;

	public ESP3Frame(byte[] packetData, int packetLength) {
		this.packetData = packetData;
		this.packetLength = packetLength;
	}

	public byte[] getPacketData() {
		return packetData;
	}

	public int getPacketLength() {
		return packetLength;
	}

	public void readPacket() {
		STATES_GET_PACKET state = STATES_GET_PACKET.GET_SYNC_STATE;
		byte[] tempHeaderBuffer = new byte[ESP3PacketHeader.ESP3_HEADER_SIZE];
		byte[] tempDataBuffer = null;
		int headerCnt = 0;
		int dataCnt = 0;

		for (int i = 0; i < packetLength; i++) {
			byte tempData = packetData[i];

			switch (state) {
			case GET_SYNC_STATE:
				if (tempData == ESP3PacketHeader.ESP3_SYNC) {
					state = STATES_GET_PACKET.GET_HEADER_STATE;
					headerCnt = 0;
					dataCnt = 0;
					tempDataBuffer = null;
				} else {
					log.finest("Sync byte not found!");
				}
				break;

			case GET_HEADER_STATE:
				tempHeaderBuffer[headerCnt++] = tempData;

				if (headerCnt == ESP3PacketHeader.ESP3_HEADER_SIZE) {
					state = STATES_GET_PACKET.CHECK_CRC8H_STATE;
				}
				break;

			case CHECK_CRC8H_STATE:
				if (ESP3PacketHeader.checkCRC8(tempHeaderBuffer, tempData)) {
					header = new ESP3PacketHeader(tempHeaderBuffer);
					state = STATES_GET_PACKET.GET_DATA_STATE;
				} else {
					log.finest("Wrong CRC8H: "
							+ Integer.toHexString(tempData & 0xFF));
					state = STATES_GET_PACKET.GET_SYNC_STATE;
				}
				break;

			case GET_DATA_STATE:
				if (tempDataBuffer == null)
					tempDataBuffer = new byte[header.getDataLength()
							+ header.getOptionalDataLength()];

				tempDataBuffer[dataCnt++] = tempData;
				if (dataCnt == (header.getDataLength() + header
						.getOptionalDataLength())) {
					state = STATES_GET_PACKET.CHECK_CRC8D_STATE;
				}
				break;

			case CHECK_CRC8D_STATE:
				state = STATES_GET_PACKET.GET_SYNC_STATE;

				if (ESP3PacketHeader.checkCRC8(tempDataBuffer, tempData)) {
					packet = new ESP3Packet(header, tempDataBuffer);
					log.finest(packet.getHeader().toString());
					log.finest("telegram rorg: "
							+ packet.telegram.getRORG());
					log.finest("telegram senderid: "
							+ packet.telegram.getSenderID().toString());
					log.finest("telegram status: "
							+ Integer.toHexString((packet.telegram
									.getStatusByte() & 0xFF)));
					log.finest("telegram payload: "
							+ packet.telegram.getPayloadAsString());
					log.finest("");
				} else {
					log.finest("Wrong CRC8D: "
							+ Integer.toHexString(tempData & 0xFF));
				}
				break;

			default:
				state = STATES_GET_PACKET.GET_SYNC_STATE;
				break;

			}

		}
	}

	public ESP3Packet getPacket() {
		return packet;
	}

	public ESP3PacketHeader getPacketHeader() {
		return header;
	}

}
