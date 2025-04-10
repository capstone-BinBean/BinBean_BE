package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.OperatingHours;
import binbean.binbean_BE.entity.BusinessHours;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.BusinessHoursRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class BusinessHoursService {

    private final BusinessHoursRepository businessHoursRepository;

    public BusinessHoursService(BusinessHoursRepository businessHoursRepository) {
        this.businessHoursRepository = businessHoursRepository;
    }

    public OperatingHours getBusinessHoursForToday(Cafe cafe) {
        BusinessHours businessHours = businessHoursRepository.findByCafeId(cafe.getId())
            .orElseThrow(() -> new NotFoundException("There is no registered businessHorus."));

        DayOfWeek today = LocalDate.now().getDayOfWeek();

        return getHoursForDay(businessHours, today);
    }

    private OperatingHours getHoursForDay(BusinessHours businessHours, DayOfWeek day) {
        String startTime;
        String endTime;

        if (day == DayOfWeek.MONDAY) {
            startTime = businessHours.getMondayStart();
            endTime = businessHours.getMondayEnd();
        } else if (day == DayOfWeek.TUESDAY) {
            startTime = businessHours.getTuesdayStart();
            endTime = businessHours.getTuesdayEnd();
        } else if (day == DayOfWeek.WEDNESDAY) {
            startTime = businessHours.getWednesdayStart();
            endTime = businessHours.getWednesdayEnd();
        } else if (day == DayOfWeek.THURSDAY) {
            startTime = businessHours.getThursdayStart();
            endTime = businessHours.getThursdayEnd();
        } else if (day == DayOfWeek.FRIDAY) {
            startTime = businessHours.getFridayStart();
            endTime = businessHours.getFridayEnd();
        } else if (day == DayOfWeek.SATURDAY) {
            startTime = businessHours.getSaturdayStart();
            endTime = businessHours.getSaturdayEnd();
        } else if (day == DayOfWeek.SUNDAY) {
            startTime = businessHours.getSundayStart();
            endTime = businessHours.getSundayEnd();
        } else {
            throw new IllegalStateException("Unexpected value: " + day);
        }

        return OperatingHours.create(startTime, endTime);
    }
}
