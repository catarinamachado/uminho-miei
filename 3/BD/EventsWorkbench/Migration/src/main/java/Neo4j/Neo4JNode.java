package Neo4j;

import java.util.Map.Entry;
/**
 * Classe Generica para criar script de criaçao de nó no Neo4j
 * */
public class Neo4JNode extends Neo4JDataFormat{

    public Neo4JNode(String type) {
        super(type);
    }

    public String createCommand(){
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE (:");
        sb.append(this.getType());
        sb.append(" {");
        boolean comma = false;
        for (Entry<String,String> e: get_atributes().entrySet()) {
            if(!comma)
                comma = true;
            else
                sb.append(",");
            sb.append(e.getKey());
            sb.append(":");
            sb.append(e.getValue());
        }
        sb.append("})");
        return sb.toString();
    }

}
