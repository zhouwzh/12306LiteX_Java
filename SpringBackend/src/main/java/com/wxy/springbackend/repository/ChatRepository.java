package com.wxy.springbackend.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import dev.ai4j.openai4j.chat.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ChatRepository{
    ChatLanguageModel chatLanguageModel;
    List<String> windowsList;
    List<String> instructionsDescription;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ChatRepository(ChatLanguageModel chatLanguageModel){
        this.windowsList = List.of(
                "Search",
                "MyAccount",
                "MyOrders",
                "ShowTrains"
        );
        this.instructionsDescription = List.of(
                "Search: Opens a search interface for looking up train tickets.",
                "MyAccount: Displays the user's personal information and allows changing passwords, usernames, or editing their profile.",
                "MyOrders: Shows the user's order history, including both unpaid and paid tickets.",
                "ShowTrains: Three param (From, To, Date), all of these cannot be None. Displays trains from the specified origin to the specified destination on the given date (YYYY-MM-DD). If you are not sure all three params are. Just ask the user"
        );
        this.chatLanguageModel = chatLanguageModel;
    }


    public Map<String, Object> getResponse(String message) {
        int maxAttempts = 3;
        String userLocation = "New York"; // May get this param from Frontend
        String windowsListString = windowsList.toString();
        String instructionsDescriptionString = String.join("\n", instructionsDescription);
        String usefulInfo = getUsefulInfo(userLocation);

        String basePrompt = String.format(systemPrompt, windowsListString, instructionsDescriptionString, usefulInfo);
        String inputMessage = basePrompt + message;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                String llmOutput = chatLanguageModel.generate(inputMessage);
                System.out.println(inputMessage);
                System.out.println(llmOutput);
                Map<String, Object> responseMap = objectMapper.readValue(llmOutput, new TypeReference<Map<String, Object>>() {});

                // Validate Instruction field
                String instruction = getStringField(responseMap, "Instruction", true);
                // Check if instruction is in the windows list or "None"
                boolean validInstruction = "None".equalsIgnoreCase(instruction) || windowsList.contains(instruction);

                if (!validInstruction) {
                    if (attempt < maxAttempts) {
                        inputMessage = basePrompt + message + "\n" +
                                "\nYour previous provided Instruction is not one of the allowed values: " + windowsListString +
                                ". Please revise your response to follow the given format and use a valid Instruction.";
                        continue;
                    } else {
                        return defaultFallbackResponse();
                    }
                }

                // Validate parameters
                String param1 = getStringField(responseMap, "Param1", true);
                String param2 = getStringField(responseMap, "Param2", true);
                String param3 = getStringField(responseMap, "Param3", true);

                boolean validParams = validateParams(instruction, param1, param2, param3);

                if (!validParams) {
                    if (attempt < maxAttempts) {
                        inputMessage = basePrompt + message + "\n" +
                                "\nYour previous parameters are invalid for the given Instruction. " +
                                "Please ensure that if the Instruction is 'Search', 'MyAccount', or 'MyOrders', all parameters (Param1, Param2, Param3) must be 'None'. " +
                                "If the Instruction is 'ShowTrains(From, To, Date)', then all three parameters must be non-'None', and the third must be in YYYY-MM-DD format." +
                                "If the user doesn't have enough information for you to give an instruction, set it all to None and only respond to the user";
                        continue;
                    } else {
                        return defaultFallbackResponse();
                    }
                }

                // If we reach here, all validations have passed
                return responseMap;

            } catch (Exception e) {
                // JSON parsing or field retrieval error
                if (attempt < maxAttempts) {
                    inputMessage = basePrompt + message +
                            "\nYour previous response was not in the correct JSON format or had invalid fields. " +
                            "Please strictly follow the JSON response format described above.";
                } else {
                    return defaultFallbackResponse();
                }
            }
        }

        return defaultFallbackResponse();
    }

    private Map<String, Object> defaultFallbackResponse() {
        return Map.of(
                "message", "Sure, Have a good day!",
                "Instruction", "None",
                "Param1", "None",
                "Param2", "None",
                "Param3", "None"
        );
    }

    private String getStringField(Map<String, Object> map, String fieldName, boolean required) {
        Object val = map.get(fieldName);
        if (required && (!(val instanceof String))) {
            throw new IllegalArgumentException(fieldName + " field missing or invalid.");
        }
        return val == null ? null : (String) val;
    }

    private boolean validateParams(String instruction, String param1, String param2, String param3) {
        // Instructions that must have all params as None:
        List<String> noParamInstructions = List.of("Search", "MyAccount", "MyOrders");
        // Instruction that must have all three params non-None and a valid date:
        String showTrainsInstruction = "ShowTrains";

        if (noParamInstructions.contains(instruction)) {
            // All parameters should be "None"
            return "None".equalsIgnoreCase(param1) &&
                    "None".equalsIgnoreCase(param2) &&
                    "None".equalsIgnoreCase(param3);
        }

        if (showTrainsInstruction.equals(instruction)) {
            // All three parameters must be non-None
            if ("None".equalsIgnoreCase(param1) || "None".equalsIgnoreCase(param2) || "None".equalsIgnoreCase(param3)) {
                return false;
            }
            // Validate date format (YYYY-MM-DD) for param3
            return isValidDate(param3);
        }

        if ("None".equalsIgnoreCase(instruction)) {
            return "None".equalsIgnoreCase(param1) &&
                    "None".equalsIgnoreCase(param2) &&
                    "None".equalsIgnoreCase(param3);
        }

        return false;
    }

    private boolean isValidDate(String dateStr) {
        // Basic validation for YYYY-MM-DD
        try {
            java.time.LocalDate.parse(dateStr);
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }

    private static String getUsefulInfo(String userLocation) { // Trivial implementation of Function Call
        LocalDate today = LocalDate.now();
        String date = today.toString(); // Format: YYYY-MM-DD

        return String.format("""
        Some useful knowledge you may need:
        - Today's date: %s
        - User's location: %s
        """, date, userLocation);
    }

    private final String systemPrompt = """
        You are a train ticket subscription assistant. Your role has two main parts:
        
        If the user wants to use a specific feature (e.g., searching for trains, changing passwords), you must indicate which window to navigate to. The frontend will handle the actual transition; you only need to provide the appropriate instruction. A list of all possible instructions is given below: %s
        
        Note: Some instructions may look like INS(a,b,c). In such cases, simply provide the instruction name, and then supply the parameters in the response fields. If only one or two parameters are known, set the others to "None".
        
        Here is the description of each instruction:
        %s
        
        If the user is simply engaging in casual conversation or asking general questions, respond with wit and humor without instructing a window change. If the user's request does not correspond to any of the provided instructions, please apologize and indicate that no such function exists.
        
        %s
        
        Response Format:
        Always reply in JSON as follows, DO NOT provide any other words so that it will be failed to convert to JSON file:
        {
          "message": "Your witty or informative response here",
          "Instruction": "The instruction name from the provided list or 'None' if just chatting",
          "Param1": "The first parameter (e.g., 'New York' for ShowTrains). If not applicable, 'None'",
          "Param2": "The second parameter (e.g., 'Miami' for ShowTrains). If not applicable, 'None'",
          "Param3": "The third parameter (e.g., '2024-12-23' for ShowTrains). If not applicable, 'None'"
        }
        
        Now this is the user's request:
        """;
}
