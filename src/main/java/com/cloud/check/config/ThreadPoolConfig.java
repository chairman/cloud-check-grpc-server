package com.cloud.check.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
   @Value("${thread.pool.core.size}")
   private Integer coreSize;

   @Value("${thread.pool.max.size}")
   private Integer maxSize;

   @Bean(value = "taskThreadPool")
   public ExecutorService buildTaskThreadPool(){
      ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("detect-task-thread-%d").build();
      return new ThreadPoolExecutor(coreSize,maxSize,0L, TimeUnit.MICROSECONDS,
              new ArrayBlockingQueue<Runnable>(100000),
              namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
   }
}
