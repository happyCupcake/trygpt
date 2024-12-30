import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

public class ChatGPT {
    static String url = "https://api.openai.com/v1/chat/completions";
    static String model = "gpt-4o";
    //static String model = "o1-preview";
    //static String model = "chatgpt-4o-latest";
    static String apiKey="";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
  

        System.out.println("Start chatting! Type 'exit' to end the chat.");


        System.out.println("Please briefly describe the persona of the chatbot:");
        String systemContext = scanner.nextLine();

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Chat ended.");
                break;
            }

            System.out.println("Bot: "+nextChat(systemContext, input));
        }

        scanner.close();
    }
 	
    public static String nextChat(String systemBehavior, String prompt) {
        try {
            URL obj = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");

            // Create both messages using GPTMessage class
            GPTMessage systemMessage = new GPTMessage("system", systemBehavior);
            GPTMessage userMessage = new GPTMessage("user", prompt);
            
            // Create request structure
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", Arrays.asList(systemMessage, userMessage));
            
            // Convert to JSON using Gson
            Gson gson = new Gson();
            String body = gson.toJson(requestBody);
            
            // Send request
            connection.setDoOutput(true);
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(body);
                writer.flush();
            }
            
            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            System.out.println(getResponse(response.toString()).choices[0].message.GetRole());
            System.out.println(getResponse(response.toString()).choices[0].finishReason);    

            //return getGPTResponse(response.toString());
            return getResponse(response.toString()).choices[0].message.GetContent();

        } catch (URISyntaxException e){
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
}

   public static String getGPTResponse(String response) {
       int start = response.indexOf("content")+ 11;

       int end = response.indexOf("\"", start);

       return response.substring(start, end);
   }

   public static GPTResponse getResponse(String response) {
        Gson gson = new Gson();
        return gson.fromJson(response, GPTResponse.class);

   }    
}


