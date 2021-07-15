package server;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ToString
@Getter
public class FileMessage extends AbstractCommand{

    public FileMessage(Path path) throws IOException {
        name = path.getFileName().toString();
        size = Files.size(path);
        data = Files.readAllBytes(path);  //подуумать как разбить файл на части
    }

    private final String name;
    private final long size;
    private final byte[] data;

    @Override
    public CommandType getType() {

        return CommandType.FILE_MESSAGE;
    }
}
