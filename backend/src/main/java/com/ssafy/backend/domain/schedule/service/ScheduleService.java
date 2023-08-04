package com.ssafy.backend.domain.schedule.service;

import com.ssafy.backend.domain.couple.Couple;
import com.ssafy.backend.domain.couple.repository.CoupleRepository;
import com.ssafy.backend.domain.reservation.Reservation;
import com.ssafy.backend.domain.reservation.service.ReservationService;
import com.ssafy.backend.domain.schedule.Schedule;
import com.ssafy.backend.domain.schedule.dto.ReservationScheduleRegistDto;
import com.ssafy.backend.domain.schedule.dto.ScheduleModifyDto;
import com.ssafy.backend.domain.schedule.dto.ScheduleRegistDto;
import com.ssafy.backend.domain.schedule.dto.ScheduleResultDto;
import com.ssafy.backend.domain.schedule.repository.ScheduleRepository;
import com.ssafy.backend.domain.user.User;
import com.ssafy.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;

    public void registSchedule(ScheduleRegistDto scheduleRegistDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("JWT token: 회원 이메일에 해당하는 회원이 없습니다."));

        Schedule schedule = new Schedule(
                scheduleRegistDto.getTitle(),
                scheduleRegistDto.getContent(),
                scheduleRegistDto.getScheduleDate(),
                scheduleRegistDto.getScheduleTime(),
                scheduleRegistDto.getScheduledBy(),
                scheduleRegistDto.getScheduleType()
        );
        Couple couple = user.getCouple();
        schedule.setCouple(couple);

        scheduleRepository.save(schedule);
    }

    public List<ScheduleResultDto> getAllSchedule() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("JWT token: 회원 이메일에 해당하는 회원이 없습니다."));
        Long coupleId = user.getCouple().getId(); //여기 커플에서 바로 얻기 가능
        //결과값도 dto로 바꿔주기
        List<Schedule> schedules = scheduleRepository.findAllByCoupleId(coupleId);
        List<ScheduleResultDto> result = new ArrayList<>();
        for (Schedule schedule : schedules) {
            ScheduleResultDto scheduleDto = new ScheduleResultDto(
                    schedule.getId(),
                    schedule.getTitle(),
                    schedule.getContent(),
                    schedule.getScheduleDate(),
                    schedule.getScheduleTime(),
                    schedule.getScheduledBy(),
                    schedule.getScheduleType()
            );
            result.add(scheduleDto);
        }
        return result;
    }

    public int modifySchedule(ScheduleModifyDto scheduleModifyDto) {
        //jpa.. 이용해서 해당 스케줄 아이디로 스케줄 객체 찾아서 set으로 변경? -- 이게맞나
        Schedule originalSchedule = scheduleRepository.findById(scheduleModifyDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("일정 아이디에 해당하는 일정이 없습니다."));

        //맞나
        originalSchedule.update(scheduleModifyDto);
        return 1; //일단
    }

    public int deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
        return 1; //일단
    }

    public ScheduleResultDto getOneSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정 아이디에 해당하는 일정이 없습니다."));

        ScheduleResultDto scheduleDto = new ScheduleResultDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getScheduleDate(),
                schedule.getScheduleTime(),
                schedule.getScheduledBy(),
                schedule.getScheduleType()
        );
        return scheduleDto;
    }

    public void registReservationSchedule(ReservationScheduleRegistDto reservationScheduleRegistDto) {
        Schedule schedule = new Schedule(
                reservationScheduleRegistDto.getTitle(),
                reservationScheduleRegistDto.getContent(),
                reservationScheduleRegistDto.getScheduleDate(),
                reservationScheduleRegistDto.getScheduleTime(),
                reservationScheduleRegistDto.getScheduledBy(),
                reservationScheduleRegistDto.getScheduleType(),
                reservationScheduleRegistDto.getReservationId()
        );

        //커플도 등록
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName()); //이메일
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("JWT token: 회원 이메일에 해당하는 회원이 없습니다."));
        Couple couple = user.getCouple();

        schedule.setCouple(couple);
        scheduleRepository.save(schedule);
    }

    public void deleteReservationSchedule(Long reservationId) {
        //??
        Schedule schedule = scheduleRepository.findByReservationId(reservationId);
        scheduleRepository.delete(schedule);
    }
}
