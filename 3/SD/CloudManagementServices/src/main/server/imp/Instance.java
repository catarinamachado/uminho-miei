package main.server.imp;

import main.server.contract.Machine;


public final class Instance implements Machine<String>{

    private final String id;

    public Instance(String id) {
        this.id = id;
    }

    public String key() {
        return this.id;
    }

}