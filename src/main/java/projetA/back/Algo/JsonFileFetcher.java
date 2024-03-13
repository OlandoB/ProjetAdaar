package projetA.back.Algo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class JsonFileFetcher {

    public static String fetchJsonFile(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            throw new IOException("HTTP GET Erreur: " + responseCode);
        }
    }


    public static String gettitre(int bookid){
        String apiUrl = "https://gutendex.com/books/"+Integer.toString(bookid)+".json";
        try {
            String jsonContent = fetchJsonFile(apiUrl);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            //System.out.println(title);
            return jsonObject.get("title").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        String apiUrl = "https://gutendex.com/books/123.json";
        try {
            String jsonContent = fetchJsonFile(apiUrl);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            String title = jsonObject.get("title").getAsString();
            System.out.println(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
