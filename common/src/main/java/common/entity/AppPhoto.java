package common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode( exclude = "id" )
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AppPhoto {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    private String telegramFileId;

    @OneToOne
    private BinaryContent binaryContent;

    private Integer fileSize;
}
