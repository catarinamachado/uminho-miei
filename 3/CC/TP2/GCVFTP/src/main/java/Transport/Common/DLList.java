package Transport.Common;

public class DLList<V> {

    private No<V> current=null;
    private No<V> head=null;
    private No<V> tail=null;

    public DLList(){
    }

    private DLList(No<V> head, No<V> tail){
        this.head = head;
        this.tail = tail;
    }

    public void add(V element ){
        
        if( head == null){
            head = tail = current = new No<>(null, element, null);
            return;
        }

        if( current == null ){
            No<V> me = new No<>(tail, element, null);

            tail.next = me;

            tail = me;
            return;
        }

        No<V> me = new No<>(current.previous, element, current);
        
        current.previous = me;

        current = me;

        if( current.previous == null ){
            head = current;
        }else{
            current.previous.next = me;
        }

    }

    public DLList<V> next(){

        if( current == null )
            current = head;
        else
            current = current.next;

        return this;
    }

    public DLList<V> view(){
        return new DLList<>(this.head, this.tail);
    }

    public DLList<V> previous(){
        if( current == null )
            current = tail;
        else if(current != null)   
            current = current.previous;

        return this;
    }

    public V value(){
        if( current == null ){
            return null;
        }else{
            return current.value;
        }
    }

    public DLList<V> start(){
        current = null;
        return this;
    }

    public void remove(){
        if(current != null){
            
            if(current.previous != null && current.next != null ){
                /* elemento do meio */
                current.previous.next = current.next;
                current.next.previous = current.previous;
                current = current.previous;
                
            }else if( current.previous != null ){
                /* tail */
                tail = current.previous;
                tail.next = null;
                current = tail;

            }else if( current.next != null ){
                /* head */
                head = current.next;
                head.previous = null;
                current = null;

            }else {
                tail = current = head = null;
            }
        }

    }

    public boolean hasNext(){
        if( current == null )
            return (head != null);


        return (current.next!=null);
    }

    public boolean hasPrevious(){

        if( current == null)
            return tail != null;

        return (current.previous!=null);
    }

    public V peek(){
        return head.value;
    }

    public boolean empty(){
        return (head == null ) && (current == null) && (tail == null);
    }

    public boolean singleton(){
        if( head == null)
            return false;
        
        return head.next == null;
    }

    class No<V> {
        No<V> next;
        No<V> previous;
        V value;

        No(No<V> before, V value , No<V> after){
            this.value = value;
            this.next = after;
            this.previous = before;
        }

    }


}