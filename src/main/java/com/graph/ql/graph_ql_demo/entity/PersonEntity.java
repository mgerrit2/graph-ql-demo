package com.graph.ql.graph_ql_demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("java:s125")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@SQLDelete(sql = "UPDATE person SET active = false WHERE id = ? AND record_version = ?")
@SQLRestriction("active = true")
@Entity
@Table(name = "person")
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "first_name", length = 50)
    private String firstname;

    @Column(name = "last_name", length = 50)
    private String lastname;


    @Column(name = "active")
    private boolean active;

    @Column(
            name = "email",
            length = 255
    )
    @Pattern(regexp = "^$|^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email")
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", length = 50, nullable = true, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy; // Tracks who soft-deleted or edited the record



    @Builder.Default
    @Version
    @Column(name = "record_version")
    private long version = 0L;


    //region getters
    public Optional<String> getFirstname(){
        return Optional.ofNullable(this.firstname);
    }

    public Optional<String> getLastname(){
        return Optional.ofNullable(this.lastname);
    }

    public Optional<String> getMail(){return Optional.ofNullable(this.email);}

    public Optional<OffsetDateTime> getCreatedAt() {
        // Since 'creates_at' is 'nullable = false' and managed by @CreatedDate,
        // it should always have a value after persistence.
        return Optional.ofNullable(this.createdAt);
    }

    public Optional<String> getCreatedBy() {
        // Since 'created_by' is 'nullable = false' and managed by @CreatedBy,
        // it should always have a value after persistence.
        return Optional.ofNullable(this.createdBy);
    }

    public Optional<OffsetDateTime> getUpdatedAt() {
        // 'updated_at' is 'nullable = false', so it should always have a value.
        return Optional.ofNullable(this.updatedAt);
    }

    public Optional<String> getLastModifiedBy() {
        return Optional.ofNullable(this.lastModifiedBy);
    }

    //endregion

    /**
     * this will print the class fields with newline for each field
     *
     * @return
     */
    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .toString();
    }

    //region Hash and equals code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonEntity personEntity)) return false;
        return Objects.equals(this.id, personEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
    //endregion

}
