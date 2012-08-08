package org.dummycreator;

class UsedInfo<T> {
    private T instance = null;
    private boolean populated = false;

    public T getInstance() {
	return instance;
    }

    public void setInstance(T instance) {
	this.instance = instance;
    }

    public boolean isPopulated() {
	return populated;
    }

    public void setPopulated(boolean populated) {
	this.populated = populated;
    }
}