package Neo4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Neo4JWriter implements Runnable{

    private AtomicBoolean operating = new AtomicBoolean(true);
    private final Queue<Neo4JDataFormat> q = new LinkedList<>();
    private final int instruction_size;
    private final Connection connection;
    private Thread th;

    public Neo4JWriter(String user, String password, String ip, int instruction_size) throws SQLException, ClassNotFoundException{
        Class.forName("org.neo4j.jdbc.bolt.BoltDriver");
        connection = DriverManager.getConnection("jdbc:neo4j:bolt://" + ip + "?username="+user+",password="+password+",routing:policy=EU");
        connection.setAutoCommit(false);
        this.instruction_size=instruction_size;
        th = new Thread(this);
        th.start();
    }

    public void queue(Neo4JDataFormat a){
        synchronized (q){
            q.add(a);
        }
    }

    public void halt() throws InterruptedException{
        operating.set(false);

        synchronized(q) {
            q.notifyAll();
        }

        th.join();

        operating.set(true);

        th = new Thread(this);
        th.start();

    }
    public void termina() throws InterruptedException, SQLException{

        operating.set(false);

        synchronized(q) {
            q.notifyAll();
        }

        th.join();

        q.clear();
        connection.close();
    }

    private void write(List<Neo4JDataFormat> rels){
        try {

            for (Neo4JDataFormat i : rels) {
                Statement st = connection.createStatement();
                st.execute(i.createCommand());
            }
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run(){

        try {
            while(operating.get() || (!q.isEmpty()) ){
                List<Neo4JDataFormat> l = new ArrayList<>();
                synchronized (q) {
                    while (q.isEmpty() && operating.get())
                        q.wait();


                    Iterator<Neo4JDataFormat> it = q.iterator();
                    for(int i = 0; i < this.instruction_size; i++){
                        if(it.hasNext()){
                            l.add(it.next());
                            it.remove();
                        }else {
                            break;
                        }
                    }
                }
                this.write(l);
            }

        }catch (InterruptedException e){
            e.getStackTrace();
        }

    }

}
