//package com.androidapp.pizzamania;
//
//import com.google.ai.client.generativeai.GenerativeModel;
//import com.google.ai.client.generativeai.type.Content;
//import com.google.ai.client.generativeai.type.GenerateContentResponse;
//
//import java.util.concurrent.CompletableFuture;
//
//public class GeminiHelper {
//
//    private static final String TAG = "Gemini Helper";
//    private final GenerativeModel model;
//
//    public GeminiHelper(String apiKey){
//        model = new GenerativeModel("gemini-1.5-flash", apiKey);
//    }
// //AIzaSyDn_NoDpmnWQUWSJQ_XLPnwCcx9QlqLl1U
//    public CompletableFuture<String> generateReply(String userMsg) {
//        return CompletableFuture.supplyAsync(() -> {
//            Content prompt = new Content.Builder()
//                    .addText(userMsg)
//                    .build();
//
////            GenerateContentResponse response = model.generateContent(prompt);
////            return response.getText();
//        });
//    }
//}
