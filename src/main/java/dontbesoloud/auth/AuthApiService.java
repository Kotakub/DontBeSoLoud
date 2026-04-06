package dontbesoloud.auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;


public class AuthApiService {
    private static final String BASE_URL = "http://127.0.0.1:8000";

    public boolean isTokenValid(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/checkToken"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;

        } catch (Exception e) {
            System.err.println("Ошибка при проверке токена: " + e.getMessage());
        }
        return false;
    }

    public String authorization(String email, String password) {
        try {
            Map<String, String> data = new java.util.HashMap<>();
            data.put("email", email);
            data.put("password", password);

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(data);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/authorization"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());
                return root.get("access_token").asText();
            } else {
                System.err.println("Ошибка авторизации: Код " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении запроса: " + e.getMessage());

        }
        return "";
    }

    public String registration(String name, String email, String password) {
        try {
            Map<String, String> data = new java.util.HashMap<>();
            data.put("name", name);
            data.put("email", email);
            data.put("password", password);

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(data);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/registration"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                JsonNode root = mapper.readTree(response.body());
                return root.get("access_token").asText();
            } else {
                System.err.println("Ошибка авторизации: Код " + response.statusCode());

            }

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
        return "";
    }

    public boolean isAdmin(String token) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/role"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json_body = mapper.readTree(response.body());
            String role = json_body.get("role").asText();
            if (response.statusCode() == 200 && role.equals("admin")) {
                return true;
            }


        } catch (Exception e) {
            System.err.println("Ошибка при получении роли: " + e.getMessage());
        }
        return false;
    }
}