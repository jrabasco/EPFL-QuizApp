package epfl.sweng.context.conn_state;

import epfl.sweng.context.ConnectionEvent;


public class ServerSyncConnectionState extends ConnectionState {
    @Override
    public ConnectionState getNextState(ConnectionEvent event) throws UnknownTransitionException {
        switch (event.getType()) {
            case COMMUNICATION_ERROR:
                return new IdleOfflineConnectionState();
            case COMMUNICATION_SUCCESS:
                return new IdleOnlineConnectionState();
            default:
                throw new UnknownTransitionException(event);
        }
    }

    @Override
    public boolean isOnline() {
        return false;
    }
}
