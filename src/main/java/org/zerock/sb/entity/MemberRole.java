package org.zerock.sb.entity;

import lombok.*;

import javax.persistence.Embeddable;

//@Embeddable
//@Getter
//@ToString
public enum MemberRole {

    USER, STORE, ADMIN;
    //일반, 판매자, 관리자
}
