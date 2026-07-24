package com.votrebanque.domain.model;

public abstract class AbstractEntity<ID> {

    //protected abstract ID id();
    public abstract ID id();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity<?> other = (AbstractEntity<?>) o;
        return id() != null && id().equals(other.id());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
