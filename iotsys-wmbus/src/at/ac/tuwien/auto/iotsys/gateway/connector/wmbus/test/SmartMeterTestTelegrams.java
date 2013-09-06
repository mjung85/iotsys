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

package at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.test;

public interface SmartMeterTestTelegrams {
	
	public static String[] telegrams = {
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 6E 00 30 85 0C D9 D1 D7 D2 C0 99 FE 5A 46 99 5F E2 6B 19 18 BA 22 27 01 03 0A 07 51 49 EA 6C 5D 29 F6 CE 8D A2 FC B5 96 64 71 50 CE 4F 8C 1F E8 21 39 07 3B",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 6F 00 30 85 D3 DC C8 8F F1 6A F5 8A FA 99 9A 15 39 EC CD 4D 6F 4D 44 36 8F 1C 9E 50 96 69 90 0D 5B 31 F6 95 28 A0 EC 1E 35 AD E7 9A 4D CB 07 58 C5 FF 6A C7",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 7C 00 30 85 02 0E CE 2F 67 55 76 7F 2C E2 15 D4 A9 0B EE 8D C3 32 1D D1 B1 C6 0E 05 69 D7 F6 9D 6C 2A 0A DE 8C BC FA F3 AE A0 26 73 5D 10 22 F8 92 3D DF D5",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 7D 00 30 85 42 D8 BB DA F6 C6 6B 49 89 F4 BC B4 5B 88 FA 91 12 69 EC 72 EF C5 19 A4 C0 69 CB BF F5 1C 86 CB 67 A0 E9 7C 23 24 DB DC 78 1C 45 98 49 DE 9F 15",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 81 00 30 85 B3 FA 51 64 ED F8 B8 ED 63 67 43 4B 83 FC 96 EE 1F F9 38 6B BC FD D0 AC 0C 1D E0 EE D6 C8 F5 03 01 D4 00 37 C6 78 93 9D C0 B6 5D AC D4 81 72 8D",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 83 00 30 85 85 83 1B FC 4B 77 48 73 38 F4 F5 58 FE 33 0A FB C3 3E 77 9A 90 61 53 8A B1 9A EF E6 A2 3A F2 B0 7F B6 79 E3 B6 B2 94 74 4F 70 A4 AF 1F 30 3C 18",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 84 00 30 85 DC 42 DF 30 4F 51 2C A7 E6 B8 2F 1D 94 72 B7 2D A2 81 A3 D0 85 94 86 69 4E 7F D5 82 95 35 9A D5 EE F5 9F 36 3A FA 71 82 6B 03 7A E7 C3 C1 A3 31",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 85 00 30 85 F1 DB D7 73 AB B8 D3 99 79 A0 53 F2 5E AF 82 09 E9 41 2F AC 7A 3E A2 F7 96 0E 2B 65 F1 6A 84 D4 FA 60 EB E6 6A B1 61 DD BD D4 8B 14 4F D9 4E 89",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 86 00 30 85 14 D2 0E 87 B3 61 03 79 1B AB 5D 96 36 8F CE 96 69 1C E6 A7 FA 20 4D D5 58 89 7C 4D 21 B9 14 F9 DC D8 BE 9C 87 E7 E5 2C C4 A7 EB 8B 3E 16 B5 3A",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 87 00 30 85 92 72 09 DC 35 D9 2E D7 82 96 6F FC 86 2F 6F 82 27 83 33 45 EB 99 EC E3 3F 3C 4F 83 33 E4 94 DA 06 57 65 58 D7 B2 CE 7B 80 30 DC 07 D5 93 F3 DF",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 88 00 30 85 25 2E B5 B5 2B 01 2C 75 C3 E0 74 D1 14 66 83 BC F4 B5 60 8A 14 B3 76 B3 8F E9 62 6B 9F 74 56 52 D0 BD 99 95 5C 6D B6 10 E5 AC 26 70 81 20 75 FD",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 89 00 30 85 D4 63 0D FA EA 41 2A 9D 01 79 54 D5 E3 8E 93 8A 43 D6 D5 B2 9C 92 D4 DF A5 8A 4E 0C 61 70 B8 C7 17 8A F3 0C 75 5F 0C F0 A7 A3 80 5E BA C0 EC 0A",
		"3E 44 2D 4C 74 44 00 15 1E 02 7A 8A 00 30 85 47 4E AB 1A ED 7B 9A C4 82 B2 FE 31 45 03 1A B3 41 5A 69 F2 75 B3 9B D6 04 11 53 EB BE 67 76 CA 08 4F 46 04 18 2B AD 13 21 FA FB 9B 7E 12 02 30"
	};
}
