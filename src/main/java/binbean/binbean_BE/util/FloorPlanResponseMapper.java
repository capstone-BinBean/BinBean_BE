package binbean.binbean_BE.util;

import binbean.binbean_BE.dto.Position;
import binbean.binbean_BE.dto.response.FloorPlanResponse;
import binbean.binbean_BE.entity.floor_plan.BorderLine;
import binbean.binbean_BE.entity.floor_plan.Counter;
import binbean.binbean_BE.entity.floor_plan.Door;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.entity.floor_plan.Seats;
import binbean.binbean_BE.entity.floor_plan.Table;
import binbean.binbean_BE.entity.floor_plan.Toilet;
import binbean.binbean_BE.entity.floor_plan.Window;
import binbean.binbean_BE.repository.floor_plan.BorderLineRepository;
import binbean.binbean_BE.repository.floor_plan.CounterRepository;
import binbean.binbean_BE.repository.floor_plan.DoorRepository;
import binbean.binbean_BE.repository.floor_plan.SeatsRepository;
import binbean.binbean_BE.repository.floor_plan.TableRepository;
import binbean.binbean_BE.repository.floor_plan.ToiletRepository;
import binbean.binbean_BE.repository.floor_plan.WindowRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FloorPlanResponseMapper {

    private final BorderLineRepository borderLineRepository;
    private final SeatsRepository seatsRepository;
    private final DoorRepository doorRepository;
    private final CounterRepository counterRepository;
    private final ToiletRepository toiletRepository;
    private final WindowRepository windowRepository;
    private final TableRepository tableRepository;

    public FloorPlanResponseMapper(BorderLineRepository borderLineRepository, SeatsRepository seatsRepository,
        DoorRepository doorRepository, CounterRepository counterRepository, ToiletRepository toiletRepository,
        WindowRepository windowRepository, TableRepository tableRepository) {
        this.borderLineRepository = borderLineRepository;
        this.seatsRepository = seatsRepository;
        this.doorRepository = doorRepository;
        this.counterRepository = counterRepository;
        this.toiletRepository = toiletRepository;
        this.windowRepository = windowRepository;
        this.tableRepository = tableRepository;
    }

    public FloorPlanResponse floorPlanResponse(FloorPlan floorPlan) {
        List<Position> borderPositions = borderLineRepository.findByFloorPlanId(floorPlan.getId())
            .stream()
            .map(BorderLine::toPositionDto)
            .collect(Collectors.toList());

        List<Position> seatPositions = seatsRepository.findByFloorPlanId(floorPlan.getId())
            .stream()
            .map(Seats::toPositionDto)
            .collect(Collectors.toList());

        List<Position> doorPositions = doorRepository.findByFloorPlanId(floorPlan.getId())
            .stream()
            .map(Door::toPositionDto)
            .collect(Collectors.toList());

        List<Position> counterPositions = counterRepository.findByFloorPlanId(floorPlan.getId())
            .stream()
            .map(Counter::toPositionDto)
            .collect(Collectors.toList());

        List<Position> toiletPositions = toiletRepository.findByFloorPlanId(floorPlan.getId())
            .stream()
            .map(Toilet::toPositionDto)
            .collect(Collectors.toList());

        List<Position> windowPositions = windowRepository.findByFloorPlanId(floorPlan.getId())
            .stream()
            .map(Window::toPositionDto)
            .collect(Collectors.toList());

        List<Position> tablePositions = tableRepository.findByFloorPlanId(floorPlan.getId())
            .stream()
            .map(Table::toPositionDto)
            .collect(Collectors.toList());

        List<Position> currentPositions = new ArrayList<>();

        return floorPlan.toFloorPlanDto(borderPositions, seatPositions, doorPositions, counterPositions,
            toiletPositions, windowPositions, tablePositions, currentPositions);
    }
}
