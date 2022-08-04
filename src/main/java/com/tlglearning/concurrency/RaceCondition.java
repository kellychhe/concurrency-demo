package com.tlglearning.concurrency;

import java.util.LinkedList;
import java.util.List;

public class RaceCondition implements Computation {

  private static final int NUM_THREADS = 4;
  private double logSum;
  @Override
  public double arithmeticMean(int[] data) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public double geometricMean(int[] data) {
    int slice = data.length / NUM_THREADS;
    List<Thread> workers = new LinkedList<>();
    for (int i = 0; i < NUM_THREADS; i++) {
      workers.add(spawn(data, i * slice, (i + 1) * slice));
    }
    for (Thread worker : workers) {
      try {
        worker.join();
      } catch (InterruptedException ignored) {
        //ignore
      }
    }
    return Math.exp(logSum / data.length);
  }

  private Thread spawn (int[] data, int startIndex, int endIndex) {
    Runnable work = () -> {
      for (int i = startIndex; i < endIndex; i++) {
        logSum += Math.log(data[i]);
      }
    };
    Thread worker = new Thread(work);
    worker.start();
    return worker;
  }


}
