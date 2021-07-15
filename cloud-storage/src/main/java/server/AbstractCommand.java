package server;

import server.CommandType;

import java.io.Serializable;

public abstract class AbstractCommand implements Serializable {
    public abstract CommandType getType();

}
