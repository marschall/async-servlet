package com.github.marschall.asyncservlet;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "asyncServlet", urlPatterns= "/asyncServlet", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
  
  private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/plain");
    CountDownLatch latch = new CountDownLatch(1);
    
    
    AsyncContext context = req.startAsync();
    LOG.info("sync work in" + Thread.currentThread().getName());
    context.start(() -> {
      LOG.info("async work in" + Thread.currentThread().getName());
      try {
        latch.await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LOG.warning("interrupted");
      }
    });
    LOG.info("count down");
    latch.countDown();
    
    context.getResponse().getWriter().print("OK");
    context.complete();
  }
  

}
