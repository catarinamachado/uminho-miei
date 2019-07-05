package main.server.contract;

import main.server.imp.scheduler.exceptions.RequestNotFoundException;

public interface MachineType<K1> extends Unique<Integer> {

    void bid(Appropriator user, Double amount);
    void reserve(Appropriator user);
    void release(Long request) throws RequestNotFoundException;

}