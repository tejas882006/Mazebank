package com.mazebank.util;

import com.mazebank.exceptions.InsufficientFundsException;
import com.mazebank.exceptions.InvalidTransactionException;
import com.mazebank.service.TransactionService;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Simple background processor demonstrating multithreading using a queue.
 */
public class TransactionProcessor implements AutoCloseable {
    private final TransactionService service = new TransactionService();
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Thread worker;
    private volatile boolean running = true;

    public TransactionProcessor() {
        worker = new Thread(() -> {
            while (running) {
                try {
                    Runnable task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    if (!running) break;
                    Thread.currentThread().interrupt();
                }
            }
        }, "txn-processor");
        worker.setDaemon(true);
        worker.start();
    }

    public void submitTransfer(int fromAccountId, int toAccountId, BigDecimal amount, String description) {
        queue.add(() -> {
            try {
                service.transfer(fromAccountId, toAccountId, amount, description);
                System.out.println("Transfer completed: " + amount + " from " + fromAccountId + " to " + toAccountId);
            } catch (InvalidTransactionException | InsufficientFundsException ex) {
                System.err.println("Transfer failed: " + ex.getMessage());
            } catch (Exception ex) {
                System.err.println("Unexpected error during transfer: " + ex.getMessage());
            }
        });
    }

    @Override
    public void close() {
        shutdown();
    }

    public void shutdown() {
        running = false;
        worker.interrupt();
    }
}
