package common_jpa.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode( exclude = "id" )
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AppPhoto {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;
    private String telegramFileId;
    @OneToOne
    private BinaryContent binaryContent;
    private Integer fileSize;
}
