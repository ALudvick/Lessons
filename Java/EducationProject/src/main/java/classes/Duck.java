package classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Duck {
    // private variables
    private String nickname;
    private int age;
    private boolean position;
    private int score;

    // constructor
    public Duck(String nickname, int age, boolean position) {
        this.nickname = nickname;
        this.age = age;
        this.position = position;
        score = 0;
    }

    // simple method
    public void sayHello() {
        if (position) System.out.printf("Привет, утка %s! Твой возраст - %d, ты танк!\n", nickname, age);
        else System.out.printf("Привет, утка %s! Твой возраст - %d, ты хиллер и всегда виноват!\n", nickname, age);
    }

    // getter for score
    public int getScore() {
        return score;
    }

    // setter for score
    public void setScore(String word) {
        this.score = word.length();
    }

    // getter for nickname
    public String getNickname() {
        return nickname;
    }

    //override toString() method
    @Override
    public String toString() {
        return "Duck{" +
                "nickname='" + nickname + '\'' +
                ", age=" + age +
                ", position=" + position +
                ", score=" + score +
                '}';
    }
}
