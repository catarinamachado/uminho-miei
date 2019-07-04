package Common;

public enum ConnectionType {
    PUT,        // PUT FILES
    GET,        // GET FILES
    LIST,       // LIST FILES AVAILABLE
    INFORM,     // REPLY WITH AVAILABLE FILES
    ASK,        // ASK WHO HAS FILE
    FRAG,       // FRAGMENT FROM FILE
    SHARE       // TELL PEERS I HAVE FILE
}
