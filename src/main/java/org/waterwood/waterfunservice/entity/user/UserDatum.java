package org.waterwood.waterfunservice.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_data")
@NoArgsConstructor
public class UserDatum {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "email")
    private String email;

    @ColumnDefault("0")
    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Column(name = "phone", length = 20)
    private String phone;

    @ColumnDefault("0")
    @Column(name = "phone_verified")
    private Boolean phoneVerified;

    @Column(name = "encryption_key_id", nullable = false, length = 50)
    private String encryptionKeyId;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public UserDatum(User user, String email, String phone, String encryptionKeyId) {
        this.user = user;
        this.id = user.getId();
        this.email = email;
        this.phone = phone;
        this.encryptionKeyId = encryptionKeyId;
        this.emailVerified = false;
        this.phoneVerified = false;
    }
}