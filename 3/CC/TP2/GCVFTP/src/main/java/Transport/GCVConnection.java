package Transport;

public final class GCVConnection {
    public static final int port = 8626;
    public static final int stdmtu = 1460; /* 496 bytes*/

    public static final int rate_control_interval = 10000;

    public static final float decrease_factor = 0.6F;
    public static final long connection_receive_ttl = 2000;
    public static final int request_retry_number = 8;
    public static final int request_retry_timeout = 2000;

    public static final int number_of_receive_workers = 2;

    public static final int max_loss_list_size = 164;

    public static final float rrt_factor = 0.125F;//
    public static final float var_rrt_factor = 0.25F;

    public static final int number_of_executor_workers = 4;

    public static final float controlBufferFactor = 0.4F;

    public static final int send_buffer_size = 8192;

    public static final int initial_window_size = 2;

}
