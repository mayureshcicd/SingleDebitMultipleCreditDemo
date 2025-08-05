package com.example.demo.controller;

import com.example.demo.dto.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/test")
public class ConcurrentController {

    @Autowired
    private RestTemplate restTemplate;
    private static final String targetUrl = "http://localhost:8080/transactions/concurrent-debit-credit";

    @PostMapping("/concurrent-single-debit-and-multiple-credit")
    public List<String> testConcurrentTransactions(@RequestParam int count,
                                                   @RequestBody TransactionRequest request) {

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(count, 50));
        // maximum 50 thread  will be created to handle concurrent requests
        /**
            count → some number you provide (e.g., the number of tasks or concurrent requests you expect).

            Math.min(count, 50) → picks the smaller between count and 50.

            newFixedThreadPool(n) → creates a pool with exactly n threads.

            So in plain English:

            If count is less than or equal to 50, you’ll get exactly count threads.

            If count is more than 50, you’ll still get 50 threads max (because of the Math.min).
        * */
        List<Future<String>> futures = new ArrayList<>();  // We are storing all responses of restTemplate.postForEntity

        // We'll post the same TransactionRequest object; RestTemplate + Jackson will handle serialization.
        for (int i = 0; i < count; i++) {
            futures.add(executor.submit(() -> {
                try {
                    // Use HttpEntity so we can set headers if needed
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<TransactionRequest> entity = new HttpEntity<>(request, headers);

                    ResponseEntity<String> resp = restTemplate.postForEntity(targetUrl, entity, String.class);
                    return resp.getStatusCode() + " - " + resp.getBody();
                } catch (Exception e) {
                    return "FAILED - " + e.getClass().getSimpleName() + ": " + e.getMessage();
                }
            }));
        }

        List<String> results = new ArrayList<>();  // Here we are  storing all responses from  futures.
        for (Future<String> f : futures) {
            try {
                results.add(f.get()); // waits for each to finish
            } catch (InterruptedException | ExecutionException e) {
                results.add("ERROR - " + e.getMessage());
            }
        }

        executor.shutdown();
        return results;
    }
}
