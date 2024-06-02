import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatGPT {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Start chatting! Type 'exit' to end the chat.");

        while (true) {
            System.out.print("You: ");
            input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Chat ended.");
                break;
            }

            System.out.println("Bot: "+nextChat(input));
        }

        scanner.close();
    }
 	
    public static String nextChat(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        String apiKey = "MY_OPENAPI_KEY";
        //String model = "gpt-3.5-turbo";
        String model = "gpt-4o";

        try {
            URL obj = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // The request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
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

            return getGPTResponse(response.toString());

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


}
