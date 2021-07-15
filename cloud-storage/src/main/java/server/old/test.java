package server.old;

import server.old.LombokTest;

public class test {
    public static void main(String[] args) {
        LombokTest a = LombokTest.builder().
                a(2).
                b(2).
                build();
        System.out.println(a.getB());


    }

}
