package com.ishaqkhan.reactivefluxclient;

import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SpringBootApplication
public class ReactiveFluxClientApplication {

	@Bean
	WebClient client() {
		return WebClient.create();
	}
	
	@Bean
	CommandLineRunner demo(WebClient client) {
		return args -> {
			client.get().uri("http://localhost:8080/movies")
						.exchange()
						.flatMapMany(clientResponse -> clientResponse.bodyToFlux(Movie.class))
						.filter(movie -> movie.getTitle().toLowerCase().contains("lambdas".toLowerCase()))
						.subscribe(movie -> client.get()
											.uri("http://localhost:8080/movies/{id}/events", movie.getId())
											.exchange()
											.flatMapMany(cr -> cr.bodyToFlux(MovieEvent.class))
											.subscribe(System.out::println));
		};
	}
	public static void main(String[] args) {
		SpringApplication.run(ReactiveFluxClientApplication.class, args);
	}

}

@AllArgsConstructor
@ToString
@NoArgsConstructor
@Data
class Movie{
	
	private String id;
	private String title;
	private String genre;
	private String director;
	private Double rating;
}

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
class MovieEvent{
	private Movie movie;
	private Date when;
	private String user;
}