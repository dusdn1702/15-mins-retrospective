package wooteco.retrospective.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.retrospective.dao.attendance.AttendanceDao;
import wooteco.retrospective.dao.attendance.TimeDao;
import wooteco.retrospective.dao.member.MemberDao;
import wooteco.retrospective.domain.attendance.Attendance;
import wooteco.retrospective.domain.attendance.Time;
import wooteco.retrospective.domain.member.Member;
import wooteco.retrospective.dto.AttendanceRequest;

@Service
public class AttendanceService {

    private final AttendanceDao attendanceDao;
    private final MemberDao memberDao;
    private final TimeDao timeDao;

    public AttendanceService(AttendanceDao attendanceDao, MemberDao memberDao, TimeDao timeDao) {
        this.attendanceDao = attendanceDao;
        this.memberDao = memberDao;
        this.timeDao = timeDao;
    }

    @Transactional
    public Attendance postAttendance(AttendanceRequest attendanceRequest) {
        validateTime(attendanceRequest);

        Member member = memberDao.findById(attendanceRequest.getMemberId())
            .orElseThrow(RuntimeException::new);
        Time time = timeDao.findById(attendanceRequest.getTimeId())
            .orElseThrow(RuntimeException::new);
        Attendance attendance = new Attendance(attendanceRequest.getDate(), member, time);

        return attendanceDao.insert(attendance);
    }

    private void validateTime(AttendanceRequest attendanceRequest) {
        if(attendanceDao.isExistSameTime(
            attendanceRequest.getMemberId(),
            attendanceRequest.getTimeId()
        )) {
            throw new IllegalArgumentException("이미 등록된 시간입니다.");
        }
    }

}