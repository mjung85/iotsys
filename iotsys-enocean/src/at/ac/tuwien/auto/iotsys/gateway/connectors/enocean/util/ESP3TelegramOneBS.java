package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3TelegramOneBS extends ESP3TelegramRPS {
	public ESP3TelegramOneBS(byte[] data, int dataLen, int optDataLen) {
		super(data, dataLen, optDataLen);
	}

	public ESP3TelegramOneBS(byte telegramData) {
		super(telegramData);
	}
}
