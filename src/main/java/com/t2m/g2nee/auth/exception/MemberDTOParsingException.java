package com.t2m.g2nee.auth.exception;

public class MemberDTOParsingException extends RuntimeException{
        public static final String MESSAGE = "유저 정보를 파싱할 수 없습니다";

            public MemberDTOParsingException(){
            super(MESSAGE);
            }
}
