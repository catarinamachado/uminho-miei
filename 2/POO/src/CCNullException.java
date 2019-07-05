/**
 * Classe de exceções.
 * Quando o contribuinte coletivo a que tentamos aceder não existe (null).
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180515
 */

public class CCNullException extends Exception{
    public CCNullException(){
    }

    public CCNullException(String msg){
        super(msg);
    }
}
