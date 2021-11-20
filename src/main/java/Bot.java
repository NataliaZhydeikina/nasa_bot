import com.fasterxml.jackson.core.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.validation.groups.Default;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "nasa_1558_bot";
    }

    @Override
    public String getBotToken() {
        return "2081827625:AAHtt7D6aECxcE8ordYQAIwDR1mhfC5LbCU";
    }
    String startDate = "2020-11-05";
    String endDate = "2021-11-05";
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            String command = update.getMessage().getText();
            if(command.startsWith("/start")){
                String answerText = nasaRequest();
                send(update, answerText);
            } else if(command.startsWith("/startDate")){
                String[] commandsData = command.split(" ");
                startDate = commandsData[1];
                String answerText = nasaRequest();
                send(update, answerText);
            } else if(command.startsWith("/endDate")){
                String[] commandsData = command.split(" ");
                endDate = commandsData[1];
                String answerText = nasaRequest();
                send(update, answerText);
            } else {
                send(update, "no such command");
            }
        }
    }

    public void send(Update update, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String nasaRequest() {
        String url = "https://api.nasa.gov/DONKI/FLR?startDate="
                +startDate+"&endDate="
                +endDate+"&api_key=bRgR8oBMZahSCNPU25yCUvPn3ystRSkAZe724aLG";
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)
                    urlObj.openConnection();
            connection.setRequestMethod("GET");
            Scanner scanner = new Scanner(connection.getInputStream());
            String requestResult = "";
            while (scanner.hasNextLine()){
                requestResult += scanner.nextLine();
            }
            return getAnswer(requestResult);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error: nooooo!!!!";

    }


    public String getAnswer(String request){
        try {
            JSONParser parser = new JSONParser();
            JSONArray nasaArray = (JSONArray)parser.parse(request);
            String result = "";
            int length = 10;
            if(nasaArray.size()<10) {
                length = nasaArray.size();
            }
            for (int i = 0; i < length; i++) {
                JSONObject currentObject = (JSONObject) nasaArray.get(i);
                result += "\n Location: "
                        +currentObject.get("sourceLocation").toString();
                result += "\n class: "
                        +currentObject.get("classType").toString();
                result += "\n time: "
                        +currentObject.get("beginTime").toString();
            }
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "no data";
    }



    public static void main(String[] args){
        try {
            TelegramBotsApi telegramBotsApi
                    = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
