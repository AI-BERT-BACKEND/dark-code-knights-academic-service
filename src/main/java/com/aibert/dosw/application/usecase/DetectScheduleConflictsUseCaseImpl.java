package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.ScheduleParser;
import com.aibert.dosw.domain.model.ScheduleConflict;
import com.aibert.dosw.domain.model.ScheduleSlot;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.DetectScheduleConflictsUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetectScheduleConflictsUseCaseImpl implements DetectScheduleConflictsUseCase {

    private final SubjectRepositoryPort subjectRepository;
    private final ScheduleParser scheduleParser;

    @Override
    public List<ScheduleConflict> detect(String studentId, String semester) {
        List<Subject> subjects = subjectRepository.findByStudentIdAndSemester(studentId, semester)
                .stream()
                .sorted(Comparator.comparingLong(Subject::getId))
                .toList();
        List<ScheduleConflict> conflicts = new ArrayList<>();

        for (int i = 0; i < subjects.size(); i++) {
            for (int j = i + 1; j < subjects.size(); j++) {
                Subject a = subjects.get(i);
                Subject b = subjects.get(j);

                List<ScheduleSlot> slotsA = scheduleParser.parse(a.getSchedule());
                List<ScheduleSlot> slotsB = scheduleParser.parse(b.getSchedule());

                for (ScheduleSlot slotA : slotsA) {
                    for (ScheduleSlot slotB : slotsB) {
                        if (slotA.overlapsWith(slotB)) {
                            conflicts.add(ScheduleConflict.builder()
                                    .subjectAId(a.getId())
                                    .subjectAName(a.getSubjectName())
                                    .subjectBId(b.getId())
                                    .subjectBName(b.getSubjectName())
                                    .conflictingSlot(slotA.toDisplayString())
                                    .build());
                        }
                    }
                }
            }
        }

        return conflicts;
    }
}
