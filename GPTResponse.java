public class GPTResponse {
    GPTChoice []choices;
}


class GPTChoice {
     String finishReason;
     int index;
    GPTMessage message;
    
}

class GPTMessage {
     String content;
     String role;
}