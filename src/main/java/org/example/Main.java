package org.example;

import org.example.http.HttpDemo;
import org.example.http.User;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        HttpDemo httpDemo = new HttpDemo();
//        httpDemo.methodGetHttp();
//        httpDemo.deleteRequest();
//        updateUser();
//        httpDemo.getAllUsersHttp();
//        httpDemo.getUserByIdHttp(1);
//        httpDemo.getUserByUsername("Bret");

//        httpDemo.getUserLastPostCommentsAndSaveToFile(1);
        httpDemo.getOpenTodosForUser(1);
    }

    private static void updateUser() {
        try {
            User userToUpdate = new User();
            userToUpdate.setId(1L);
            userToUpdate.setUsername("patron_dsns");
            userToUpdate.setFirstName("Patron");
            userToUpdate.setLastName("DSNS");
            userToUpdate.setEmail("patronchik@gmail.com");
            userToUpdate.setPassword("Tysyhihsi9008_iksn");
            userToUpdate.setPhone("+38055488412");

            HttpDemo.updateUserHttp(userToUpdate);
            userToUpdate.setUserStatus(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}