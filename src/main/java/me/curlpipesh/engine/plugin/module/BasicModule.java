package me.curlpipesh.engine.plugin.module;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.curlpipesh.engine.plugin.Plugin;

/**
 * @author c
 * @since 7/12/15
 */
public abstract class BasicModule implements Module {
    @Getter
    private final String name, description;

    @SuppressWarnings("FieldMayBeFinal")
    @Getter
    @Setter
    private String status = "Ok";

    @Getter
    private final Plugin plugin;

    public BasicModule(@NonNull final Plugin plugin, @NonNull final String name, @NonNull final String description) {
        this.name = name;
        this.description = description;
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(final boolean enabled) {}
}
