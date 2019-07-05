/**
 * Classe de exceções.
 * Quando o contribuinte individual a que tentamos aceder não existe (null).
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180515
 */

public class CINullException extends Exception{
    public CINullException(){
    }

    public CINullException(String msg){
        super(msg);
    }
}
