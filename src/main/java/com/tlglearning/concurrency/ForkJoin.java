package com.tlglearning.concurrency;


import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoin implements Computation {

  private static final int FORK_THRESHOLD = 10_000_000;

  private final Object lock = new Object();

  private double logSum;

  @Override
  public double arithmeticMean(int[] data) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public double geometricMean(int[] data) {
    Worker worker = new Worker(data, 0, data.length);
    ForkJoinPool pool = new ForkJoinPool();
    pool.invoke(worker);
    return Math.exp(logSum / data.length);
  }

  private Thread spawn(int[] data, int startIndex, int endIndex) {
    Runnable work = () -> {
      double logSubtotal = 0;
      for (int i = startIndex; i < endIndex; i++) {
        logSubtotal += Math.log(data[i]);
      }
      synchronized (lock) {
        logSum += logSubtotal;
      }
    };
    Thread worker = new Thread(work);
    worker.start();
    return worker;
  }

  private void update(int data) {
    double logData = Math.log(data);
    synchronized (lock) {
      logSum += logData;
    }
  }

  private class Worker extends RecursiveAction {

    private final int[] data;
    private final int startIndex;
    private final int endIndex;

    private Worker(int[] data, int startIndex, int endIndex) {
      this.data = data;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }

    @Override
    protected void compute() {
      if (endIndex - startIndex <= FORK_THRESHOLD) {
        double logSubtotal = 0;
        for (int i = startIndex; i < endIndex; i++) {
          logSubtotal += Math.log(data[i]);
        }
        synchronized (lock) {
          logSum += logSubtotal;
        }
      } else {
        int midpoint = (startIndex + endIndex) / 2;
        invokeAll(new Worker(data, startIndex, midpoint), new Worker(data, midpoint, endIndex));
      }
    }

  }


}
