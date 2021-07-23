package server;

import java.io.Serializable;

public enum CommandType implements Serializable {
    FILE_REQUEST,
    FILE_MESSAGE,
    LIST_REQUEST,
    LIST_MESSAGE,
    DELETE_REQUEST,
    RENAME_REQUEST,
    PATH_REQUEST,
    PATH_UP,
    PATH_RESPONSE
}
