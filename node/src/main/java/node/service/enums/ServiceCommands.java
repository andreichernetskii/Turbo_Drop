package node.service.enums;

public enum ServiceCommands {

    HELP( "/help" ),
    REGISTRATION( "/registration" ),
    CANCEL( "/cancel" ),
    START( "/start" );

    private final String cmd;

    ServiceCommands( String cmd ) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public static ServiceCommands fromValue( String value ) {
        for( ServiceCommands command: ServiceCommands.values() ) {
            if ( command.cmd.equals( value )) return command;
        }

        return null;
    }
}
