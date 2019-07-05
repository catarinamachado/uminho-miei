package Neo4j;

import java.util.*;

public abstract class Neo4JDataFormat{

    private final String name;
    private final Map<String,String> atributes = new HashMap();

    Neo4JDataFormat(String name){
        this.name = name;
    }

    Map<String,String> get_atributes(){
        return new HashMap<>(atributes);
    }

    String getType(){
        return this.name;
    }

    public abstract String createCommand();

    public void addAtribute(String type, String attribute){
        atributes.put(type,attribute);
    }


}
