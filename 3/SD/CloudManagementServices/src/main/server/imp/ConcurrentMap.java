package main.server.imp;

import main.exception.*;


import java.util.HashMap;
import java.util.Map;


class ConcurrentMap<K,V> {

    private final Map<K, V> users;

    private final RWLock rl = new RWLock();

    public ConcurrentMap() {
        this.users = new HashMap<>();
    }

    public void put(K key ,V user) throws UserAlreadyExistsException {
        this.rl.writeLock();
        try {
            if (!this.users.containsKey(key)) {
                this.users.put(key, user);
            } else {
                throw new UserAlreadyExistsException();
            }
        }finally {
            this.rl.writeUnlock();
        }
    }

    public V remove(K key) throws UserNotFoundException {
        this.rl.writeLock();
        try {
            Double s;

            if (!this.users.containsKey(key))
                throw new UserNotFoundException();

            return this.users.remove(key);
        }finally {
            this.rl.writeUnlock();
        }
    }

    public boolean contains(K key) {
        this.rl.readLock();
        try {
            return this.users.containsKey(key);
        }finally {
            this.rl.readUnlock();
        }
    }

    public V get(K key) throws UserNotFoundException {
        this.rl.readLock();
        try {
            if (!this.users.containsKey(key))
                throw new UserNotFoundException();

            return this.users.get(key);
        }finally {
            this.rl.readUnlock();
        }
    }

}