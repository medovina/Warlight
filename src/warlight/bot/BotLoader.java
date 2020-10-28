package warlight.bot;

public interface BotLoader {
    Class<?> load(String botFQCN) throws ClassNotFoundException;
}
