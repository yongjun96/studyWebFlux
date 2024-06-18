package study.webflux;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

@Slf4j
public class HelloReactor {

    public static void main (String[] args){

        System.out.println("-----------------------------------1번---------------------------------------------------");

        // Mono 예제
        Mono.just("Hello Reactor!!")
                .subscribe(message -> System.out.println(message));


        System.out.println("-----------------------------------2번---------------------------------------------------");

        // Mono emit error signal 예제
        Mono.empty()
                .subscribe(
                        // UpStream 에서 emit 된 data 를 전달
                        message -> log.info("{}", message),
                        // Upstream 에서 error 발생 시 onError signal 전달
                        error -> {},
                        // Upstream 에서 onComplete signal 전달
                        () -> log.info("emit onComplete signal")
                );

        System.out.println("-----------------------------------3번---------------------------------------------------");

        // Flux 예제
        Flux<String> sequence = Flux.just("Hello", "Reactor");
        sequence.map(data -> data.toLowerCase())
                .subscribe(data -> log.info(data));


        System.out.println("-----------------------------------4번---------------------------------------------------");

        URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
                .host("worldtimeapi.org")
                .port(80)
                .path("/api/timezone/Asia/Seoul")
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Mono.just(restTemplate.exchange(worldTimeUri, HttpMethod.GET, new HttpEntity<String>(headers), String.class))
                .map(response -> {
                    DocumentContext jsonContext = JsonPath.parse(response.getBody());
                    String dateTime = jsonContext.read("$.datetime");
                    return dateTime;
                })
                .subscribe(
                        data -> log.info("emit data : "+ data),
                        error -> {},
                        () -> log.info("emit onComplete signal")
                );

    }

}
