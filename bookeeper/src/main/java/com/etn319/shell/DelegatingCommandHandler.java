package com.etn319.shell;

import lombok.Setter;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("Data Access Interface")
public class DelegatingCommandHandler implements CommandHandler {
    @Setter
    private CommandHandler delegate;

    @Override
    @ShellMethod(value = "Count objects of chosen type", key = "count")
    public String count() {
        return delegate.count();
    }

    @Override
    @ShellMethod(value = "Get object of chosen type by id and load it to cache", key = "get")
    public String get(long id) {
        return delegate.get(id);
    }

    @Override
    @ShellMethod(value = "Get all objects of chosen type", key = "get-all")
    public String getAll() {
        return delegate.getAll();
    }

    @Override
    @ShellMethod(value = "Save cached object to DB", key = "save")
    public String save() {
        return delegate.save();
    }

    @Override
    @ShellMethod(value = "Delete object of chosen type by id", key = "delete")
    public String delete(long id) {
        return delegate.delete(id);
    }

    @Override
    @ShellMethod(value = "Clear cache of chosen type", key = {"clean", "cache-clean", "cc"})
    public String clearCache() {
        return delegate.clearCache();
    }

    @Override
    @ShellMethod(value = "Get current cache object of chosen type", key = {"current", "cache", "c"})
    public String getCurrent() {
        return delegate.getCurrent();
    }
}
