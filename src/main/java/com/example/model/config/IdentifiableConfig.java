package com.example.model.config;

public abstract class IdentifiableConfig {
    public String id;

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;

        return hashCode() == obj.hashCode();
    }
}
