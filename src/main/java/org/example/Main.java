package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Random;

public class Main extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        // Bu yerda bot foydalanuvchi nomini kiriting
        return "tashkentinfocsbot";
    }

    @Override
    public String getBotToken() {
        // Bu yerda bot tokenini kiriting
        return "7965927079:AAEJndw-OXIKAP157jbXQwanmIEN468E4ug";
    }
    String[] arr = {"DNX", "JAVAh KO'T", "Damini ol yban", "o'rtolarin bilan o'ynagin borib", "darmet jala"};

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            System.out.println(update.getMessage().getChatId() + " : " + update.getMessage().getFrom().getFirstName());
            switch (messageText) {
                case "/start" ->
                        sendMessage(chatId, "Salom! Bu CS 1.6 info bot. O'yin haqida ma'lumot olish uchun buyruqlarni kiriting. /info");
                case "/help" -> sendMessage(chatId, "Bu yerda yordam haqida ma'lumotlar bo'ladi.");
                case "/info" -> {
                    try {
                        getServerInfo(chatId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendMessage(chatId, "Serverdan ma'lumot olishda xatolik yuz berdi.");
                    }
                }
                default -> {
                    if (chatId == 5246215841L){
                        Random r = new Random();
                        sendMessage(chatId, arr[r.nextInt(arr.length)]);
                    }
                }
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getServerInfo(long chatId) throws IOException {
        String serverIp = "5.182.26.226";
        int serverPort = 27032;

        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(serverIp);

        // A2S_INFO request packet
        byte[] request = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x54, 'S', 'o', 'u', 'r', 'c', 'e', ' ', 'E', 'n', 'g', 'i', 'n', 'e', ' ', 'Q', 'u', 'e', 'r', 'y', 0x00};

        DatagramPacket packet = new DatagramPacket(request, request.length, address, serverPort);
        socket.send(packet);

        byte[] buffer = new byte[1400];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);

        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        // Parsing the response
        byteBuffer.position(4); // Skip the header (4 bytes)
        byte responseType = byteBuffer.get(); // Read response type
        if (responseType != 0x49) {
            throw new IOException("Unexpected response type: " + responseType);
        }

        byteBuffer.get(); // Skip the Network Version (1 byte)
        String serverName = readString(byteBuffer);
        String mapName = readString(byteBuffer);
        String gameDirectory = readString(byteBuffer);
        String gameDescription = readString(byteBuffer);

        byteBuffer.get(); // Skip the game version (1 byte)
        int players = byteBuffer.get() & 0xFF;  // Read players
        int maxPlayers = byteBuffer.get() & 0xFF;  // Read max players

        StringBuilder info = new StringBuilder();
        info.append("ℹ Server nomi: ").append(serverName).append("\n");
        info.append("\uD83D\uDDFA Xarita: ").append(mapName).append("\n");
        info.append("\uD83D\uDD2B O'yinchilar: ").append(maxPlayers).append("/").append("32").append("\n");
        info.append("\uD83D\uDCDD Info: ").append("@cstashkentinfobot\n");
        info.append("\uD83D\uDC68\u200D\uD83E\uDDB1 Admin: ").append("@xumoyiddin_xolmuminov");

        sendMessage(chatId, info.toString());

        socket.close();
    }

    private String readString(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        char c;
        while ((c = (char) buffer.get()) != 0) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(new Main());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}