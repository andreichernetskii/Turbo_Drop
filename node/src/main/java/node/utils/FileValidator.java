package node.utils;

public class FileValidator {

    // max size of file available for demo user
    private static final long MAX_FILE_SIZE_FOR_DEMO_MB = 5;

    // max size of file available for demo user in bytes
    private static final long MAX_FILE_SIZE_FOR_DEMO_BYTES = MAX_FILE_SIZE_FOR_DEMO_MB * 1024 * 1024;

    public static boolean isFileSizeValid(Long fileSize) {
        return fileSize <= MAX_FILE_SIZE_FOR_DEMO_BYTES;
    }
}
