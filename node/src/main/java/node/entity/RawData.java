package node.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Getter
@Setter
@EqualsAndHashCode( exclude = "id" )
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
@Entity
public class RawData {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Type( type = "jsonb" )
    @Column( columnDefinition = "jsonb" )
    private Update event;
}
