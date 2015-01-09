package com.hp.livepaper;

@SuppressWarnings("serial")
public class LivePaperException extends Exception {
  public LivePaperException(String string) {
    super(string);
  }
  public LivePaperException(String string, Throwable e) {
    super(string, e);
  }
}