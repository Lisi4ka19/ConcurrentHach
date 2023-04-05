package com.aniskina.alisa.concurrenthash.concurrenthash;

import dto.DataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@Slf4j
public class ConcurrentHashApplication {

    private static ConcurrentHashMap<String, DataDTO> hashMap = new ConcurrentHashMap<>();

    private static List<String> keys = Arrays.asList("one", "two", "three", "for", "five");

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(11);

        SetTask setTask = new SetTask();
        executor.execute(setTask);

        for (int i = 0; i < 10; i++) {
            ReaderTask worker = new ReaderTask();
            executor.execute(worker);
        }

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input = "";
            try {
                input = br.readLine().toLowerCase().trim();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (input.equals("kill")) {
                executor.shutdownNow();
                break;
            }
        }
    }

    static class ReaderTask implements Runnable {

        @Override
        public void run() {

            while (true) {
                try {
                    int i = (int) (Math.random() * 5);
                    if (hashMap.get(keys.get(i)) != null) {
                        log.info(hashMap.get(keys.get(i)).toString());
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    printFinishLog();
                    break;
                }
            }
        }
    }

    static class SetTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    int price = (int) (Math.random() * 100);
                    keys.stream().forEach(key -> {
                        hashMap.put(key, DataDTO.builder().value(key).price(price).build());
                    });
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
					printFinishLog();
                    break;
                }
            }
        }
    }

	static void printFinishLog(){
		log.info(String.format("Thread %s was finished", Thread.currentThread().getName()));
	}

}
