package utils;

import org.hashids.Hashids;

public class CryptoTool {
    private final Hashids hashids;

    public CryptoTool( String salt ) {
        int minHashLength = 10;
        this.hashids = new Hashids( salt, minHashLength );
    }

    public String hashOf( Long value ) {
        return hashids.encode( value );
    }

    public Long idOf( String value ) {
        long[] result = hashids.decode( value );
        return ( result != null && result.length > 0 ) ? result[ 0 ] : null;
    }
}
