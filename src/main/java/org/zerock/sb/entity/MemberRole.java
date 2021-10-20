package org.zerock.sb.entity;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Embeddable;

public enum MemberRole {

    USER, STORE, ADMIN;
}
