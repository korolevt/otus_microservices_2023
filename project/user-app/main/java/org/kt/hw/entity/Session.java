package org.kt.hw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="user_id")
    private Integer userId;

    @Column(name="token")
    private UUID token;

    @Column(name="user_agent")
    private String userAgent;

    @Column(name="ip_address")
    private String ipAddress;

    @Column(name="expires")
    private Instant expires;

    @Column(name="created")
    private Instant created;

}


