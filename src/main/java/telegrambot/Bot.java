package telegrambot;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import mqtt.MqttSubscriber;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
	private final static Logger LOGGER = Logger.getLogger(Bot.class);

	private boolean flag = true;

	public static void main(String[] args) {
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new Bot());

		} catch (TelegramApiRequestException e) {
			LOGGER.error("TelegramApiRequestException",e);
		}
	}

	public synchronized void sendMsg(Message message, String text) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);

		sendMessage.setChatId(message.getChatId().toString());

		sendMessage.setReplyToMessageId(message.getMessageId());

		sendMessage.setText(text);
		try {

			setButtons(sendMessage);
			execute(sendMessage);

		} catch (TelegramApiException e) {
			LOGGER.error("TelegramApiException",e);
		}
	}

	public void onUpdateReceived(Update update) {
		String clientId = MqttClient.generateClientId();
		MqttSubscriber subscriber = new MqttSubscriber(clientId);
		try {
			subscriber.subscribe();
		} catch (MqttException e1) {
			LOGGER.error("MqttException",e1);
		}
		Message message = update.getMessage();
		if (message != null && message.hasText()) {
			switch (message.getText()) {
			case "/help":
				sendMsg(message, "Help");
				break;
			case "/recieve":
				sendMsg(message, "Start");
				while (flag) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						LOGGER.error("InterruptedException",e);
					}
					if (subscriber.getMessage() == null) {
						sendMsg(message, "not found");
						break;
					} else {
						sendMsg(message, String.valueOf(subscriber.getMessage()));
					}
				}
				break;
			default:
				sendMsg(message, String.valueOf(subscriber.getMessage()));
				break;

			}
		}

	}

	public void setButtons(SendMessage sendMessage) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(false);

		List<KeyboardRow> keyboardRowList = new ArrayList<>();
		KeyboardRow keyboardFirstRow = new KeyboardRow();

		keyboardFirstRow.add(new KeyboardButton("/help"));
		keyboardFirstRow.add(new KeyboardButton("/recieve"));

		keyboardRowList.add(keyboardFirstRow);
		replyKeyboardMarkup.setKeyboard(keyboardRowList);

	}

	public String getBotUsername() {
		return "LightSensorMQTTMonitoring_BOT";
	}

	public String getBotToken() {
		return "759667637:AAGMbNxjvozjF8Hdl5Eayd2ms_WW1riKlEg";
	}
}
