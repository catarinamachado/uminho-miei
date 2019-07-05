/**
 * Classe de exceções.
 * Quando o conteúdo do CSV não corresponde ao formato pedido pela aplicação.
 *
 * @author A81047
 * @author A34900
 * @author A82339
 * @version 20180522
 */

public class CSVformatIncorretException extends Exception {
    public CSVformatIncorretException() {
    }

    public CSVformatIncorretException(String message) {
        super(message);
    }
}
