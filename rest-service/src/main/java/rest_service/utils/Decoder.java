package rest_service.utils;

import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Decoder {

    private final Hashids hashids;

    public Long idOf( String value ) {

        long[] result = hashids.decode( value );

        return (result != null && result.length > 0) ? result[0] : null;
    }
}
