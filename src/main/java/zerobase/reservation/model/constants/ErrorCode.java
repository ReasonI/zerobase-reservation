package zerobase.reservation.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //STORE
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND,"등록되지 않은 상점입니다."),
    NAME_EXIST(HttpStatus.BAD_REQUEST,"이미 등록 되어있는 이름입니다."),

    //USER, PARTNER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 ID 입니다."),
    PASSWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "비밀번호가 일치하지 않습니다."),
    USER_EXIST(HttpStatus.BAD_REQUEST,"이미 사용 중인 아이디 입니다.");

    private final HttpStatus httpStatus;
    private final String description;
}
