import Neo4j.Neo4JDataFormat;
import Neo4j.Neo4JNode;
import Neo4j.Neo4JRelation;
import Neo4j.Neo4JWriter;
import RelationalDB.EventsWorkbenchGetter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Migrator {

    private static Neo4JWriter neow;

    public static void main(String[] args){

        try {
            if(args.length < 6 ){
                System.out.println(" Insufficient number of arguments ");
                System.out.println("arguments should be :");
                System.out.println("<sqluser> <sqlpassword> <sqlip> <neo4juser> <neo4jpassword> <neo4jip>");
                System.out.println("user sqluserpw localhost:3306 user neo4juserpw localhost:11001");
                return;
            }

            String sqluser = args[0];
            String sqlpassword = args[1];
            String sqlip = args[2];
            String neo4juser = args[3];
            String neo4jpassword = args[4];
            String neo4jip = args[5];

            EventsWorkbenchGetter wb = new EventsWorkbenchGetter(sqluser,sqlpassword,sqlip);
            neow = new Neo4JWriter(neo4juser,neo4jpassword,neo4jip,10);

            List<String> eventlist = new ArrayList<>();
            eventlist.add("id");eventlist.add("nome");//eventlist.add("preco");
            transfer_table(eventlist, wb.evento(),"Evento");

            List<String> orglist = new ArrayList<>();
            orglist.add("id");orglist.add("nome");orglist.add("email");
            transfer_table(orglist, wb.organizador(),"Organizador");

            List<String> divlist = new ArrayList<>();
            divlist.add("id");divlist.add("tipo");divlist.add("preco");
            transfer_table(divlist, wb.divulgacao(),"Divulgacao");

            List<String> partlist = new ArrayList<>();
            partlist.add("id");partlist.add("nome");partlist.add("email");partlist.add("telemovel");
            partlist.add("genero");partlist.add("nif");partlist.add("DataDeNascimento");
            transfer_table(partlist,wb.participante(),"Participante");

            neow.halt();

            List<String> participalist = new ArrayList<>();
            participalist.add("preco");
            transfer_relationship(participalist, wb.participa(),
                    "Participa", "Participante",
                    "Evento");

            transfer_relationship(new ArrayList<>(), wb.organiza(),
                    "Organiza", "Organizador",
                    "Evento");

            transfer_relationship(new ArrayList<>(), wb.divulga(),
                    "Divulga", "Divulgacao",
                    "Evento");

            transfer_relationship(new ArrayList<>(), wb.influencia(),
                    "Influencia", "Divulgacao",
                    "Participante");

            wb.termina();
            neow.termina();

        }catch(SQLException|InterruptedException a ){
            System.out.println(a.getMessage());

        }catch(ClassNotFoundException b){
            System.out.println(" Driver error ");
        }
    }

    private static void transfer_relationship(List<String> atributes, ResultSet table,
                                              String relationship, String from ,
                                              String to) throws SQLException{
        while(table.next())
            neow.queue(fillrelationship(atributes ,table ,relationship , from ,to ));
    }

    private static void transfer_table(List<String> atributes, ResultSet table,
                                       String nodetype) throws SQLException{
        while(table.next())
            neow.queue(fillnode(atributes, table, nodetype));
    }

    private static Neo4JRelation fillrelationship(List<String> atributes,
                                                  ResultSet table, String relationship, String from,
                                                  String to) throws SQLException{

        Neo4JRelation rel = new Neo4JRelation(relationship,
                from, table.getString(1) ,
                to, table.getString(2));

        int col = table.getMetaData().getColumnCount();

        for (int i = 3; i <= col; i++)
            rel.addAtribute(atributes.get(i - 3), table.getString(i));

        return rel;
    }

    private static Neo4JNode fillnode(List<String> atributes,
                                      ResultSet table, String node) throws SQLException{
        Neo4JNode neo = new Neo4JNode(node);
        int col = table.getMetaData().getColumnCount();

        for (int i = 1; i <= col; i++)
            if(atributes.get(i - 1).equals("id") || atributes.get(i - 1).equals("preco"))
                neo.addAtribute(atributes.get(i - 1), table.getString(i));
            else
                neo.addAtribute(atributes.get(i - 1), " '" + table.getString(i) + "' ");


        return neo;
    }
}
