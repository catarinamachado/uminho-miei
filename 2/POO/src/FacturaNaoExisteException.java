/**
 * Classe de exceções.
 * Factura não existe
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180522
 */

public class FacturaNaoExisteException extends Exception{
    public FacturaNaoExisteException(String msg){
        super(msg);
    }
}