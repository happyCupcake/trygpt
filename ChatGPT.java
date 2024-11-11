import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.google.gson.Gson;

public class ChatGPT {
    static String url = "https://api.openai.com/v1/chat/completions";
    static String model = "chatgpt-4o-latest";
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
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // The request body
            String systemMessage = "{\"role\": \"system\", \"content\": \"" +    systemBehavior + "\"},";

            String body = "{\"model\": \"" + model + "\", \"messages\": ["+systemMessage + "{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            System.out.println(getResponse(response.toString()).choices[0].message.role);
            System.out.println(getResponse(response.toString()).choices[0].finishReason);    

            //return getGPTResponse(response.toString());
            return getResponse(response.toString()).choices[0].message.content;

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

