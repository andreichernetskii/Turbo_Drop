package node.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.telegram.telegrambots.meta.api.objects.Update;


@Getter
@Setter
@EqualsAndHashCode( exclude = "id" )
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Convert(attributeName = "jsonb", converter = JsonBinaryType.class)
@Entity
public class RawData {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE )
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column( columnDefinition = "jsonb" )
    private Update event;
}
