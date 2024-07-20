package com.possible.eventbooking.cron;

import com.possible.eventbooking.dto.EmailDto;
import com.possible.eventbooking.model.Event;
import com.possible.eventbooking.model.Reservation;
import com.possible.eventbooking.repository.EventRepository;
import com.possible.eventbooking.repository.ReservationRepository;
import com.possible.eventbooking.util.EmailServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventNotificationTask {


    private final EventRepository eventRepository;

    private final ReservationRepository reservationRepository;

    private final EmailServiceUtil emailService;

//    @Scheduled(cron = "0 0 9 * * ?")
    @Scheduled(cron = "*/5 * * * * ?")
    public void sendEventNotifications() {
        LocalDate today = LocalDate.now();
        List<Event> upcomingEvents = eventRepository.findAllByDate(today.plusDays(1));

        for (Event event : upcomingEvents) {
            List<Reservation> reservations = reservationRepository.findAllById(Collections.singleton(event.getId()));
            for (Reservation reservation : reservations) {
                EmailDto mailDto = EmailDto.builder()
                        .toAddress(List.of(reservation.getUser().getEmail()))
                        .content("Dear " + reservation.getUser().getName() + ",\n\nThis is a reminder that you have an upcoming event: " + event.getName() + " on " + event.getDate() + ".")
                        .subject("Reminder: Upcoming Event")
                        .build();
                emailService.sendEmail(mailDto);
            }
        }
    }

}

