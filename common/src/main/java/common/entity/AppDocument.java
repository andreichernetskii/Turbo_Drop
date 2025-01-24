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
import javax.persistence.OneToOne;

@Getter
@Setter
@EqualsAndHashCode( exclude = "id" )
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AppDocument {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    private String telegramFileId;

    private String docName;

    @OneToOne
    private BinaryContent binaryContent;

    private String mimeType;

    private Long fileSize;
}
