package org.example.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.hc.core5.pool.PoolStats;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HttpDemo {

    Gson gson = new GsonBuilder().create();

    User userRequest = User.builder()
            .id(1l)
            .username("anastasiiaaxaxa")
            .firstName("Anastasiia")
            .lastName("Izotova")
            .email("22enotik22@gmail.com")
            .password("3456789")
            .phone("+380997096811")
            .userStatus(1)
            .build();
    String baseUri = "https://jsonplaceholder.typicode.com/users";
    String baseUri1 = "https://jsonplaceholder.typicode.com/users/1";

    public void methodGetHttp() throws URISyntaxException, IOException, InterruptedException {
        String json = gson.toJson(userRequest);
        HttpRequest createUser = HttpRequest.newBuilder(new URI(baseUri))
                .method("POST", HttpRequest.BodyPublishers.ofString(json))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();


        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(createUser, HttpResponse.BodyHandlers.ofString());
        System.out.println("response.statusCode() = " + response.statusCode());
        System.out.println("response.body() = " + response.body());
        User createdUser = gson.fromJson(response.body(), User.class);
        HttpRequest getUserRequest = HttpRequest.newBuilder(new URI(baseUri + "/" + createdUser.getUsername()))
                .GET()
                .header("accept", "application/json")
                .build();

        HttpResponse<String> getResponse = httpClient.send(getUserRequest, HttpResponse.BodyHandlers.ofString());
        User userFromRemote = gson.fromJson(getResponse.body(), User.class);
        System.out.println("userFromRemote = " + userFromRemote);
    }

    public void deleteRequest() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest deleteRequest = HttpRequest.newBuilder(new URI(baseUri1))
                .DELETE()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> send = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("send.statusCode() = " + send.statusCode());
    }

    public static void updateUserHttp(User userToUpdate) throws URISyntaxException, IOException, InterruptedException {
        String baseUri = "https://jsonplaceholder.typicode.com/users";
        String userUri = baseUri + "/" + userToUpdate.getId();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest updateUserRequest = HttpRequest.newBuilder(new URI(userUri))
                .method("PUT", HttpRequest.BodyPublishers.ofString(userToUpdate.toJson()))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(updateUserRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("response.statusCode() = " + response.statusCode());
        System.out.println("response.body() = " + response.body());
    }

    public void getAllUsersHttp() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest getAllUsersRequest = HttpRequest.newBuilder(new URI(baseUri))
                .GET()
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(getAllUsersRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("response.statusCode() = " + response.statusCode());
        System.out.println("response.body() = " + response.body());
    }

    public void getUserByIdHttp(long userId) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String userUrl = baseUri + "/" + userId;
        HttpRequest getUserRequest = HttpRequest.newBuilder(new URI(userUrl))
                .GET()
                .header("accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(getUserRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("response.statusCode() = " + response.statusCode());
        System.out.println("response.body() = " + response.body());
    }

    public void getUserByUsername(String username) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String userUrl = baseUri + "?username=" + username;
        HttpRequest getUsernameRequest = HttpRequest.newBuilder(new URI(userUrl))
                .GET()
                .header("accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(getUsernameRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("response.statusCode() = " + response.statusCode());
        System.out.println("response.body() = " + response.body());
    }

    public void getUserLastPostCommentsAndSaveToFile(long userId) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        String userPostsUrl = "https://jsonplaceholder.typicode.com/users/" + userId + "/posts";
        HttpRequest userPostsRequest = HttpRequest.newBuilder(new URI(userPostsUrl))
                .GET()
                .header("accept", "application/json")
                .build();

        HttpResponse<String> userPostsResponse = httpClient.send(userPostsRequest, HttpResponse.BodyHandlers.ofString());

        if (userPostsResponse.statusCode() == 200) {
            List<Post> posts = gson.fromJson(userPostsResponse.body(), new TypeToken<List<Post>>() {
            }.getType());
            if (!posts.isEmpty()) {
                Long lastPostId = posts.get(posts.size() - 1).getId();
                String commentsUrl = "https://jsonplaceholder.typicode.com/posts/" + lastPostId + "/comments";
                HttpRequest commentsRequest = HttpRequest.newBuilder(new URI(commentsUrl))
                        .GET()
                        .header("accept", "application/json")
                        .build();

                HttpResponse<String> commentsResponse = httpClient.send(commentsRequest, HttpResponse.BodyHandlers.ofString());

                if (commentsResponse.statusCode() == 200) {
                    String commentsJson = commentsResponse.body();
                    String fileName = "user-" + userId + "-post-" + lastPostId + "-comments.json";

                    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                        writer.write(commentsJson);
                        System.out.println("Коментарі до останнього посту користувача " + userId + " збережено у файлі " + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Не вдалося отримати коментарі. Статус код: " + commentsResponse.statusCode());
                }
            } else {
                System.out.println("Користувач з ID " + userId + " не має постів.");
            }
        } else {
            System.out.println("Не вдалося отримати список постів користувача. Статус код: " + userPostsResponse.statusCode());
        }
    }

    public void getOpenTodosForUser(long userId) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        String userTodosUrl = "https://jsonplaceholder.typicode.com/users/" + userId + "/todos";
        HttpRequest userTodosRequest = HttpRequest.newBuilder(new URI(userTodosUrl))
                .GET()
                .header("accept", "application/json")
                .build();

        HttpResponse<String> userTodosResponse = httpClient.send(userTodosRequest, HttpResponse.BodyHandlers.ofString());

        if (userTodosResponse.statusCode() == 200) {
            String todosJson = userTodosResponse.body();
            Gson gson = new Gson();
            Todo[] todos = gson.fromJson(todosJson, Todo[].class);

            for (Todo todo : todos) {
                if (!todo.isCompleted()) {
                    String title = todo.getTitle();
                    System.out.println("Відкриті задачі(false) userId: " + userId + ": " + title);
                }
            }
        } else {
            System.out.println("Не вдалося отримати список задач користувача. Статус код: " + userTodosResponse.statusCode());
        }
    }
}

