package com.github.robert2411.sma.smasunnyboy15;

import org.junit.Test;

import static org.junit.Assert.*;

public class SmaSunnyBoy15ClientTest {

    @Test
    public void getSID(){
        var client = new SmaSunnyBoy15Client("192.168.0.1", "testPass");

        System.out.println(client.getSID());

    }

    @Test
    public void getPayload(){
        var client = new SmaSunnyBoy15Client("192.168.0.1", "testPass");

        System.out.println(client.getPayload());

    }

}