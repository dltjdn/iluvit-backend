package FIS.iLUVit.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

//    @Configuration : Spring 설정 관련 Class로 @Component 등록되어 Scanning 될 수 있다.
//    @EnableAsync : Spring method에서 비동기 기능을 사용가능하게 활성화 한다.
//            CorePoolSize : 기본 실행 대기하는 Thread의 수**
//    MaxPoolSize : 동시 동작하는 최대 Thread의 수
//    QueueCapacity : MaxPoolSize 초과 요청에서 Thread 생성 요청시,
//    해당 요청을 Queue에 저장하는데 이때 최대 수용 가능한 Queue의 수,
//    Queue에 저장되어있다가 Thread에 자리가 생기면 하나씩 빠져나가 동작
//    ThreadNamePrefix : 생성되는 Thread 접두사 지정

    /**
     * 해당 풀 설정을 한 이유 찾기...
     * why?
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("ILovIt_Async");
        executor.initialize();
        return executor;
    }


}

