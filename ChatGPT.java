import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;

public class ChatGPT {
    static String url = "https://api.openai.com/v1/chat/completions";
    static String model = "gpt-4o";
    //static String model = "o1-preview";
    //static String model = "chatgpt-4o-latest";
    static String apiKey="";


    // Keep track of conversation history
    static List<GPTMessage> conversationHistory = new ArrayList<>();
    static final int MAX_HISTORY = 4;

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
            List<GPTMessage> messagesToSend = new ArrayList<>();

            // Always include System message as the first message
            messagesToSend.add(new GPTMessage("system", systemBehavior));

            // Include last four messages in the conversation history as context for the AI Bot
            // The last four includes two back-and-forth exchanges from user and bot
            // The right role is automatically included in the history
            if (!conversationHistory.isEmpty()) {
                int historyStart = Math.max(0, conversationHistory.size() - MAX_HISTORY);
                messagesToSend.addAll(conversationHistory.subList(historyStart, conversationHistory.size()));
            }
            
            // After the system behavior and some context, lets include the current user message
            GPTMessage userMessage = new GPTMessage("user", prompt);
            messagesToSend.add(userMessage);
            

            URL obj = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");

            // Create request structure
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messagesToSend);
            
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

            
            // Get response from API
            GPTResponse gptResponse = getResponse(response.toString());
            GPTMessage assistantMessage = gptResponse.choices[0].message;

            // Add both the current user message and the corresponding AI response to history in the end
            conversationHistory.add(userMessage);        // Add user message
            conversationHistory.add(assistantMessage);   // Add bot response


            // Trim history if needed
            if (conversationHistory.size() > MAX_HISTORY) {
                conversationHistory = new ArrayList<>(
                    conversationHistory.subList(
                        conversationHistory.size() - MAX_HISTORY, 
                        conversationHistory.size()
                    )
                );
            }

            
            return assistantMessage.GetContent();

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


