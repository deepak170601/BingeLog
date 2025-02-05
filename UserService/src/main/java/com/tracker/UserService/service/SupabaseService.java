package com.tracker.UserService.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.UserService.config.SupabaseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupabaseService {

    private final SupabaseConfig supabaseConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode fetchFromSupabase(String table, String column, String value) {
        String url = supabaseConfig.getSupabaseUrl() + "/rest/v1/" + table + "?"
                + column + "=eq." + value;

        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            log.error("Error parsing Supabase response: {}", e.getMessage());
            return null;
        }
    }

    public JsonNode postToSupabase(String table, Map<String, Object> data) {
        String url = supabaseConfig.getSupabaseUrl() + "/rest/v1/" + table;

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(data, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
            log.error("Error posting to Supabase: {}", e.getMessage());
            return null;
        }
    }

    public void updateInSupabase(String table, String column, String value, Map<String, Object> updatedData) {
        String url = supabaseConfig.getSupabaseUrl() + "/rest/v1/" + table + "?"
                + column + "=eq." + value;

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updatedData, headers);
        restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseConfig.getSupabaseApiKey());
        headers.set("Authorization", "Bearer " + supabaseConfig.getSupabaseApiKey());
        return headers;
    }
}
