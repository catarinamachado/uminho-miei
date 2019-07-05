package Neo4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Classe Generica para criar script de criaçao de arcos no Neo4j
 * */
public class Neo4JRelation extends Neo4JDataFormat{
    private String originNodeType;
    private String idOri;
    private String destNodeType;
    private String idDest;

    public Neo4JRelation(String reltype, String originNodeType, String idOri, String destNodeType, String idDest) {
        super(reltype);
        this.originNodeType = originNodeType;
        this.idOri = idOri;
        this.destNodeType = destNodeType;
        this.idDest = idDest;
    }

    public String createCommand(){
        StringBuilder sb = new StringBuilder();
        sb.append("MATCH (ori:" +originNodeType+ " {id:"+idOri+"})\n");
        sb.append("MATCH (dest:" +destNodeType+ " {id:"+idDest+"})\n");
        sb.append("Create (ori) -[:" +getType() +"");
        if (get_atributes().size() > 0){
            sb.append(" {") ;
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
            sb.append("}");
        }
        sb.append("]-> (dest)");
        return sb.toString();
    }

}
