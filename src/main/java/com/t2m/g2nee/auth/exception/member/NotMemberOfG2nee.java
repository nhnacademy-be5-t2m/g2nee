package com.t2m.g2nee.auth.exception.member;

public class NotMemberOfG2nee extends RuntimeException {
    public static final String Message = "g2nee의 회원이 아닙니다, 회원가입 바랍니다";

    public NotMemberOfG2nee() {
        super(Message);
    }


}
