package common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@EqualsAndHashCode( exclude = "id" )
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
