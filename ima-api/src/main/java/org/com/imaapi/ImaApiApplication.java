package org.com.imaapi;

import org.com.imaapi.service.CalendarioGoogleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.google.api.services.calendar.model.Event;

import java.util.List;

@SpringBootApplication
public class ImaApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImaApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            try {
                var service = CalendarioGoogleService.getCalendarService();
                List<Event> events = CalendarioGoogleService.getUpcomingEvents(service, 10);
                CalendarioGoogleService.printUpcomingEvents(events);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}