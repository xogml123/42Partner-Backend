package com.seoul.openproject.partner.service.slack;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SlackBotService {

    @Value("${slack.bot.token}")
    private String slackToken;

    @Value("${slack.uri}")
    private String slackURI;

    public Optional<String> getSlackIdByEmail(String slackEmail){
        String url = slackURI + "users.lookupByEmail";
        String email = slackEmail;
        url += "?email=" + email;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-type", "application/x-www-form-urlencoded");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            String.class
        );
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        if (jsonObject.getBoolean("ok")){
            JSONObject profile = jsonObject.getJSONObject("user");
            String id = null;
            if (profile.get("id") instanceof String){
                id = (String) profile.get("id");
            }
            return Optional.ofNullable(id);
        }else{
            return Optional.empty();
        }
    }

    public String sendMessage(String slackId, String message) {
        String url = slackURI + "chat.postMessage";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-type", "application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channel", slackId);
        jsonObject.put("text", message);
        String body = jsonObject.toString();

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        JSONObject jsonResponse = new JSONObject(responseEntity.getBody());
        if (jsonResponse.getBoolean("ok")){
            return jsonResponse.getString("channel");
        }else{
            return null;
        }

    }

    public Optional<String> createMPIM(List<String> slackIds){
        String url = slackURI + "conversations.open";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-type", "application/json; charset=utf-8");

        String userIds = String.join(",", slackIds);
        System.out.println("userIds = " + userIds);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", userIds);
        String body = jsonObject.toString();

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        JSONObject jsonResponse = new JSONObject(responseEntity.getBody());
        if (jsonResponse.getBoolean("ok")){
            return Optional.ofNullable(jsonResponse.getJSONObject("channel").getString("id"));
        }else{
            return Optional.ofNullable();
        }

    }
}
