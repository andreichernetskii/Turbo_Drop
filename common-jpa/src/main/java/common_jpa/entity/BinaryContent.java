package common_jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BinaryContent {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;
    private byte[] fileAsArrayOfBytes;
}
