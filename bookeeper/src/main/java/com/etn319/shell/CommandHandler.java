package com.etn319.shell;

public interface CommandHandler {
    String count();
    String get(long id);
    String getAll();
    String save();
    String delete(long id);
    String clearCache();
}
