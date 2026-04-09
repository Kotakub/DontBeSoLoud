package dontbesoloud.general;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import dontbesoloud.Main;

public class GeneralApiService {
    private static final String BASE_URL = "http://127.0.0.1:8000";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public ArrayList<Lesson> loadLessons(int groupId) {
        String token = Main.getToken();
        ArrayList<Lesson> lessons = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            // Формируем запрос. В URL передаем groupId как параметр
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/general/get_lessons?group_id=" + groupId))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json_body = mapper.readTree(response.body());

                if (json_body.isArray()) {
                    for (JsonNode lesson : json_body) {
                        lessons.add(new Lesson(
                                lesson.get("id").asInt(),
                                lesson.get("group_id").asInt(),
                                lesson.get("lesson_type").asText(),
                                lesson.get("sign_name").asText(),
                                lesson.get("sign_id").asInt(),
                                lesson.get("photo_url").asText(),
                                lesson.get("description").asText()
                        ));
                    }
                }
            } else {
                System.err.println("Сервер вернул ошибку: " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке уроков: " + e.getMessage());
        }

        return lessons;
    }

    public ArrayList<Group> loadGroups() {
        String token = Main.getToken();
        ArrayList<Group> groups = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            // Формируем запрос. В URL передаем groupId как параметр
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/general/get_groups"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json_body = mapper.readTree(response.body());

                if (json_body.isArray()) {
                    for (JsonNode group : json_body) {
                        groups.add(new Group(
                                group.get("id").asInt(),
                                group.get("name").asText(),
                                group.get("photo_url").asText()
                        ));
                    }
                }
            } else {
                System.err.println("Сервер вернул ошибку: " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке уроков: " + e.getMessage());
        }

        return groups;
    }

    public void saveGroup(Group group) {
        sendPostRequest("/general/save_group", group);
    }

    public void deleteGroup(int groupId) {
        String token = Main.getToken();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/general/delete_group?id=" + groupId))
                    .header("Authorization", "Bearer " + token)
                    .DELETE() // Используем метод DELETE для удаления
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Ошибка при удалении группы: " + e.getMessage());
        }
    }

    // --- МЕТОДЫ ДЛЯ УРОКОВ ---

    public void saveLesson(Lesson lesson, int groupId) {
        // Передаем урок вместе с ID группы, к которой он относится
        try {
            String json = mapper.writeValueAsString(lesson);
            sendPostRequest("/general/save_lesson?group_id=" + groupId, lesson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLesson(int lessonId) {
        String token = Main.getToken();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/general/delete_lesson?id=" + lessonId))
                    .header("Authorization", "Bearer " + token)
                    .DELETE()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Ошибка при удалении урока: " + e.getMessage());
        }
    }

    private void sendPostRequest(String path, Object body) {
        String token = Main.getToken();
        try {
            String jsonBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json") // Обязательно указываем тип контента
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.err.println("Ошибка сервера (" + path + "): " + response.statusCode() + " | " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Ошибка при выполнении POST запроса (" + path + "): " + e.getMessage());
        }
    }
}