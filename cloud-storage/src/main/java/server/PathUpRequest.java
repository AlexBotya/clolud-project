package server;

public class PathUpRequest extends AbstractCommand{
    @Override
    public CommandType getType() {
        return CommandType.PATH_UP;
    }
}
