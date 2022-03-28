package utils;

import java.io.Serializable;

public enum Command implements Serializable {

    REG,
    REG_OK,
    LOGIN_TAKEN,
    AUTH,
    AUTH_OK,
    POST_CARRIER,
    HELLO,
    GET_ROOT,
    GET_FILENAME_LIST,
    POST_FILENAME_LIST,
    OPEN_DIRECTORY,
    UP_DIRECTORY,
    GET_LIST,
    POST_FILE,
    GET_NEXT_PART,
    DOWNLOAD_FILE,
    FILE_FROM_SERVER,
    DISCONNECT,
    TEST,
    COMMAND2,
    COMMAND3

}