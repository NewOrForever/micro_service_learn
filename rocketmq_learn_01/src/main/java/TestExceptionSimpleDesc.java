import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.util.Assert;

/**
 * ClassName:TestExceptionSimpleDesc
 * Package:com.example.rocketmq
 * Description:
 *
 * @Date:2024/7/4 14:04
 * @Author:qs@1.com
 */
@Slf4j
public class TestExceptionSimpleDesc {
    public static void main(String[] args) {
        try {
            Assert.state(false, "非法的访问权限");
        } catch (Exception e) {
            System.out.println(RemotingHelper.exceptionSimpleDesc(e));
            System.out.println("-----------------------------------------------------------");
            System.out.println(exceptionFullDesc(e));
            System.out.println("-----------------------------------------------------------");
            e.printStackTrace();
            System.out.println("-----------------------------------------------------------");
            log.error("error", e);
        }
    }

    public static String exceptionFullDesc(final Throwable e) {
        StringBuilder sb = new StringBuilder();
        if (e != null) {
            sb.append(e.toString()).append("\r\n");

            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                for (StackTraceElement element : stackTrace) {
                    sb.append(element.toString()).append("\r\n");
                }
            }
        }

        return sb.toString();
    }
}
